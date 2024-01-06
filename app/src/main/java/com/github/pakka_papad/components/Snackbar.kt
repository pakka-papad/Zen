package com.github.pakka_papad.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.material3.Snackbar as M3Snackbar

@Composable
fun Snackbar(
    snackbarData: SnackbarData
){
    M3Snackbar(
        snackbarData = snackbarData,
        shape = MaterialTheme.shapes.medium,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )
}