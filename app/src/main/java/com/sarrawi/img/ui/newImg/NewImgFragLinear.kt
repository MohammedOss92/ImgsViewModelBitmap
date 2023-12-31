package com.sarrawi.img.ui.newImg

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.sarrawi.img.adapter.AdapterRecyLin
import com.sarrawi.img.databinding.FragmentFourBinding
import com.sarrawi.img.databinding.FragmentNewImgFragLinearBinding
import com.sarrawi.img.db.repository.FavoriteImageRepository
import com.sarrawi.img.db.repository.ImgRepository
import com.sarrawi.img.db.viewModel.FavoriteImagesViewModel
import com.sarrawi.img.db.viewModel.Imgs_ViewModel
import com.sarrawi.img.db.viewModel.ViewModelFactory
import com.sarrawi.img.db.viewModel.ViewModelFactory2
import com.sarrawi.img.model.FavoriteImage
import com.sarrawi.img.model.ImgsModel
import com.sarrawi.img.paging.PagingAdapterImageLinear
import com.sarrawi.img.ui.frag.FourFragmentArgs
import java.io.File


class NewImgFragLinear : Fragment() {


    private lateinit var _binding: FragmentNewImgFragLinearBinding

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

        _binding = FragmentNewImgFragLinearBinding.inflate(inflater, container, false)
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

        imgsffav.updateImages()
        // Live Connected
        imgsViewmodel.isConnected.observe(requireActivity()) { isConnected ->

            if (isConnected) {
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


    private fun createDirectory() {
        val dir = File(Environment.getExternalStorageDirectory(), "MyPics")
        if (!dir.exists()) {
            dir.mkdirs()
        }
    }

    private fun setUpRv() {
        if (isAdded) {
            imgsViewmodel.getAllImgsNewViewModel().observe(viewLifecycleOwner) { imgs ->
                adapterLinRecy.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW

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

                        adapterLinRecy.img_list = allImages

                        if (binding.rvImgNewLin.adapter == null) {
                            binding.rvImgNewLin.layoutManager = LinearLayoutManager(requireContext())
                            binding.rvImgNewLin.adapter = adapterLinRecy
                        } else {
                            adapterLinRecy.notifyDataSetChanged()
                        }
                        if (currentItemId != -1) {
                            binding.rvImgNewLin.scrollToPosition(currentItemId)
                        }

                    }
                }

//                adapterLinRecy.onItemClick = { _, imgModel: ImgsModel,currentItemId ->
//                    if (imgsViewmodel.isConnected.value == true) {
//
////                        clickCount++
////                        if (clickCount >= 2) {
////// بمجرد أن يصل clickCount إلى 2، اعرض الإعلان
////                            if (mInterstitialAd != null) {
////                                mInterstitialAd?.show(requireActivity())
////                            } else {
////                                Log.d("TAG", "The interstitial ad wasn't ready yet.")
////                            }
////                            clickCount = 0 // اعيد قيمة المتغير clickCount إلى الصفر بعد عرض الإعلان
////
////                        }
//
//                        val directions = NewImgFragLinearDirections.actionNewImgFragLinearToNewImgFragPager(ID,currentItemId,imgModel.image_url)
//                        findNavController().navigate(directions)
//
//                    } else {
//                        val snackbar = Snackbar.make(
//                            requireView(),
//                            "لا يوجد اتصال بالإنترنت",
//                            Snackbar.LENGTH_SHORT
//                        )
//                        snackbar.show()
//                    }
//                }
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
                    binding.rvImgNewLin.scrollToPosition(currentItemId)
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
                    binding.rvImgNewLin.scrollToPosition(currentItemId)
                }
            }
            // تحقق من قيمة it.is_fav
            println("it.is_fav: ${it.is_fav}")
//                  setUpViewPager()

            adapterLinRecy.notifyDataSetChanged()
            println("it.is_fav: ${it.is_fav}")
            if (currentItemId != -1) {
                binding.rvImgNewLin.scrollToPosition(currentItemId)
            }
        }

//        adapterLinRecy.onItemClick = { _, imgModel: ImgsModel, currentItemId ->
//
//            if (imgsViewmodel.isConnected.value == true) {
//                clickCount++
//                if (clickCount >= 2) {
//// بمجرد أن يصل clickCount إلى 2، اعرض الإعلان
//                    if (mInterstitialAd != null) {
//                        mInterstitialAd?.show(requireActivity())
//                    } else {
//                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
//                    }
//                    clickCount = 0 // اعيد قيمة المتغير clickCount إلى الصفر بعد عرض الإعلان
//
//                }
//                val directions = NewImgFragLinearDirections.actionNewImgFragLinearToNewImgFragPager(
//                    ID,
//                    currentItemId,
//                    imgModel.image_url
//                )
//                findNavController().navigate(directions)
//            } else {
//                val snackbar = Snackbar.make(
//                    requireView(),
//                    "لا يوجد اتصال بالإنترنت",
//                    Snackbar.LENGTH_SHORT
//                )
//                snackbar.show()
//            }
//
//
//
//        }

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

}