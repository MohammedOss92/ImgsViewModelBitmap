package com.sarrawi.img.ui.fragFav

import android.os.Bundle
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
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.img.adapter.Fav_Adapter
import com.sarrawi.img.databinding.FragmentFavoriteRecyBinding
import com.sarrawi.img.db.repository.FavoriteImageRepository
import com.sarrawi.img.db.viewModel.FavoriteImagesViewModel
import com.sarrawi.img.db.viewModel.ViewModelFactory2
import com.sarrawi.img.model.FavoriteImage
import com.sarrawi.img.paging.Paging_Fav_Adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class FavoriteFragmentRecy : Fragment() {

    private lateinit var _binding: FragmentFavoriteRecyBinding

    private val binding get() = _binding

    private val a by lazy {  FavoriteImageRepository(requireActivity().application) }
    var mInterstitialAd: InterstitialAd?=null
    var clickCount = 0

    private val imgsffav: FavoriteImagesViewModel by viewModels {
        ViewModelFactory2(a)
    }
    private val favAdapter by lazy { Fav_Adapter(requireActivity()) }
    private val pagingfavAdapter by lazy { Paging_Fav_Adapter(requireActivity()) }

    private val favoriteImageRepository by lazy { FavoriteImageRepository(requireActivity().application) }
    private val favoriteImagesViewModel: FavoriteImagesViewModel by viewModels {
        ViewModelFactory2(favoriteImageRepository)
    }
    private var currentItemId = -1
    private var ID = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteRecyBinding.inflate(inflater,container,false)
        return  _binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        InterstitialAd_fun()
        setUpRva()
        adapterOnClick()
    }

    private  fun setUpRva()  {




        favoriteImagesViewModel.getAllFava().observe(viewLifecycleOwner) { listShows ->
            //  msgsAdapter.stateRestorationPolicy=RecyclerView.Adapter.StateRestorationPolicy.ALLOW
            favAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.ALLOW
//            msgsAdapter.msgsModel = listShows
//            binding.rcMsgs.adapter = msgsAdapter
            favAdapter.fav_img_list = listShows
            if(binding.recyclerFav.adapter == null){
                binding.recyclerFav.layoutManager = GridLayoutManager(requireContext(),2)
                binding.recyclerFav.adapter = favAdapter
            }else{
                favAdapter.notifyDataSetChanged()
            }
            Log.e("tessst","enter111")

            if (currentItemId != -1) {
                binding.recyclerFav.scrollToPosition(currentItemId)
            }

        }
    }




    private fun adapterOnClick() {
        favAdapter.onItemClick = { _, favimage: FavoriteImage, currentItemId ->

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
            val directions = FavoriteFragmentRecyDirections.actionFavoriteFragmentRecyToFavFragmentLinRecy(ID, currentItemId,favimage.image_url)
            findNavController().navigate(directions)

        }
        favAdapter.onbtnclick = {
            it.is_fav = false
            imgsffav.updateImages()
            imgsffav.removeFavoriteImage(FavoriteImage(it.id!!, it.ID_Type_id, it.new_img, it.image_url))

            val snackbar = Snackbar.make(view!!, "تم الحذف", Snackbar.LENGTH_SHORT)
            snackbar.show()
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

}

