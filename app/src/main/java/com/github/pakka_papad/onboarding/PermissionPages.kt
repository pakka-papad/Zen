package com.github.pakka_papad.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.airbnb.lottie.compose.*
import com.github.pakka_papad.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionPage(
    permissionState: PermissionState,
) = PermissionPage(
    permissionState = permissionState,
    lottieRawRes = R.raw.notification_permission,
    header = "Notification access",
    description = "Zen needs access to post notifications regarding the media playing",
    grantedMessage = "Notification access granted",
    notGrantedMessage = "Grant access to notification"
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReadAudioPermissionPage(
    permissionState: PermissionState,
) = PermissionPage(
    permissionState = permissionState,
    lottieRawRes = R.raw.storage_permission,
    header = "Audio files access",
    description = "Zen needs access to read audio files present on the device",
    grantedMessage = "Access granted",
    notGrantedMessage = "Grant access to read audio"
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReadStoragePermissionPage(
    permissionState: PermissionState,
) = PermissionPage(
    permissionState = permissionState,
    lottieRawRes = R.raw.storage_permission,
    header = "Storage access",
    description = "Zen needs storage access to read audio files present on the device",
    grantedMessage = "Access granted",
    notGrantedMessage = "Grant access to read storage"
)