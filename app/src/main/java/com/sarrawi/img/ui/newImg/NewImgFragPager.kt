package com.sarrawi.img.ui.newImg

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.img.Api.ApiService
import com.sarrawi.img.R
import com.sarrawi.img.adapter.ViewPagerAdapter
import com.sarrawi.img.databinding.FragmentNewImgFragPagerBinding
import com.sarrawi.img.databinding.FragmentPagerImgBinding
import com.sarrawi.img.db.repository.FavoriteImageRepository
import com.sarrawi.img.db.repository.ImgRepository
import com.sarrawi.img.db.viewModel.FavoriteImagesViewModel
import com.sarrawi.img.db.viewModel.Imgs_ViewModel
import com.sarrawi.img.db.viewModel.ViewModelFactory
import com.sarrawi.img.db.viewModel.ViewModelFactory2
import com.sarrawi.img.model.FavoriteImage
import com.sarrawi.img.model.ImgsModel
import com.sarrawi.img.ui.frag.PagerFragmentImgArgs
import kotlinx.coroutines.launch


class NewImgFragPager : Fragment() {


    lateinit var _binding: FragmentNewImgFragPagerBinding
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
        _binding = FragmentNewImgFragPagerBinding.inflate(inflater, container, false)




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
            imgsViewmodel.getAllImgsNewViewModel().observe(requireActivity()) { imgs ->
                // print data
                if (imgs.isEmpty()) {
                    // قم بتحميل البيانات من الخادم إذا كانت القائمة فارغة
                    imgsViewmodel.getAllImgsNewViewModel()
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
                            binding.pagernewimg.adapter =adapterpager
                            binding.pagernewimg.setCurrentItem(currentItemId,false) // set for selected item
                            adapterpager.notifyDataSetChanged()

                        }

                        else {
                            // No data
                        }

                    }
                }


            }}




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

}