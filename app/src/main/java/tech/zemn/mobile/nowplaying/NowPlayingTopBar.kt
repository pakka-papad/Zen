package tech.zemn.mobile.nowplaying

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.zemn.mobile.MainActivity

@Composable
fun NowPlayingTopBar(
    onBackArrowPressed: () -> Unit,
    title: String,
) {
    val statusBarHeight = MainActivity.top.dp
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp + statusBarHeight)
            .background(Color(0xFF17C379)),
        navigationIcon = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(56.dp)
                    .padding(top = statusBarHeight),
                contentAlignment = Alignment.Center
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
                    .padding(top = statusBarHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        actions = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(56.dp)
                    .padding(top = statusBarHeight),
                contentAlignment = Alignment.Center
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
                            ),
                            onClick = { }
                        ),
                    colorFilter = ColorFilter.tint(Color.White),
                )
            }
        }
    )
}