package com.github.pakka_papad.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SelectableCard(
    isSelected: Boolean,
    onSelectChange: (isSelected: Boolean) -> Unit,
    content: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ){
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectChange
        )
        content()
    }
}