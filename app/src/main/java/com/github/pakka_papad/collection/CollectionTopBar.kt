package com.github.pakka_papad.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.R
import com.github.pakka_papad.components.more_options.OptionsDropDown

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionTopBar(
    topBarTitle: String,
    alpha: Float,
    onBackArrowPressed: () -> Unit,
    actions: List<CollectionActions> = listOf(),
){
    Box {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = topBarTitle,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.alpha(alpha)
                )
            },
            navigationIcon = {
                Icon(
                    imageVector = Icons.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                    modifier = Modifier
                        .padding(6.dp)
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            CircleShape
                        )
                        .padding(5.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(
                                bounded = false,
                                radius = 25.dp,
                            ),
                            onClick = onBackArrowPressed
                        ),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            actions = {
                CollectionTopBarActions(actions)
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = alpha)
            )
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(alpha)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun CollectionTopBarActions(
    actions: List<CollectionActions>
) {
    var dropDownMenuExpanded by remember { mutableStateOf(false) }
    Icon(
        imageVector = Icons.Outlined.MoreVert,
        contentDescription = stringResource(R.string.more_menu_button),
        modifier = Modifier
            .padding(6.dp)
            .size(40.dp)
            .rotate(90f)
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), CircleShape)
            .padding(5.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = 25.dp,
                    color = Color.White,
                ),
                onClick = {
                    dropDownMenuExpanded = true
                }
            ),
        tint = MaterialTheme.colorScheme.onSurface,
    )
    OptionsDropDown(
        options = actions,
        expanded = dropDownMenuExpanded,
        onDismissRequest = {
            dropDownMenuExpanded = false
        },
        offset = DpOffset(x = 0.dp, y = (-46).dp)
    )
}