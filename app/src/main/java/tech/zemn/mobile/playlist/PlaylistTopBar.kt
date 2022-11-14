package tech.zemn.mobile.playlist

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun PlaylistTopBar(
    topBarTitle: String,
    topBarBackgroundImageUri: String,
    onBackArrowPressed: () -> Unit,
    onPlayAllPressed: () -> Unit,
    onPlaylistAddToQueuePressed: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(256.dp)
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
                    (if (isSystemInDarkTheme()) Color.Black else Color.White).copy(alpha = 0.4f)
                )
                .align(Alignment.TopCenter)
        )
        var dropDownMenuExpanded by remember { mutableStateOf(false) }
        SmallTopAppBar(
            modifier = Modifier
                .background(Color.Transparent)
                .align(Alignment.BottomCenter),
            navigationIcon = {
                Image(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(30.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = rememberRipple(
                                bounded = false,
                                radius = 25.dp,
                                color = Color.White,
                            ),
                            onClick = onBackArrowPressed
                        ),
                    colorFilter = ColorFilter.tint(Color.White)
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
                Image(
                    imageVector = Icons.Outlined.MoreVert,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(30.dp)
                        .rotate(90f)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = rememberRipple(
                                bounded = false,
                                radius = 25.dp,
                                color = Color.White,
                            ),
                            onClick = {
                                dropDownMenuExpanded = true
                            }
                        ),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                )
                DropdownMenu(
                    expanded = dropDownMenuExpanded,
                    onDismissRequest = {
                        dropDownMenuExpanded = false
                    },
                    content = {
                        DropdownMenuItem(
                            onClick = {
                                onPlayAllPressed()
                                dropDownMenuExpanded = false
                            },
                            text = {
                                Text(
                                    text = "Play all",
                                    fontSize = 14.sp,
                                )
                            },
                        )
                        DropdownMenuItem(
                            onClick = {
                                onPlaylistAddToQueuePressed()
                                dropDownMenuExpanded = false
                            },
                            text = {
                                Text(
                                    text = "Add playlist to queue",
                                    fontSize = 14.sp,
                                )
                            },
                        )
                    },
                    offset = DpOffset(x = 0.dp, y = (-20).dp)
                )
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
    }
}