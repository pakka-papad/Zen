package com.github.pakka_papad.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.R
import com.github.pakka_papad.components.Snackbar
import com.github.pakka_papad.components.TopBarWithBackArrow
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.ui.theme.ZenTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private val viewModel :SettingsViewModel by viewModels()

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
                val themePreference by preferenceProvider.theme.collectAsStateWithLifecycle()
                val scanStatus by viewModel.scanStatus.collectAsStateWithLifecycle()
                val isCrashlyticsDisabled by preferenceProvider.isCrashlyticsDisabled.collectAsStateWithLifecycle()

                val tabsSelection by viewModel.tabsSelection.collectAsStateWithLifecycle()

                val restoreClicked = remember{ {
                    if (navController.currentDestination?.id == R.id.settingsFragment){
                        navController.navigate(R.id.action_settingsFragment_to_restoreFragment)
                    }
                } }
                val whatsNewClicked = remember{ {
                    if (navController.currentDestination?.id == R.id.settingsFragment){
                        navController.navigate(R.id.action_settingsFragment_to_whatsNewFragment)
                    }
                } }
                val onRestoreFoldersClicked = remember{ {
                    if (navController.currentDestination?.id == R.id.settingsFragment){
                        navController.navigate(R.id.action_settingsFragment_to_restoreFolderFragment)
                    }
                } }

                val snackbarHostState = remember { SnackbarHostState() }

                val message by viewModel.message.collectAsStateWithLifecycle()
                LaunchedEffect(key1 = message){
                    if (message.isEmpty()) return@LaunchedEffect
                    snackbarHostState.showSnackbar(message)
                }

                ZenTheme(themePreference) {
                    Scaffold(
                        topBar = {
                            TopBarWithBackArrow(
                                onBackArrowPressed = navController::popBackStack,
                                title = stringResource(R.string.settings),
                                actions = { }
                            )
                        },
                        content = { paddingValues ->
                            SettingsList(
                                paddingValues = paddingValues,
                                themePreference = themePreference,
                                onThemePreferenceChanged = preferenceProvider::updateTheme,
                                scanStatus = scanStatus,
                                onScanClicked = viewModel::scanForMusic,
                                onRestoreClicked = restoreClicked,
                                disabledCrashlytics = isCrashlyticsDisabled,
                                onAutoReportCrashClicked = preferenceProvider::toggleCrashlytics,
                                onWhatsNewClicked = whatsNewClicked,
                                onRestoreFoldersClicked = onRestoreFoldersClicked,
                                tabsSelection = tabsSelection,
                                onTabsSelectChange = viewModel::onTabsSelectChanged,
                                onTabsOrderChanged = viewModel::onTabsOrderChanged,
                                onTabsOrderConfirmed = viewModel::saveTabsOrder
                            )
                        },
                        snackbarHost = {
                            SnackbarHost(
                                hostState = snackbarHostState,
                                snackbar = {
                                    Snackbar(it)
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}