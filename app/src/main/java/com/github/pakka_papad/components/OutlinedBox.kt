package com.github.pakka_papad.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun OutlinedBox(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    borderWidth: Dp = 2.dp,
    borderColor: Color = MaterialTheme.colorScheme.onSurface,
    borderShape: Shape = RoundedCornerShape(6.dp),
    label: String,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier) {
        var labelRect by remember { mutableStateOf<Rect?>(null) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawWithoutRect(labelRect)
                .zIndex(2f)
                .padding(top = 13.dp)
                .border(borderWidth, borderColor, borderShape)
                .padding(contentPadding),
            content = content,
        )
        Text(
            text = label,
            color = borderColor,
            modifier = Modifier
                .height(26.dp)
                .zIndex(3f)
                .padding(start = 10.dp)
                .onGloballyPositioned {
                    labelRect = it.boundsInParent()
                }
                .padding(horizontal = 4.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OBox() {
    OutlinedBox(
        content = {
            Text(
                text = "Use Material You dynamic theme"
            )
        },
        borderColor = Color.Magenta,
        label = "App Theme",
        modifier = Modifier.padding(horizontal = 30.dp).fillMaxWidth(),
        contentPadding = PaddingValues(10.dp)
    )
}

fun Modifier.drawWithoutRect(rect: Rect?) =
    drawWithContent {
        if (rect != null) {
            clipRect(
                left = rect.left,
                top = rect.top,
                right = rect.right,
                bottom = rect.bottom,
                clipOp = ClipOp.Difference,
            ) {
                this@drawWithContent.drawContent()
            }
        } else {
            drawContent()
        }
    }
