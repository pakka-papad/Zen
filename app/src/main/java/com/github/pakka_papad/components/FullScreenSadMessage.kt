package com.github.pakka_papad.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.R

@Composable
fun FullScreenSadMessage(
    message: String? = null,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
) = Column(
    modifier = modifier
        .sizeIn(minWidth = 200.dp, minHeight = 200.dp, maxWidth = 500.dp, maxHeight = 500.dp)
        .padding(paddingValues)
        .alpha(0.4f),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    content = {
        Icon(
            modifier = Modifier
                .weight(1f, false)
                .aspectRatio(1f, false)
                .fillMaxSize()
                .padding(24.dp),
            painter = painterResource(R.drawable.ic_baseline_sentiment_very_dissatisfied_40),
            contentDescription = stringResource(R.string.sad_face_image),
            tint = MaterialTheme.colorScheme.onSurface
        )
        message?.let {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                text = it,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
)