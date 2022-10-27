package tech.zemn.mobile.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.zemn.mobile.ZemnApp

@Composable
fun HomeTopBar() {
    val density = LocalDensity.current
    val statusBarHeight by remember { mutableStateOf(with(density) { ZemnApp.statusBarHeight.toDp() }) }
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp + statusBarHeight)
            .background(Color(0xFF17C379)),
        title = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = statusBarHeight, bottom = 16.dp),
                contentAlignment = Alignment.BottomStart,
            ) {
                Text(
                    text = "Zemn Music Player",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }

        }
    )
}