package tech.zemn.mobile.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeTopBar() {
    Box(
        contentAlignment = Alignment.BottomCenter
    ) {
        SmallTopAppBar(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)),
            title = {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Text(
                        text = "Zemn Music Player",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
            ),
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}