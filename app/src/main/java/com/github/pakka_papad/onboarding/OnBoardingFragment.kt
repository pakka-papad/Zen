package com.github.pakka_papad.onboarding

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.R
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.ui.theme.DefaultTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private val viewModel : OnBoardingViewModel by viewModels()

    private lateinit var navController: NavController

    @Inject
    lateinit var preferenceProvider: ZenPreferenceProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val setOnBoardingComplete = remember{ {
                    preferenceProvider.setOnBoardingComplete()
                    if (navController.currentDestination?.id == R.id.onBoardingFragment){
                        navController.navigate(R.id.action_onBoardingFragment_to_homeFragment)
                    }
                } }
                DefaultTheme {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .windowInsetsPadding(WindowInsets.systemBars)
                    ) {
                        val scanStatus by viewModel.scanStatus.collectAsState()
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                            ContentApi33(
                                scanStatus = scanStatus,
                                scanForMusic = viewModel::scanForMusic,
                                setOnBoardingComplete = setOnBoardingComplete
                            )
                        } else {
                            Content(
                                scanStatus = scanStatus,
                                scanForMusic = viewModel::scanForMusic,
                                setOnBoardingComplete = setOnBoardingComplete
                            )
                        }
                    }
                }
            }
        }
    }
}
