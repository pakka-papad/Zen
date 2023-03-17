package com.github.pakka_papad.splash

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.R
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private lateinit var navController: NavController

    private var binding: FragmentSplashBinding? = null

    @Inject
    lateinit var preferenceProvider: ZenPreferenceProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            binding?.appIcon?.visibility = View.GONE
            binding?.linearIndicator?.visibility = View.GONE
        } else {
            binding?.progressCircular?.visibility = View.GONE
        }
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                preferenceProvider.isOnBoardingComplete.collectLatest {
                    if (it == null) return@collectLatest
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S){
                        delay(400)
                    }
                    val nextFragment = when(it){
                        true -> R.id.action_splashFragment_to_homeFragment
                        false -> R.id.action_splashFragment_to_onBoardingFragment
                    }
                    navController.navigate(nextFragment)
                }
            }
        }
    }
}