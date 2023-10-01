package com.github.pakka_papad.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.pakka_papad.components.SmallTopBar
import com.github.pakka_papad.R
import com.github.pakka_papad.Screens
import com.github.pakka_papad.components.SortOptionChooser
import com.github.pakka_papad.components.SortOptions
import com.github.pakka_papad.components.getSortOptions

@Composable
fun HomeTopBar(
    onSettingsClicked: () -> Unit,
    onSearchClicked: () -> Unit,
    currentScreen: Screens,
    onSortOptionChosen: (currScreen: Int, option: Int) -> Unit,
    currentSortOrder: Map<Int,Int>,
) {
    var sortMenuVisible by  remember { mutableStateOf(false) }
    var options by remember { mutableStateOf(listOf<SortOptions>()) }
    LaunchedEffect(key1 = currentScreen){
        sortMenuVisible = false
        options = currentScreen.getSortOptions()
    }
    SmallTopBar(
        leadingIcon = { },
        title = buildAnnotatedString {
            withStyle(
                SpanStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                )
            ) {
                append("Zen ")
            }
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp
                )
            ) {
                append("Music")
            }
        },
        actions = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "search-btn",
                modifier = Modifier
                    .size(48.dp)
                    .padding(9.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = false,
                            radius = 25.dp,
                        ),
                        onClick = onSearchClicked
                    ),
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_sort_40),
                contentDescription = "sort-btn",
                modifier = Modifier
                    .size(48.dp)
                    .padding(9.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = false,
                            radius = 25.dp,
                        ),
                        onClick = { sortMenuVisible = true }
                    ),
                tint = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = "settings-btn",
                modifier = Modifier
                    .size(48.dp)
                    .padding(9.dp)
                    .rotate(90f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(
                            bounded = false,
                            radius = 25.dp,
                        ),
                        onClick = onSettingsClicked
                    ),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        },
        titleMaxLines = 1,
        backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
        onBackgroundColor = MaterialTheme.colorScheme.onSurface,
    )
    if (sortMenuVisible){
        SortOptionChooser(
            options = options,
            selectedOption = currentSortOrder[currentScreen.ordinal] ?: options.first().ordinal,
            onOptionSelect = { option ->
                onSortOptionChosen(currentScreen.ordinal, option)
                sortMenuVisible = false
            },
            onChooserDismiss = { sortMenuVisible = false }
        )
    }
}
