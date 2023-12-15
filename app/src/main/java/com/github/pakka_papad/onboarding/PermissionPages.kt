package com.github.pakka_papad.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
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
    header = stringResource(R.string.notification_access),
    description = stringResource(R.string.zen_needs_access_to_post_notifications_regarding_the_media_playing),
    grantedMessage = stringResource(R.string.notification_access_granted),
    notGrantedMessage = stringResource(R.string.grant_access_to_notification)
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReadAudioPermissionPage(
    permissionState: PermissionState,
) = PermissionPage(
    permissionState = permissionState,
    lottieRawRes = R.raw.storage_permission,
    header = stringResource(R.string.audio_files_access),
    description = stringResource(R.string.zen_needs_access_to_read_audio_files_present_on_the_device),
    grantedMessage = stringResource(R.string.access_granted),
    notGrantedMessage = stringResource(R.string.grant_access_to_read_audio)
)

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReadStoragePermissionPage(
    permissionState: PermissionState,
) = PermissionPage(
    permissionState = permissionState,
    lottieRawRes = R.raw.storage_permission,
    header = stringResource(R.string.storage_access),
    description = stringResource(R.string.zen_needs_storage_access_to_read_audio_files_present_on_the_device),
    grantedMessage = stringResource(R.string.access_granted),
    notGrantedMessage = stringResource(R.string.grant_access_to_read_storage)
)