package com.sarrawi.img.ui.frag

import android.content.ContentValues
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
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.img.Api.ApiService
import com.sarrawi.img.adapter.FavAdapterPager
import com.sarrawi.img.adapter.ViewPagerAdapter
import com.sarrawi.img.databinding.FragmentPagerImgBinding
import com.sarrawi.img.db.repository.FavoriteImageRepository
import com.sarrawi.img.db.repository.ImgRepository
import com.sarrawi.img.db.viewModel.FavoriteImagesViewModel
import com.sarrawi.img.db.viewModel.Imgs_ViewModel
import com.sarrawi.img.db.viewModel.ViewModelFactory
import com.sarrawi.img.db.viewModel.ViewModelFactory2
import com.sarrawi.img.model.FavoriteImage
import com.sarrawi.img.model.ImgsModel
import kotlinx.coroutines.launch
import java.io.File


class PagerFragmentImg : Fragment() {

    lateinit var _binding: FragmentPagerImgBinding
    private val binding get() = _binding

    private val retrofitService = ApiService.provideRetrofitInstance()

    private val mainRepository by lazy {
        ImgRepository(
            retrofitService,
            requireActivity().application
        )
    }

    private val imgsViewmodel: Imgs_ViewModel by viewModels {
        ViewModelFactory(requireContext(), mainRepository)
    }

    private val a by lazy { FavoriteImageRepository(requireActivity().application) }

    private val imgsffav: FavoriteImagesViewModel by viewModels {
        ViewModelFactory2(a)
    }

    private val adapterpager by lazy {
        ViewPagerAdapter(requireActivity())
    }

    private val favoriteImageRepository by lazy { FavoriteImageRepository(requireActivity().application) }
    private val favoriteImagesViewModel: FavoriteImagesViewModel by viewModels {
        ViewModelFactory2(favoriteImageRepository)
    }

    private var currentItemId = -1
    private var ID = -1
    var imgsmodel: ImgsModel? = null

    private lateinit var adView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ID = PagerFragmentImgArgs.fromBundle(requireArguments()).id
        currentItemId = PagerFragmentImgArgs.fromBundle(requireArguments()).currentItemId

        imgsmodel?.image_url = PagerFragmentImgArgs.fromBundle(requireArguments()).imageUrl
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPagerImgBinding.inflate(inflater, container, false)




        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgsViewmodel.isConnected.observe(requireActivity()) { isConnected ->

            if (isConnected) {
//                  setUpViewPager()

//                binding.lyNoInternet.visibility = View.GONE
//                setUpPager()
                adapterpager.updateInternetStatus(isConnected)
            } else {
//                     binding.progressBar.visibility = View.GONE
//                binding.lyNoInternet.visibility = View.VISIBLE
                adapterpager.updateInternetStatus(isConnected)

            }
        }
        imgsViewmodel.checkNetworkConnection(requireContext())

        setUpViewPager()
        adapterOnClick()
//        binding.adView.loadAd(AdRequest.Builder().build())
//        adapterpager.onSaveImageClickListenerp = object : ViewPagerAdapter.OnSaveImageClickListenerp {
//            override fun onSaveImageClickp(position: Int) {
//                saveImageToExternalStorage(position)
//            }
//        }
    }

    private fun setUpViewPager() =
        imgsViewmodel.viewModelScope.launch {
            imgsViewmodel.getAllImgsViewModel(ID).observe(requireActivity()) { imgs ->
                // print data
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

                        if (imgs != null) {
                            adapterpager.img_list_Pager=imgs
                            binding.pagerimg.adapter =adapterpager
                            binding.pagerimg.setCurrentItem(currentItemId,false) // set for selected item
                            adapterpager.notifyDataSetChanged()

                        }

                        else {
                            // No data
                        }

                    }
                }


            }}


//    private fun setUpPager() {
//        if (isAdded) {
//            imgsViewmodel.getAllImgsViewModel(ID).observe(viewLifecycleOwner) { imgs ->
//
//
//                if (imgs.isEmpty()) {
//                    // قم بتحميل البيانات من الخادم إذا كانت القائمة فارغة
//                    imgsViewmodel.getAllImgsViewModel(ID)
//                } else {
//                    // إذا كانت هناك بيانات، قم بتحديث القائمة في الـ RecyclerView
//
//                    // هنا قم بالحصول على البيانات المفضلة المحفوظة محليًا من ViewModel
//                    favoriteImagesViewModel.getAllFav()
//                        .observe(viewLifecycleOwner) { favoriteImages ->
//                            val allImages: List<ImgsModel> = imgs
//
//                            for (image in allImages) {
//                                val isFavorite =
//                                    favoriteImages.any { it.id == image.id } // تحقق مما إذا كانت الصورة مفضلة
//                                image.is_fav = isFavorite // قم بتحديث حالة الصورة
//                            }
//
//                            adapterpager.img_list_Pager = allImages
//
//                            if (imgs != null) {
////                                adapterpager.img_list_Pager=imgs
//                                binding.pagerimg.adapter =adapterpager
//                                binding.pagerimg.setCurrentItem(currentItemId,false) // set for selected item
//                                binding.pagerimg.orientation=ViewPager2.ORIENTATION_HORIZONTAL
//                                adapterpager.notifyDataSetChanged()
//
//                            }
//
//                            else {
//                                // No data
//                                adapterpager.notifyDataSetChanged()
//                            }
//
//
////                            if (currentItemId != -1) {
////                                binding.pagerimg.scrollToPosition(currentItemId)
////                            }
//
//                        }
//                }
//
//
//            }
//        }
//    }

    fun adapterOnClick() {
        adapterpager.onbtnClick = { it: ImgsModel, i: Int ->
            val fav = FavoriteImage(it.id!!, it.ID_Type_id, it.new_img, it.image_url)

            println("it.is_fav: ${it.is_fav}")
            if (it.is_fav) {
                it.is_fav = false
                imgsffav.removeFavoriteImage(fav)

                imgsffav.updateImages()
                val snackbar = Snackbar.make(view!!, "تم الحذف", Snackbar.LENGTH_SHORT)
                snackbar.show()
//                setUpViewPager()

                adapterpager.notifyDataSetChanged()
                println("it.is_fav: ${it.is_fav}")
                currentItemId = i
//                if (currentItemId != -1) {
//                    binding.rvImgCont.scrollToPosition(currentItemId)
//                }
            } else {
                it.is_fav = true
                imgsffav.addFavoriteImage(fav)

                imgsffav.updateImages()
                val snackbar = Snackbar.make(view!!, "تم الاضافة", Snackbar.LENGTH_SHORT)
                snackbar.show()
//                setUpViewPager()

                adapterpager.notifyDataSetChanged()
                println("it.is_fav: ${it.is_fav}")
                currentItemId = i

            }
            // تحقق من قيمة it.is_fav
            println("it.is_fav: ${it.is_fav}")
//            setUpViewPager()

            adapterpager.notifyDataSetChanged()
            println("it.is_fav: ${it.is_fav}")

        }



    }

    fun saveImageToExternalStorage(position: Int) {
        val item = adapterpager.img_list_Pager.getOrNull(position)



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



}