package com.github.pakka_papad.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.R
import com.github.pakka_papad.SharedViewModel
import com.github.pakka_papad.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private lateinit var navController: NavController

    private val viewModel by activityViewModels<SharedViewModel>()

    private var binding: FragmentSplashBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        activity?.window?.decorView?.windowInsetsController?.setSystemBarsAppearance(
            APPEARANCE_LIGHT_STATUS_BARS,
            APPEARANCE_LIGHT_STATUS_BARS
        )
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.isOnBoardingComplete.collect {
                    if (it == null) return@collect
                    navController.navigate(
                        if (it) R.id.action_splashFragment_to_homeFragment
                        else R.id.action_splashFragment_to_onBoardingFragment
                    )
                }
            }
        }
    }
}