package tech.zemn.mobile.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeTopBar() {
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .background(Color(0xFF17C379)),
        title = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 40.dp)
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