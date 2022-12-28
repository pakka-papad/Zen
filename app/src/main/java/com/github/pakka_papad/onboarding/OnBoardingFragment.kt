package com.github.pakka_papad.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.R
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.data.music.ScanStatus
import com.github.pakka_papad.ui.theme.DefaultTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@AndroidEntryPoint
class OnBoardingFragment : Fragment() {

    private val viewModel : OnBoardingViewModel by viewModels()

    private lateinit var navController: NavController

    @Inject
    lateinit var preferenceProvider: ZenPreferenceProvider

    @OptIn(ExperimentalPagerApi::class, ExperimentalPermissionsApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        return ComposeView(requireContext()).apply {
            setContent {
                DefaultTheme {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .windowInsetsPadding(WindowInsets.systemBars)
                    ) {
                        val pageCount = 2
                        val pagerState = rememberPagerState()
                        val scope = rememberCoroutineScope()
                        val readExternalStoragePermissionState =
                            rememberPermissionState(permission = android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        val scanStatus by viewModel.scanStatus.collectAsState()
                        HorizontalPager(
                            count = pageCount,
                            modifier = Modifier
                                .weight(0.85f)
                                .fillMaxSize(),
                            state = pagerState,
                            userScrollEnabled = false,

                        ) { page ->
                            Page(
                                pageIndex = page,
                                readExternalStoragePermissionState = readExternalStoragePermissionState,
                                scanStatus = scanStatus,
                                onStartScanClicked = viewModel::scanForMusic
                            )
                        }
                        PageIndicator(
                            pageCount = pageCount,
                            currentPage = pagerState.currentPage,
                            nextEnabled = when (pagerState.currentPage) {
                                0 -> readExternalStoragePermissionState.status.isGranted
                                1 -> scanStatus is ScanStatus.ScanComplete
                                else -> true
                            },
                            onNextClicked = {
                                if (pagerState.currentPage == pageCount-1){
                                    preferenceProvider.setOnBoardingComplete()
                                    navController.navigate(R.id.action_onBoardingFragment_to_homeFragment)
                                } else {
                                    scope.launch {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            },
                            onBackClicked = {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        max(
                                            0,
                                            pagerState.currentPage - 1
                                        )
                                    )
                                }
                            },
                            modifier = Modifier
                                .weight(0.15f)
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}