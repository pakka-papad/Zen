package com.github.pakka_papad.onboarding

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.RawRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.github.pakka_papad.Constants
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionPage(
    permissionState: PermissionState,
    @RawRes lottieRawRes: Int,
    header: String,
    description: String,
    grantedMessage: String,
    notGrantedMessage: String,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieRawRes))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().weight(0.4f),
            contentAlignment = Alignment.Center,
        ){
            Text(
                text = header,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.ExtraBold,
            )
        }
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier.fillMaxWidth().weight(0.4f),
            contentAlignment = Alignment.Center,
        ){
            Text(
                text = description,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Button(
            onClick = {
                if (!permissionState.status.isGranted){
                    permissionState.launchPermissionRequest()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (permissionState.status.isGranted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
        ) {
            Text(
                text = if (permissionState.status.isGranted) grantedMessage else notGrantedMessage,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = if (permissionState.status.isGranted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
            )
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
                        intent.data = Uri.fromParts("package", Constants.PACKAGE_NAME, null)
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
                    text = "We can't go ahead without this permission. Kindly go to app settings to grant permission",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                )
            }
        )
    }
}