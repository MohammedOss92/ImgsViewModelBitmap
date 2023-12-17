package com.sarrawi.img.ui.fragFav

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
import androidx.core.app.Person.fromBundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.img.adapter.FavAdapterLinRecy
import com.sarrawi.img.adapter.FavAdapterPager
import com.sarrawi.img.adapter.ViewPagerAdapter
import com.sarrawi.img.databinding.FragmentFavoritePagerBinding
import com.sarrawi.img.databinding.FragmentFavoriteRecyBinding
import com.sarrawi.img.databinding.FragmentFourBinding
import com.sarrawi.img.db.repository.FavoriteImageRepository
import com.sarrawi.img.db.viewModel.FavoriteImagesViewModel
import com.sarrawi.img.db.viewModel.ViewModelFactory2
import com.sarrawi.img.model.FavoriteImage
import com.sarrawi.img.model.ImgsModel
import kotlinx.coroutines.launch
import java.io.File


class FavoritePagerFrag : Fragment() {
    private lateinit var _binding: FragmentFavoritePagerBinding

    private val binding get() = _binding
    private val favoriteImageRepository by lazy { FavoriteImageRepository(requireActivity().application) }
    private val favoriteImagesViewModel: FavoriteImagesViewModel by viewModels {
        ViewModelFactory2(favoriteImageRepository)
    }





    private val adapterpager by lazy {
        FavAdapterPager(requireActivity())
    }


    private var ID = -1
    private  var currentItemId:Int=0
    var imgsmodel: ImgsModel? = null // تهيئة المتغير كاختياري مع قيمة ابتدائية

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ID = FavoritePagerFragArgs.fromBundle(requireArguments()).id
        currentItemId = FavoritePagerFragArgs.fromBundle(requireArguments()).currentItemId

        imgsmodel?.image_url = FavoritePagerFragArgs.fromBundle(requireArguments()).imageUrl
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritePagerBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRv()
        adapterOnClick()
//        adapterpager.onSaveImageClickListenerfp = object : FavAdapterPager.OnSaveImageClickListenerfavp {
//            override fun onSaveImageClickfp(position: Int) {
//                saveImageToExternalStorage(position)
//            }
//        }

    }

    private fun setUpRv() {
        if (isAdded) {
            favoriteImagesViewModel.getAllFava().observe(viewLifecycleOwner) { imgs ->
                // تم استدعاء الدالة فقط إذا كان ال Fragment متصلاً بنشاط

                if (imgs != null) {
                    adapterpager.fav_img_list_pager=imgs
                    binding.pagerFav.adapter =adapterpager
                    binding.pagerFav.setCurrentItem(currentItemId,false) // set for selected item
                    adapterpager.notifyDataSetChanged()

                }

                else {
                    // No data
                }

            }}

        }

    fun saveImageToExternalStorage(position: Int) {
        val item = adapterpager.fav_img_list_pager.getOrNull(position)



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

    fun adapterOnClick(){

        adapterpager.onbtnclick ={
            it.is_fav = false
            favoriteImagesViewModel.updateImages()
            favoriteImagesViewModel.removeFavoriteImage(FavoriteImage(it.id!!, it.ID_Type_id, it.new_img, it.image_url))

            val snackbar = Snackbar.make(view!!, "تم الحذف", Snackbar.LENGTH_SHORT)
            snackbar.show()
        }

    }


    }


//    private fun setUpViewPager() =
//        favoriteImagesViewModel.viewModelScope.launch {
//            favoriteImagesViewModel.getAllFav().observe(requireActivity()) { imgs ->
//                // print data
//                if (imgs != null) {
//                    viewPagerAdapter.img_list=imgs
//                    binding.viewpager.adapter =viewPagerAdapter
//                    binding.viewpager.setCurrentItem(currentItemId,false) // set for selected item
//                    viewPagerAdapter.notifyDataSetChanged()
//
//                }
//
//                else {
//                    // No data
//                }
//
//            }}

