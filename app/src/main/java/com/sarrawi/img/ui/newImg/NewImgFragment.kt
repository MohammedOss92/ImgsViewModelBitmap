package com.sarrawi.img.ui.newImg

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.img.Api.ApiService
import com.sarrawi.img.R
import com.sarrawi.img.adapter.AdItemDecoration
import com.sarrawi.img.adapter.AdapterRecyLin
import com.sarrawi.img.adapter.ImgAdapter
import com.sarrawi.img.databinding.FragmentFourBinding
import com.sarrawi.img.databinding.FragmentNewImgBinding
import com.sarrawi.img.db.repository.FavoriteImageRepository
import com.sarrawi.img.db.repository.ImgRepository
import com.sarrawi.img.db.viewModel.FavoriteImagesViewModel
import com.sarrawi.img.db.viewModel.Imgs_ViewModel
import com.sarrawi.img.db.viewModel.ViewModelFactory
import com.sarrawi.img.db.viewModel.ViewModelFactory2
import com.sarrawi.img.model.FavoriteImage
import com.sarrawi.img.model.ImgsModel
import com.sarrawi.img.paging.PagingAdapterImage
import com.sarrawi.img.paging.PagingAdapterImageLinear
import com.sarrawi.img.ui.frag.ThirdFragmentDirections
import com.sarrawi.img.utils.DataStatus
import kotlinx.coroutines.launch


class NewImgFragment : Fragment() {


    private lateinit var _binding: FragmentNewImgBinding

    private val binding get() = _binding

    private val retrofitService = ApiService.provideRetrofitInstance()
    private val mainRepository by lazy { ImgRepository(retrofitService,requireActivity().application) }
    private val imgsViewModel: Imgs_ViewModel by viewModels {
        ViewModelFactory(requireContext(), mainRepository)
    }
    private val imgAdapter by lazy { ImgAdapter(requireActivity()) }
    private val imgAdaptert by lazy { PagingAdapterImage(requireActivity()) }
    private var ID = -1


    lateinit var image_url:String
    private var recyclerViewState: Parcelable? = null

    private val favoriteImageRepository by lazy { FavoriteImageRepository(requireActivity().application) }
    private val favoriteImagesViewModel: FavoriteImagesViewModel by viewModels {
        ViewModelFactory2(favoriteImageRepository)
    }

    private val a by lazy {  FavoriteImageRepository(requireActivity().application) }
    private val imgsffav: FavoriteImagesViewModel by viewModels {
        ViewModelFactory2(a)
    }

    private var currentItemId = -1
    var clickCount = 0
    var mInterstitialAd: InterstitialAd?=null
    private lateinit var adView: AdView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentNewImgBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgsViewModel.isConnected.observe(requireActivity()) { isConnected ->
            if (isConnected) {
//                setUpRvth()
                setUpRv()
                adapterOnClick()
                imgAdapter.updateInternetStatus(isConnected)
                binding.lyNoInternet.visibility = View.GONE


            } else {
//                binding.progressBar.visibility = View.GONE
                binding.lyNoInternet.visibility = View.VISIBLE
                imgAdapter.updateInternetStatus(isConnected)
            }
        }
        InterstitialAd_fun()
//        setUpRvth()
        setUpRv()
        adapterOnClick()

        imgsViewModel.checkNetworkConnection(requireContext())
    }


    private fun setUpRv() {
        if (isAdded) {
            imgsViewModel.getAllImgsNewViewModel().observe(viewLifecycleOwner) { imgs ->
                imgAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
                val adItemDecoration = AdItemDecoration(adInterval = 4, adHeight = 50) // تحديد الفاصل بين الإعلانات وارتفاع الإعلان
                binding.rvImgNewcont.addItemDecoration(adItemDecoration)
                if (imgs.isEmpty()) {
                    // قم بتحميل البيانات من الخادم إذا كانت القائمة فارغة
                    imgsViewModel.getAllImgsNewViewModel()
                } else {
                    // إذا كانت هناك بيانات، قم بتحديث القائمة في الـ RecyclerView

                    // هنا قم بالحصول على البيانات المفضلة المحفوظة محليًا من ViewModel
                    favoriteImagesViewModel.getAllFava().observe(viewLifecycleOwner) { favoriteImages ->
                        val allImages: List<ImgsModel> = imgs

                        for (image in allImages) {
                            val isFavorite = favoriteImages.any { it.id == image.id } // تحقق مما إذا كانت الصورة مفضلة
                            image.is_fav = isFavorite // قم بتحديث حالة الصورة
                        }

                        imgAdapter.img_list = allImages

                        if (binding.rvImgNewcont.adapter == null) {
                            binding.rvImgNewcont.layoutManager = GridLayoutManager(requireContext(),2)
                            binding.rvImgNewcont.adapter = imgAdapter
                        } else {
                            imgAdapter.notifyDataSetChanged()
                        }
                        if (currentItemId != -1) {
                            binding.rvImgNewcont.scrollToPosition(currentItemId)
                        }


                    }

                }

                imgAdapter.onItemClick = { _, imgModel: ImgsModel,currentItemId ->
                    if (imgsViewModel.isConnected.value == true) {

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

                        val directions = NewImgFragmentDirections.actionNewImgFragmentToNewImgFragLinear(ID, currentItemId,imgModel.image_url)
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






    fun adapterOnClick() {
        imgAdapter.onItemClick = { _, imgModel: ImgsModel, currentItemId ->
            if (imgsViewModel.isConnected.value == true) {

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


                val directions = NewImgFragmentDirections.actionNewImgFragmentToNewImgFragLinear(
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


            imgAdapter.onbtnClick = { it: ImgsModel, i: Int ->
                if (it.is_fav) {
                    // إذا كانت الصورة مفضلة، قم بإلغاء الإعجاب بها
                    it.is_fav = false
                    imgsffav.removeFavoriteImage(
                        FavoriteImage(
                            it.id!!,
                            it.ID_Type_id,
                            it.new_img,
                            it.image_url
                        )
                    )
                    imgsffav.updateImages()
                    imgsffav.getFavByIDModels(it.id!!)
                    val snackbar = Snackbar.make(view!!, "تم الحذف", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                } else {
                    // إذا لم تكن الصورة مفضلة، قم بإضافتها للمفضلة
                    it.is_fav = true
                    imgsffav.addFavoriteImage(
                        FavoriteImage(
                            it.id!!,
                            it.ID_Type_id,
                            it.new_img,
                            it.image_url
                        )
                    )
                    imgsffav.updateImages()
                    imgsffav.getFavByIDModels(it.id!!)
                    val snackbar = Snackbar.make(view!!, "تم الإضافة", Snackbar.LENGTH_SHORT)
                    snackbar.show()
                }
                // تحقق من قيمة it.is_fav
                println("it.is_fav: ${it.is_fav}")
                // تحديث RecyclerView Adapter
                imgAdaptert.notifyDataSetChanged()
            }
        }

    }

    fun InterstitialAd_fun() {
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
}