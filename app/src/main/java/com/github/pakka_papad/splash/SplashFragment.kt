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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.github.pakka_papad.R
import com.github.pakka_papad.SharedViewModel
import com.github.pakka_papad.data.UserPreferences
import com.github.pakka_papad.databinding.FragmentSplashBinding

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


        val startedAt = System.currentTimeMillis()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED){
                viewModel.theme.collectLatest {
//                    if (it.theme != UserPreferences.Theme.UNRECOGNIZED){
                        if (System.currentTimeMillis() - startedAt < 1500){
                            delay(1000)
                        }
                        navController.navigate(R.id.action_splashFragment_to_homeFragment)
//                    }
                }
            }
        }
    }
}