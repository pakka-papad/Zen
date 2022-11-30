package com.github.pakka_papad.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.LayoutDirection
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import com.github.pakka_papad.SharedViewModel
import com.github.pakka_papad.components.TopAppBar
import com.github.pakka_papad.ui.theme.ZenTheme

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel by activityViewModels<SharedViewModel>()

    private lateinit var navController: NavController

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        return ComposeView(requireContext()).apply {
            setContent {
                val themePreference by viewModel.theme.collectAsState()
                ZenTheme(
                    themePreference = themePreference
                ) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                onBackArrowPressed = navController::popBackStack,
                                title = "Settings",
                                actions = { }
                            )
                        },
                        content = { paddingValues ->
                            val insetsPadding =
                                WindowInsets.systemBars.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal).asPaddingValues()
                            SettingsList(
                                paddingValues = PaddingValues(
                                    top = paddingValues.calculateTopPadding(),
                                    start = insetsPadding.calculateStartPadding(LayoutDirection.Ltr),
                                    end = insetsPadding.calculateEndPadding(LayoutDirection.Ltr),
                                    bottom = insetsPadding.calculateBottomPadding()
                                ),
                                themePreference = themePreference,
                                onThemePreferenceChanged = viewModel::updateTheme
                            )
                        }
                    )
                }
            }
        }
    }
}