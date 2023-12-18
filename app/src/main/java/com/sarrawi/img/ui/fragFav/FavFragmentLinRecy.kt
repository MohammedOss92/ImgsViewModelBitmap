package com.sarrawi.img.ui.fragFav

import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.img.adapter.AdapterRecyLin
import com.sarrawi.img.adapter.FavAdapterLinRecy
import com.sarrawi.img.databinding.FragmentFavLinRecyBinding
import com.sarrawi.img.db.repository.FavoriteImageRepository
import com.sarrawi.img.db.viewModel.FavoriteImagesViewModel
import com.sarrawi.img.db.viewModel.ViewModelFactory2
import com.sarrawi.img.model.FavoriteImage
import com.sarrawi.img.model.ImgsModel
import com.sarrawi.img.paging.FavPagingAdapterImageLinear
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class FavFragmentLinRecy : Fragment() {
    private lateinit var _binding: FragmentFavLinRecyBinding

    private val binding get() = _binding

    private val a by lazy {  FavoriteImageRepository(requireActivity().application) }

    private val imgsffav: FavoriteImagesViewModel by viewModels {
        ViewModelFactory2(a)
    }
    private val favAdapterLinRecy by lazy { FavAdapterLinRecy(requireActivity()) }
    private val favPagingAdapterImageLinear by lazy { FavPagingAdapterImageLinear(requireActivity()) }

    private val favoriteImageRepository by lazy { FavoriteImageRepository(requireActivity().application) }
    private val favoriteImagesViewModel: FavoriteImagesViewModel by viewModels {
        ViewModelFactory2(favoriteImageRepository)
    }

    private var ID = -1
    var idd = -1
    private var ID_Type_id = -1
    private var currentItemId = -1
    private  var newimage:Int= -1
    private lateinit var imageUrl : String
    var imgsmodel: ImgsModel? = null // تهيئة المتغير كاختياري مع قيمة ابتدائية
    var mInterstitialAd: InterstitialAd?=null
    var clickCount = 0
    private val WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1 // تعريف الثابت هنا
    private val READ_EXTERNAL_STORAGE_REQUEST_CODE = 2 // تعريف الثابت هنا

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ID = FavFragmentLinRecyArgs.fromBundle(requireArguments()).id
        currentItemId = FavFragmentLinRecyArgs.fromBundle(requireArguments()).currentItemId

        imgsmodel?.image_url = FavFragmentLinRecyArgs.fromBundle(requireArguments()).imageUrl
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFavLinRecyBinding.inflate(inflater,container,false)
        return  _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        InterstitialAd_fun ()
        setUpRv()
        adapterOnClick()

//        favAdapterLinRecy.onSaveImageClickListenerfav = object :
//            FavAdapterLinRecy.OnSaveImageClickListenerfav{
//            override fun onSaveImageClick(position: Int) {
//                saveImageToExternalStorage(position)
//            }
//
//        }

    }

    private  fun setUpRv()  {




        favoriteImagesViewModel.getAllFava().observe(viewLifecycleOwner) { listShows ->
            //  msgsAdapter.stateRestorationPolicy=RecyclerView.Adapter.StateRestorationPolicy.ALLOW
            favAdapterLinRecy.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.ALLOW
//            msgsAdapter.msgsModel = listShows
//            binding.rcMsgs.adapter = msgsAdapter
            favAdapterLinRecy.fav_img_list = listShows
            if(binding.recyclerFav.adapter == null){
                binding.recyclerFav.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerFav.adapter = favAdapterLinRecy
            }else{
                favAdapterLinRecy.notifyDataSetChanged()
            }
            Log.e("tessst","enter111")

            if (currentItemId != -1) {
                binding.recyclerFav.scrollToPosition(currentItemId)
            }

        }
    }




    private fun adapterOnClick() {

        favAdapterLinRecy.onbtnclick = {
            it.is_fav = false
            imgsffav.updateImages()
            imgsffav.removeFavoriteImage(FavoriteImage(it.id!!, it.ID_Type_id, it.new_img, it.image_url))

            val snackbar = Snackbar.make(view!!, "تم الحذف", Snackbar.LENGTH_SHORT)
            snackbar.show()
        }

        favAdapterLinRecy.onItemClick={_, favimage: FavoriteImage, currentItemId ->

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

            val directions = FavFragmentLinRecyDirections.actionFavFragmentLinRecyToFavoritePagerFrag(ID, currentItemId,favimage.image_url)
            findNavController().navigate(directions)
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

    fun saveImageToExternalStorage(position: Int) {
        val item = favAdapterLinRecy.fav_img_list.getOrNull(position)

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
                                put(
                                    MediaStore.Images.Media.RELATIVE_PATH,
                                    Environment.DIRECTORY_PICTURES + File.separator + folderName
                                )
                            }

                            // حفظ الصورة باستخدام MediaStore
                            val contentResolver = requireContext().contentResolver
                            val imageUri =
                                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageDetails)

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


}