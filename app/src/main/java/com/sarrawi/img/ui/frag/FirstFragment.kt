package com.sarrawi.img.ui.frag

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.sarrawi.img.R
import com.sarrawi.img.databinding.FragmentFirstBinding


class FirstFragment : Fragment() {

    private lateinit var _binding: FragmentFirstBinding

    private val binding get() = _binding!!





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler(Looper.myLooper()!!).postDelayed({


            findNavController()
                .navigate(
                    R.id.action_FirstFragment_to_SecondFragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(
                            R.id.FirstFragment,
                            true).build()
                )

        },5000)
    }



    override fun onDestroyView() {
        super.onDestroyView()

    }


}