package com.github.pakka_papad.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
private fun ResultCard(
    onSeeAllClicked: () -> Unit,
    title: String,
    result: @Composable () -> Unit
) = Column(
    modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.titleLarge
        )
        Icon(
            imageVector = Icons.Outlined.ArrowBack,
            contentDescription = "see-all-arrow",
            modifier = Modifier
                .size(48.dp)
                .rotate(180f)
                .clickable(
                    onClick = onSeeAllClicked,
                    indication = rememberRipple(false, 24.dp),
                    interactionSource = remember { MutableInteractionSource() }
                )
                .padding(9.dp)
        )
    }
    result()
}

@Composable
fun TextResult(
    texts: List<String>,
    title: String,
){
    if (texts.isEmpty()) return
    ResultCard(
        onSeeAllClicked = {  },
        title = title,
        result = {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ){
                items(texts){ text ->
                    Text(
                        text = text,
                        maxLines = 1,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(12.dp)
                            .widthIn(max = 300.dp),
                        style = MaterialTheme.typography.titleLarge,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    )
}
