package tech.zemn.mobile.playlist

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import tech.zemn.mobile.nowplaying.NowPlayingTopBar

@Composable
fun PlaylistTopBar(
    topBarTitle: String,
    topBarBackgroundImageUri: String,
    onBackArrowPressed: () -> Unit
) {
    if (topBarBackgroundImageUri.isEmpty()) {
        NowPlayingTopBar(
            onBackArrowPressed = onBackArrowPressed,
            title = topBarTitle,
        )
    } else {
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
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black,
                                Color.Transparent,
                            )
                        ),
                    )
                    .align(Alignment.TopCenter)

            )
            TopAppBar(
                modifier = Modifier.fillMaxSize(),
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Image(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier
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
                    }
                },
                title = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.BottomStart),
                            text = topBarTitle,
                            color = Color.White,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Image(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .rotate(90f)
                                .clickable(
                                    interactionSource = MutableInteractionSource(),
                                    indication = rememberRipple(
                                        bounded = false,
                                        radius = 25.dp,
                                        color = Color.White,
                                    ),
                                    onClick = { }
                                ),
                            colorFilter = ColorFilter.tint(Color.White),
                        )
                    }
                },
                backgroundColor = Color.Transparent,
            )
        }
    }
}