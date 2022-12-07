package com.github.pakka_papad.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.github.pakka_papad.components.TopBarWithBackArrow
import com.github.pakka_papad.components.more_options.OptionsDropDown
import com.github.pakka_papad.data.UserPreferences
import com.github.pakka_papad.ui.theme.ThemePreference

@Composable
fun CollectionTopBar(
    topBarTitle: String,
    topBarBackgroundImageUri: String,
    onBackArrowPressed: () -> Unit,
    themePreference: ThemePreference,
    actions: List<CollectionActions> = listOf(),
) {
    val configuration = LocalConfiguration.current
    if (topBarBackgroundImageUri.isEmpty() || configuration.screenHeightDp < 360) {
        TopBarWithBackArrow(
            onBackArrowPressed = onBackArrowPressed,
            title = topBarTitle,
            actions = {
                CollectionTopBarActions(actions)
            }
        )
    } else {
        val systemInDarkTheme = isSystemInDarkTheme()
        val darkScrim by remember(themePreference) {
            derivedStateOf {
                when (themePreference.theme) {
                    UserPreferences.Theme.DARK_MODE -> true
                    UserPreferences.Theme.LIGHT_MODE, UserPreferences.Theme.UNRECOGNIZED -> false
                    UserPreferences.Theme.USE_SYSTEM_MODE -> systemInDarkTheme
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (configuration.screenHeightDp >= 720) 240.dp else 120.dp)
        ) {
            AsyncImage(
                model = topBarBackgroundImageUri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black,
                            )
                        ),
                    )
                    .align(Alignment.BottomCenter)
            )
            val paddingValues =
                WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                    .asPaddingValues()
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(paddingValues.calculateTopPadding())
                    .background(
                        (if (darkScrim) Color.Black else Color.White).copy(alpha = 0.4f)
                    )
                    .align(Alignment.TopCenter)
            )
            SmallTopAppBar(
                modifier = Modifier
                    .background(Color.Transparent)
                    .padding(
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    )
                    .align(Alignment.BottomCenter),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(30.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(
                                    bounded = false,
                                    radius = 25.dp,
                                    color = Color.White,
                                ),
                                onClick = onBackArrowPressed
                            ),
                        tint = Color.White
                    )
                },
                title = {
                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomStart),
                        text = topBarTitle,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 20.sp
                    )
                },
                actions = {
                    CollectionTopBarActions(actions)
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun CollectionTopBarActions(
    actions: List<CollectionActions>
) {
    var dropDownMenuExpanded by remember { mutableStateOf(false) }
    Icon(
        imageVector = Icons.Outlined.MoreVert,
        contentDescription = null,
        modifier = Modifier
            .padding(16.dp)
            .size(30.dp)
            .rotate(90f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = 25.dp,
                    color = Color.White,
                ),
                onClick = {
                    dropDownMenuExpanded = true
                }
            ),
        tint = MaterialTheme.colorScheme.onSurface,
    )
    OptionsDropDown(
        options = actions,
        expanded = dropDownMenuExpanded,
        onDismissRequest = {
            dropDownMenuExpanded = false
        },
        offset = DpOffset(x = 0.dp, y = (-46).dp)
    )
}