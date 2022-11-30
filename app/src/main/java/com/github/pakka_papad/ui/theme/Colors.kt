package com.github.pakka_papad.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun Colors(paddingValues: PaddingValues) {
    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Fixed(2),
        contentPadding = paddingValues
    ) {
        item { ColorCard(color = MaterialTheme.colorScheme.primary, colorType = "primary") }
        item { ColorCard(color = MaterialTheme.colorScheme.onPrimary, colorType = "on-primary") }
        item { ColorCard(color = MaterialTheme.colorScheme.primaryContainer, colorType = "primary-container") }
        item { ColorCard(color = MaterialTheme.colorScheme.onPrimaryContainer, colorType = "on-primary-container") }
        item { ColorCard(color = MaterialTheme.colorScheme.inversePrimary, colorType = "inverse-primary") }
        item { ColorCard(color = MaterialTheme.colorScheme.secondary, colorType = "secondary") }
        item { ColorCard(color = MaterialTheme.colorScheme.onSecondary, colorType = "on-secondary") }
        item { ColorCard(color = MaterialTheme.colorScheme.secondaryContainer, colorType = "secondary-container") }
        item { ColorCard(color = MaterialTheme.colorScheme.onSecondaryContainer, colorType = "on-secondary-container") }
        item { ColorCard(color = MaterialTheme.colorScheme.tertiary, colorType = "tertiary") }
        item { ColorCard(color = MaterialTheme.colorScheme.onTertiary, colorType = "on-tertiary") }
        item { ColorCard(color = MaterialTheme.colorScheme.tertiaryContainer, colorType = "tertiary-container") }
        item { ColorCard(color = MaterialTheme.colorScheme.background, colorType = "background") }
        item { ColorCard(color = MaterialTheme.colorScheme.onBackground, colorType = "on-background") }
        item { ColorCard(color = MaterialTheme.colorScheme.surface, colorType = "surface") }
        item { ColorCard(color = MaterialTheme.colorScheme.onSurface, colorType = "on-surface") }
        item { ColorCard(color = MaterialTheme.colorScheme.surfaceVariant, colorType = "surface-variant") }
        item { ColorCard(color = MaterialTheme.colorScheme.onSurfaceVariant, colorType = "on-surface-variant") }
        item { ColorCard(color = MaterialTheme.colorScheme.surfaceTint, colorType = "surface-tint") }
        item { ColorCard(color = MaterialTheme.colorScheme.inverseSurface, colorType = "inverse-surface") }
        item { ColorCard(color = MaterialTheme.colorScheme.inverseOnSurface, colorType = "inverse-on-surface") }
        item { ColorCard(color = MaterialTheme.colorScheme.error, colorType = "error") }
        item { ColorCard(color = MaterialTheme.colorScheme.onError, colorType = "on-error") }
        item { ColorCard(color = MaterialTheme.colorScheme.errorContainer, colorType = "error-container") }
        item { ColorCard(color = MaterialTheme.colorScheme.onErrorContainer, colorType = "on-error-container") }
        item { ColorCard(color = MaterialTheme.colorScheme.outline, colorType = "outline") }
    }
}

@Composable
fun ColorCard(
    color: Color,
    colorType: String,
) {
    Box(
        modifier = Modifier
            .aspectRatio(ratio = 1f, matchHeightConstraintsFirst = false)
            .fillMaxWidth()
            .background(color),
        contentAlignment = Alignment.BottomStart,
    ){
        Text(
            text = colorType
        )
    }
}