package com.github.pakka_papad.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.storage_explorer.StorageFile
import com.github.pakka_papad.R

@Composable
fun Files(
    files: List<StorageFile>,
    onFileClicked: (StorageFile) -> Unit,
){
    if (files.isEmpty()){
        FullScreenSadMessage("Nothing here")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
    ){
        items(
            items = files,
        ){
            File(
                file = it,
                onFileClicked = onFileClicked,
            )
        }
    }
}

@Composable
fun File(
    file: StorageFile,
    onFileClicked: (StorageFile) -> Unit,
){
    val resource = painterResource(
        if (file.isDirectory) R.drawable.ic_outline_folder_40
        else R.drawable.ic_outline_music_note_40
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onFileClicked(file) }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = resource,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = file.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}