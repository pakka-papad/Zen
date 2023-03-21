package com.github.pakka_papad.onboarding

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.github.pakka_papad.data.music.ScanStatus
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch
import kotlin.math.max


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPagerApi::class, ExperimentalPermissionsApi::class)
@Composable
fun ColumnScope.ContentApi33(
    scanStatus: ScanStatus,
    scanForMusic: () -> Unit,
    setOnBoardingComplete: () -> Unit,
) {
    val pageCount = 3
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val readExternalStoragePermissionState =
        rememberPermissionState(permission = Manifest.permission.READ_MEDIA_AUDIO)
    val notificationPermissionState =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)
    HorizontalPager(
        count = pageCount,
        modifier = Modifier
            .weight(0.85f)
            .fillMaxSize(),
        state = pagerState,
        userScrollEnabled = false,
    ) { page ->
        PageApi33(
            pageIndex = page,
            readExternalStoragePermissionState = readExternalStoragePermissionState,
            notificationPermissionState = notificationPermissionState,
            scanStatus = scanStatus,
            onStartScanClicked = scanForMusic
        )
    }
    PageIndicator(
        pageCount = pageCount,
        currentPage = pagerState.currentPage,
        nextEnabled = when (pagerState.currentPage) {
            0 -> notificationPermissionState.status.isGranted
            1 -> readExternalStoragePermissionState.status.isGranted
            2 -> scanStatus is ScanStatus.ScanComplete
            else -> true
        },
        onNextClicked = {
            if (pagerState.currentPage == pageCount-1){
                setOnBoardingComplete()
            } else {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        },
        onBackClicked = {
            scope.launch {
                pagerState.animateScrollToPage(max(0, pagerState.currentPage - 1))
            }
        },
        modifier = Modifier
            .weight(0.15f)
            .fillMaxSize()
    )
}

@OptIn(ExperimentalPagerApi::class, ExperimentalPermissionsApi::class)
@Composable
fun ColumnScope.Content(
    scanStatus: ScanStatus,
    scanForMusic: () -> Unit,
    setOnBoardingComplete: () -> Unit,
) {
    val pageCount = 2
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val readExternalStoragePermissionState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
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
            onStartScanClicked = scanForMusic
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
                setOnBoardingComplete()
            } else {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        },
        onBackClicked = {
            scope.launch {
                pagerState.animateScrollToPage(max(0, pagerState.currentPage - 1))
            }
        },
        modifier = Modifier
            .weight(0.15f)
            .fillMaxSize()
    )
}