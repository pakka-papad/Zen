package com.github.pakka_papad.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
private fun BaseTopBar(
    appBar: @Composable BoxScope.() -> Unit,
    backgroundColor: Color,
) = Box(
    contentAlignment = Alignment.BottomCenter,
    modifier = Modifier
        .background(backgroundColor),
    content = {
        appBar()
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
)

@Composable
private fun TopBarTitle(
    title: String,
    titleMaxLines: Int,
    textColor: Color,
) = Text(
    text = title,
    style = MaterialTheme.typography.titleLarge,
    maxLines = titleMaxLines,
    overflow = TextOverflow.Ellipsis,
    color = textColor,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterAlignedTopBar(
    leadingIcon: @Composable () -> Unit,
    title: String,
    actions: @Composable RowScope.() -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface,
    titleMaxLines: Int,
) = BaseTopBar(
    appBar = {
        CenterAlignedTopAppBar(
            modifier = Modifier
                .background(backgroundColor)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
            navigationIcon = leadingIcon,
            title = {
                TopBarTitle(
                    title = title,
                    titleMaxLines = titleMaxLines,
                    textColor = onBackgroundColor,
                )
            },
            actions = actions,
        )
    },
    backgroundColor = backgroundColor,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopBar(
    leadingIcon: @Composable () -> Unit,
    title: AnnotatedString,
    actions: @Composable RowScope.() -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface,
    titleMaxLines: Int,
) = BaseTopBar(
    appBar = {
        TopAppBar(
            modifier = Modifier
                .background(backgroundColor)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
            navigationIcon = leadingIcon,
            title = {
                Text(
                    text = title,
                    maxLines = titleMaxLines,
                    color = onBackgroundColor,
                    style = MaterialTheme.typography.titleLarge,
                    overflow = TextOverflow.Ellipsis
                )
            },
            actions = actions,
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = backgroundColor
            )
        )
    },
    backgroundColor = backgroundColor
)

@Composable
fun TopBarWithBackArrow(
    onBackArrowPressed: () -> Unit,
    title: String,
    actions: @Composable RowScope.() -> Unit,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    onBackgroundColor: Color = MaterialTheme.colorScheme.onSurface,
    titleMaxLines: Int = 1
) = CenterAlignedTopBar(
    leadingIcon = {
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
                    ),
                    onClick = onBackArrowPressed
                ),
            tint = onBackgroundColor
        )
    },
    title = title,
    actions = actions,
    backgroundColor = backgroundColor,
    titleMaxLines = titleMaxLines,
)