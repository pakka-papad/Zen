package com.github.pakka_papad.restore_folder

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.pakka_papad.components.SelectableCard
import com.github.pakka_papad.data.music.BlacklistedFolder

@Composable
fun RestoreFoldersContent(
    folders: List<BlacklistedFolder>,
    selectList: List<Boolean>,
    onSelectChanged: (index: Int, isSelected: Boolean) -> Unit,
) {
    if (folders.size != selectList.size) return
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        itemsIndexed(
            items = folders,
            key = { index, folder -> folder.path }
        ) { index, folder ->
            SelectableBlacklistedFolder(
                folder = folder,
                isSelected = selectList[index],
                onSelectChange = { onSelectChanged(index, it) }
            )
        }
    }
}

@Composable
fun SelectableBlacklistedFolder(
    folder: BlacklistedFolder,
    isSelected: Boolean,
    onSelectChange: (Boolean) -> Unit,
) = SelectableCard(
    isSelected = isSelected,
    onSelectChange = onSelectChange,
    content = {
        Text(
            text = folder.path,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium
        )
    }
)
