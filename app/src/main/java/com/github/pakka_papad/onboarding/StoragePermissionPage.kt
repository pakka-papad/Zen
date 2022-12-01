package com.github.pakka_papad.onboarding

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.Constants
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun StoragePermissionPage(
    permissionState: PermissionState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (permissionState.status.isGranted) "Storage access granted!!!" else "Zen needs access to internal storage to find all the awesome songs and build an amazing library \uD83E\uDD73",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        if (!permissionState.status.isGranted){
            Button(
                onClick = permissionState::launchPermissionRequest
            ) {
                Text(
                    text = "Grant access to storage",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
    val context = LocalContext.current
    AnimatedVisibility(visible = permissionState.status.shouldShowRationale) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.fromParts("package",Constants.PACKAGE_NAME,null)
                        context.startActivity(intent)
                    }
                ) {
                    Text(
                        text = "Open app settings",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            },
            text = {
                Text(
                    text = "We can't go ahead without access to internal storage. Kindly go to app settings to grant permission",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }
        )
    }
}