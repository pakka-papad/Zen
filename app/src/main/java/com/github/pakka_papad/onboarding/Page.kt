package com.github.pakka_papad.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.R
import com.github.pakka_papad.data.music.ScanStatus
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Page(
    pageIndex: Int,
    readExternalStoragePermissionState: PermissionState,
    scanStatus: ScanStatus,
    onStartScanClicked: () -> Unit
){
    when(pageIndex){
        0 -> ReadStoragePermissionPage(readExternalStoragePermissionState)
        1 -> ScanningPage(
            scanStatus = scanStatus,
            onStartScanClicked = onStartScanClicked
        )
        else -> Text(
            text = "Page $pageIndex",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PageApi33(
    pageIndex: Int,
    readExternalStoragePermissionState: PermissionState,
    notificationPermissionState: PermissionState,
    scanStatus: ScanStatus,
    onStartScanClicked: () -> Unit
){
    when(pageIndex){
        0 -> NotificationPermissionPage(notificationPermissionState)
        1 -> ReadAudioPermissionPage(readExternalStoragePermissionState)
        2 -> ScanningPage(
            scanStatus = scanStatus,
            onStartScanClicked = onStartScanClicked
        )
        else -> Text(
            text = "Page $pageIndex",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
fun PageIndicator(
    pageCount: Int,
    currentPage: Int,
    nextEnabled: Boolean,
    onNextClicked: () -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .padding(10.dp),
    ) {
        if (currentPage != 0){
            Button(
                onClick = onBackClicked,
                modifier = Modifier
                    .align(Alignment.CenterStart)
            ){
                Text(
                    text = stringResource(R.string.back),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            val primaryColor = MaterialTheme.colorScheme.primary
            val inversePrimaryColor = MaterialTheme.colorScheme.inversePrimary
            repeat(pageCount){
                if (it == currentPage){
                    Canvas(modifier = Modifier.size(12.dp)){
                        drawCircle(
                            color = primaryColor
                        )
                    }
                } else {
                    Canvas(modifier = Modifier.size(8.dp)){
                        drawCircle(
                            color = inversePrimaryColor
                        )
                    }
                }
            }
        }
        Button(
            onClick = onNextClicked,
            modifier = Modifier
                .align(Alignment.CenterEnd),
            enabled = nextEnabled
        ){
            Text(
                text = if(pageCount-1 == currentPage) stringResource(R.string.finish) else stringResource(R.string.next),
                style = MaterialTheme.typography.titleMedium,
                color = if (nextEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}