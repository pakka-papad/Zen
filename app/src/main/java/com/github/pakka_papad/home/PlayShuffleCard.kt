package com.github.pakka_papad.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.R

@Composable
fun PlayShuffleCard(
    onPlayAllClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
) {
    val configuration = LocalConfiguration.current
    val spacerModifier = Modifier.width(8.dp)
    val iconModifier = Modifier.size(24.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            onClick = onPlayAllClicked,
            modifier = Modifier
                .weight(1f),
            content = {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_play_arrow_40),
                    modifier = iconModifier,
                    contentDescription = stringResource(R.string.play_all_button)
                )
                if (configuration.screenWidthDp > 340) {
                    Spacer(spacerModifier)
                    Text(
                        text = stringResource(R.string.play_all),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            },
            shape = MaterialTheme.shapes.large
        )
        Button(
            onClick = onShuffleClicked,
            modifier = Modifier
                .weight(1f),
            content = {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_shuffle_40),
                    modifier = iconModifier,
                    contentDescription = stringResource(R.string.shuffle_button),
                )
                if (configuration.screenWidthDp > 340) {
                    Spacer(spacerModifier)
                    Text(
                        text = stringResource(R.string.shuffle),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            },
            shape = MaterialTheme.shapes.large
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