package com.github.pakka_papad.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.R
import com.github.pakka_papad.data.ZenPreferenceProvider
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Stub Fragment with no view
 * Used to decide if onboarding is to be shown or home screen
 */
@AndroidEntryPoint
class SplashFragment : Fragment() {

    private lateinit var navController: NavController

    @Inject
    lateinit var preferenceProvider: ZenPreferenceProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        lifecycleScope.launch {
            preferenceProvider.isOnBoardingComplete.collectLatest {
                if (it == null) return@collectLatest
                if (navController.currentDestination?.id != R.id.splashFragment) return@collectLatest
                val nextFragment = when(it){
                    true -> R.id.action_splashFragment_to_homeFragment
                    false -> R.id.action_splashFragment_to_onBoardingFragment
                }
                navController.navigate(nextFragment)
            }
        }
        return null
    }
}