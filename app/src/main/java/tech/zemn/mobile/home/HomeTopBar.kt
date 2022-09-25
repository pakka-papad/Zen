package tech.zemn.mobile.home

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun HomeTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Zemn Music Player",
                fontSize = 20.sp,
                color = Color.White
            )
        }
    )
}