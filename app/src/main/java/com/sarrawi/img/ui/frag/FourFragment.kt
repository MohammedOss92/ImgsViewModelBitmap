package com.sarrawi.img.ui.frag

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.appsearch.SetSchemaRequest.READ_EXTERNAL_STORAGE
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.webkit.MimeTypeMap
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.img.Api.ApiService
import com.sarrawi.img.R
import com.sarrawi.img.adapter.AdapterRecyLin
import com.sarrawi.img.databinding.FragmentFourBinding
import com.sarrawi.img.db.repository.FavoriteImageRepository
import com.sarrawi.img.db.repository.ImgRepository
import com.sarrawi.img.db.viewModel.*
import com.sarrawi.img.model.FavoriteImage
import com.sarrawi.img.model.ImgsModel
import java.io.File
import java.io.FileOutputStream
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.sarrawi.img.paging.PagingAdapterImageLinear


class FourFragment : Fragment() {

    private lateinit var _binding: FragmentFourBinding

    private val binding get() = _binding
    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1 // تعريف الثابت هنا
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 2 // تعريف الثابت هنا

    private val retrofitService = ApiService.provideRetrofitInstance()

    private val mainRepository by lazy {
        ImgRepository(
            retrofitService,
            requireActivity().application
        )
    }
    private val a by lazy { FavoriteImageRepository(requireActivity().application) }


    private val imgsViewmodel: Imgs_ViewModel by viewModels {
        ViewModelFactory(requireContext(), mainRepository)
    }


    private val imgsffav: FavoriteImagesViewModel by viewModels {
        ViewModelFactory2(a)
    }


    private val favoriteImageRepository by lazy { FavoriteImageRepository(requireActivity().application) }
    private val favoriteImagesViewModel: FavoriteImagesViewModel by viewModels {
        ViewModelFactory2(favoriteImageRepository)
    }


    private val adapterLinRecy by lazy { AdapterRecyLin(requireActivity()) }
    private val pagingadapterLinRecy by lazy { PagingAdapterImageLinear(requireActivity()) }

    var idd = -1
    private var ID_Type_id = -1
    private var ID = -1
    private var currentItemId = -1
    private var newimage: Int = -1
    private lateinit var imageUrl: String
    var imgsmodel: ImgsModel? = null // تهيئة المتغير كاختياري مع قيمة ابتدائية
    var clickCount = 0
    var mInterstitialAd: InterstitialAd?=null
    private lateinit var adView: AdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFourBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        ID = FourFragmentArgs.fromBundle(requireArguments()).id
        currentItemId = FourFragmentArgs.fromBundle(requireArguments()).currentItemId

        imgsmodel?.image_url = FourFragmentArgs.fromBundle(requireArguments()).imageUrl


    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //
        setHasOptionsMenu(true)
        menu_item()
//        setUpRv()
//        adapterOnClick()
        imgsffav.updateImages()
        // Live Connected
        imgsViewmodel.isConnected.observe(requireActivity()) { isConnected ->

            if (isConnected) {
//                  setUpViewPager()
                InterstitialAd_fun()
                setUpRv()
                adapterOnClick2()
                adapterLinRecy.updateInternetStatus(isConnected)
                binding.lyNoInternet.visibility = View.GONE

            } else {
//                     binding.progressBar.visibility = View.GONE
                binding.lyNoInternet.visibility = View.VISIBLE
                adapterLinRecy.updateInternetStatus(isConnected)

            }
        }
        imgsViewmodel.checkNetworkConnection(requireContext())

//        adapterLinRecy.onSaveImageClickListener = object : AdapterRecyLin.OnSaveImageClickListener {
//            override fun onSaveImageClick(position: Int) {
//                saveImageToExternalStorage(position)
//            }
//        }



        // التحقق من إذن الكتابة على التخزين الخارجي
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // إذا لم يكن لديك الإذن، قم بطلبه
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_EXTERNAL_STORAGE_REQUEST_CODE)
        } else {
            // تم منح الإذن، قم بإنشاء المجلد مباشرة
            createDirectory()
        }

    }



    private fun setUpRv() {
        if (isAdded) {
            imgsViewmodel.getAllImgsViewModel(ID).observe(viewLifecycleOwner) { imgs ->
                adapterLinRecy.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW

                if (imgs.isEmpty()) {
                    // قم بتحميل البيانات من الخادم إذا كانت القائمة فارغة
                    imgsViewmodel.getAllImgsViewModel(ID)
                } else {
                    // إذا كانت هناك بيانات، قم بتحديث القائمة في الـ RecyclerView

                    // هنا قم بالحصول على البيانات المفضلة المحفوظة محليًا من ViewModel
                    favoriteImagesViewModel.getAllFava().observe(viewLifecycleOwner) { favoriteImages ->
                        val allImages: List<ImgsModel> = imgs

                        for (image in allImages) {
                            val isFavorite = favoriteImages.any { it.id == image.id } // تحقق مما إذا كانت الصورة مفضلة
                            image.is_fav = isFavorite // قم بتحديث حالة الصورة
                        }

                        adapterLinRecy.img_list = allImages

                        if (binding.rvImgCont.adapter == null) {
                            binding.rvImgCont.layoutManager = LinearLayoutManager(requireContext())
                            binding.rvImgCont.adapter = adapterLinRecy
                        } else {
                            adapterLinRecy.notifyDataSetChanged()
                        }
                        if (currentItemId != -1) {
                            binding.rvImgCont.scrollToPosition(currentItemId)
                        }

                    }
                }

                adapterLinRecy.onItemClick = { _, imgModel: ImgsModel,currentItemId ->
                    if (imgsViewmodel.isConnected.value == true) {

//                        clickCount++
//                        if (clickCount >= 2) {
//// بمجرد أن يصل clickCount إلى 2، اعرض الإعلان
//                            if (mInterstitialAd != null) {
//                                mInterstitialAd?.show(requireActivity())
//                            } else {
//                                Log.d("TAG", "The interstitial ad wasn't ready yet.")
//                            }
//                            clickCount = 0 // اعيد قيمة المتغير clickCount إلى الصفر بعد عرض الإعلان
//
//                        }

                        val directions = FourFragmentDirections.actionFourFragmentToPagerFragmentImg(ID,currentItemId,imgModel.image_url)
                        findNavController().navigate(directions)

                    } else {
                        val snackbar = Snackbar.make(
                            requireView(),
                            "لا يوجد اتصال بالإنترنت",
                            Snackbar.LENGTH_SHORT
                        )
                        snackbar.show()
                    }
                }
            }
        }
    }




    fun adapterOnClick2() {
        adapterLinRecy.onbtnClick = { it: ImgsModel, i: Int ->
            val fav = FavoriteImage(it.id!!, it.ID_Type_id, it.new_img, it.image_url)

            println("it.is_fav: ${it.is_fav}")
            if (it.is_fav) {
                it.is_fav = false
                imgsffav.removeFavoriteImage(fav)

                imgsffav.updateImages()
                val snackbar = Snackbar.make(view!!, "تم الحذف", Snackbar.LENGTH_SHORT)
                snackbar.show()
//                      setUpViewPager()

                adapterLinRecy.notifyDataSetChanged()
                println("it.is_fav: ${it.is_fav}")
                currentItemId = i
                if (currentItemId != -1) {
                    binding.rvImgCont.scrollToPosition(currentItemId)
                }
            } else {
                it.is_fav = true
                imgsffav.addFavoriteImage(fav)

                imgsffav.updateImages()
                val snackbar = Snackbar.make(view!!, "تم الاضافة", Snackbar.LENGTH_SHORT)
                snackbar.show()
//                      setUpViewPager()

                adapterLinRecy.notifyDataSetChanged()
                println("it.is_fav: ${it.is_fav}")
                currentItemId = i
                if (currentItemId != -1) {
                    binding.rvImgCont.scrollToPosition(currentItemId)
                }
            }
            // تحقق من قيمة it.is_fav
            println("it.is_fav: ${it.is_fav}")
//                  setUpViewPager()

            adapterLinRecy.notifyDataSetChanged()
            println("it.is_fav: ${it.is_fav}")
            if (currentItemId != -1) {
                binding.rvImgCont.scrollToPosition(currentItemId)
            }
        }

        adapterLinRecy.onItemClick = { _, imgModel: ImgsModel, currentItemId ->

            if (imgsViewmodel.isConnected.value == true) {
                clickCount++
                if (clickCount >= 2) {
// بمجرد أن يصل clickCount إلى 2، اعرض الإعلان
                    if (mInterstitialAd != null) {
                        mInterstitialAd?.show(requireActivity())
                    } else {
                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
                    }
                    clickCount = 0 // اعيد قيمة المتغير clickCount إلى الصفر بعد عرض الإعلان

                }
                val directions = FourFragmentDirections.actionFourFragmentToPagerFragmentImg(
                    ID,
                    currentItemId,
                    imgModel.image_url
                )
                findNavController().navigate(directions)
            } else {
                val snackbar = Snackbar.make(
                    requireView(),
                    "لا يوجد اتصال بالإنترنت",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            }



        }

    }


    fun InterstitialAd_fun (){


        MobileAds.initialize(requireActivity()) { initializationStatus ->
            // do nothing on initialization complete
        }

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireActivity(),
            "ca-app-pub-1895204889916566/2401606550",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until an ad is loaded.
                    mInterstitialAd = interstitialAd
                    Log.i("onAdLoadedL", "onAdLoaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.d("onAdLoadedF", loadAdError.toString())
                    mInterstitialAd = null
                }
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_fragment_four, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
//                    R.id.action_option1 -> {
//                        // اتخاذ إجراء عند اختيار Option 1
//                        true
//                    }
//                    R.id.action_option2 -> {
//                        // اتخاذ إجراء عند اختيار Option 2
//                        true
//                    }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun menu_item() {
        // The usage of an interface lets you inject your own implementation

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                // menuInflater.inflate(R.menu.menu_zeker, menu) // هنا لا داعي لتكرار هذا السطر
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when (menuItem.itemId) {
                    R.id.aa -> {

                        return true
                    }

                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }








    fun getCompressFormat(imageUrl: String): Bitmap.CompressFormat? {
        val extension = MimeTypeMap.getFileExtensionFromUrl(imageUrl)
        return when (extension?.toLowerCase()) {
            "jpg", "jpeg" -> Bitmap.CompressFormat.JPEG
            "png" -> Bitmap.CompressFormat.PNG
            // يمكنك إضافة المزيد من الامتدادات هنا
            else -> null // ارجع قيمة null لعدم تغيير الصيغة
        }
    }




private fun createDirectory() {
    val dir = File(Environment.getExternalStorageDirectory(), "MyPics")
    if (!dir.exists()) {
        dir.mkdirs()
    }
}

override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when (requestCode) {
        WRITE_EXTERNAL_STORAGE_REQUEST_CODE -> {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // تم منح الإذن، قم بإنشاء المجلد
                createDirectory()
            }
        }
    }
}

    fun saveImageToExternalStorage(position: Int) {
        val item = adapterLinRecy.img_list.getOrNull(position)
        if (item != null) {
            val imageUri = Uri.parse(item.image_url) // تحديد URI للصورة من URL
            Glide.with(requireContext())
                .asBitmap()
                .load(imageUri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        try {
                            val folderName = "MyPics"
                            val displayName = String.format("%d.png", System.currentTimeMillis())
                            val mimeType = "image/png"
                            // تمثيل المعلومات الخاصة بالصورة
                            val imageDetails = ContentValues().apply {
                                put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
                                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
                                put(MediaStore.Images.Media.WIDTH, resource.width)
                                put(MediaStore.Images.Media.HEIGHT, resource.height)
                                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + folderName)
                            }
                            // حفظ الصورة باستخدام MediaStore
                            val contentResolver = requireContext().contentResolver
                            val imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageDetails)
                            if (imageUri != null) {
                                val outputStream = contentResolver.openOutputStream(imageUri)
                                resource.compress(getCompressFormat(item.image_url), 100, outputStream)
                                outputStream?.close()
                                showSaveSuccessMessage()
                            } else {
                                showSaveErrorMessage()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.e("MyApp", "An error occurred: ${e.message}")
                            showSaveErrorMessage()
                            Log.e("MyApp", "An error occurred: ${e.message}")
                        }
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {
                        // يمكنك التعامل مع هذا الحالة إذا كنت بحاجة إلى تنظيف أي موارد
                    }
                })
        }

    }
    private fun showSaveSuccessMessage() {
        Snackbar.make(requireView(), "تم الحفظ بنجاح", Snackbar.LENGTH_SHORT).show()
    }

    private fun showSaveErrorMessage() {
        Snackbar.make(requireView(), "حدث خطأ أثناء الحفظ", Snackbar.LENGTH_SHORT).show()
    }

//    fun saveImageToExternalStorage(position: Int) {
//        val item = pagingadapterLinRecy.snapshot().items.getOrNull(position)
//
//        if (item != null) {
//            val imageUri = Uri.parse(item.image_url) // تحديد URI للصورة من URL
//
//            Glide.with(requireContext())
//                .asBitmap()
//                .load(imageUri)
//                .into(object : CustomTarget<Bitmap>() {
//                    override fun onResourceReady(
//                        resource: Bitmap,
//                        transition: Transition<in Bitmap>?
//                    ) {
//                        try {
//                            val folderName = "MyPics"
//                            val displayName = String.format("%d.png", System.currentTimeMillis())
//                            val mimeType = "image/png"
//
//                            // تمثيل المعلومات الخاصة بالصورة
//                            val imageDetails = ContentValues().apply {
//                                put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
//                                put(MediaStore.Images.Media.MIME_TYPE, mimeType)
//                                put(MediaStore.Images.Media.WIDTH, resource.width)
//                                put(MediaStore.Images.Media.HEIGHT, resource.height)
//                                put(
//                                    MediaStore.Images.Media.RELATIVE_PATH,
//                                    Environment.DIRECTORY_PICTURES + File.separator + folderName
//                                )
//                            }
//
//                            // حفظ الصورة باستخدام MediaStore
//                            val contentResolver = requireContext().contentResolver
//                            val imageUri =
//                                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageDetails)
//
//                            if (imageUri != null) {
//                                val outputStream = contentResolver.openOutputStream(imageUri)
//                                resource.compress(getCompressFormat(item.image_url), 100, outputStream)
//                                outputStream?.close()
//                                showSaveSuccessMessage()
//                            } else {
//                                showSaveErrorMessage()
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                            Log.e("MyApp", "An error occurred: ${e.message}")
//                            showSaveErrorMessage()
//                        }
//                    }
//
//                    override fun onLoadCleared(placeholder: Drawable?) {
//                        // يمكنك التعامل مع هذا الحالة إذا كنت بحاجة إلى تنظيف أي موارد
//                    }
//                })
//        }
//    }



}




