package com.sarrawi.img.ui.frag

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.sarrawi.img.adapter.TypesAdapter_T
import com.sarrawi.img.databinding.FragmentSecondBinding
import com.sarrawi.img.db.viewModel.ImgTypes_ViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    private val binding get() = _binding!!
    private val imgtypesViewmodel: ImgTypes_ViewModel by lazy {
        ViewModelProvider(requireActivity(), ImgTypes_ViewModel.ImgTypesViewModelFactory(requireActivity().application)).get(
            ImgTypes_ViewModel::class.java)
    }

    private val typesAdapter_T by lazy {
        TypesAdapter_T(requireActivity())
    }

    var clickCount = 0
    var mInterstitialAd: InterstitialAd?=null

    private lateinit var adView: AdView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        InterstitialAd_fun()
        setUpRv()
        adapterOnClick()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun setUpRv() = imgtypesViewmodel.viewModelScope.launch {
            imgtypesViewmodel.getAllTypes_ViewModel().observe(requireActivity()){imgTypes->
                typesAdapter_T.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
                typesAdapter_T.imgTypes_list = imgTypes
                if(binding.rvImgTypes.adapter == null){


                    binding.rvImgTypes.layoutManager = GridLayoutManager(requireContext(), 2) // هنا تعيين عدد الأعمدة إلى 2
                    binding.rvImgTypes.adapter = typesAdapter_T
                    typesAdapter_T.notifyDataSetChanged()
                }else{
                    typesAdapter_T.notifyDataSetChanged()
                }
            }
        }

    fun adapterOnClick(){
        typesAdapter_T.onItemClick = {id ->

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

            val directions = SecondFragmentDirections.actionSecondFragmentToThirdFragment(id)
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

}