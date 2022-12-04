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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.R

@Composable
fun PlayShuffleCard(
    onPlayAllClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(85.dp),
    ) {
        val cf = LocalConfiguration.current
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,

        ) {
            Button(
                onClick = onPlayAllClicked,
                modifier = Modifier
//                    .width(IntrinsicSize.Max)
//                    .fillMaxWidth(0.7f)
//
                    .weight(1f)
                    .clip(RoundedCornerShape(30.dp)),
                content = {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_play_arrow_24),
                        modifier = Modifier
                            .size(30.dp)
                            .padding(4.dp),
                        contentDescription = "play-all-button"
                    )
                    if (cf.screenWidthDp > 340){
                        Text(
                            text = "Play All",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            )
            Button(
                onClick = onShuffleClicked,
                modifier = Modifier
//                    .width(IntrinsicSize.Max)
//                    .fillMaxWidth(0.7f)
                    .weight(1f)
                    .clip(RoundedCornerShape(30.dp)),
                content = {
                    Icon(
                        painter = painterResource(R.drawable.ic_baseline_shuffle_24),
                        modifier = Modifier
                            .size(30.dp)
                            .padding(4.dp),
                        contentDescription = "shuffle-button"
                    )
                    if (cf.screenWidthDp > 340){
                        Text(
                            text = "Shuffle",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            )
        }
        Spacer(
            modifier = Modifier
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