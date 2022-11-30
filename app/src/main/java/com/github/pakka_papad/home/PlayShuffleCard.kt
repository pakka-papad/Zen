package com.github.pakka_papad.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.pakka_papad.R

@Composable
fun PlayShuffleCard(
    onPlayAllClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(85.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                onClick = onPlayAllClicked,
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .height(IntrinsicSize.Max)
                    .clip(RoundedCornerShape(30.dp)),
                content = {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_play_arrow_24),
                        modifier = Modifier
                            .size(30.dp)
                            .padding(4.dp),
                        contentDescription = "play-all-button"
                    )
                    Text(
                        text = "Play All",
                        fontSize = 16.sp,
                    )
                }
            )
            Button(
                onClick = onShuffleClicked,
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .height(IntrinsicSize.Max)
                    .clip(RoundedCornerShape(30.dp)),
                content = {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_shuffle_24),
                        modifier = Modifier
                            .size(30.dp)
                            .padding(4.dp),
                        contentDescription = "shuffle-button"
                    )
                    Text(
                        text = "Shuffle",
                        fontSize = 16.sp,
                    )
                }
            )
        }
        Spacer(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .height(0.8.dp)
                .background(color = MaterialTheme.colorScheme.surfaceVariant)
                .align(Alignment.BottomCenter)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PlayShuffleCardPreview() {
    PlayShuffleCard(
        onPlayAllClicked = { },
        onShuffleClicked = { }
    )
}