package com.github.pakka_papad.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.R
import com.github.pakka_papad.components.MiniSongCard
import com.github.pakka_papad.components.more_options.FolderOptions
import com.github.pakka_papad.components.more_options.OptionsAlertDialog
import com.github.pakka_papad.components.more_options.SongOptions
import com.github.pakka_papad.data.music.MiniSong
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.storage_explorer.Directory
import com.github.pakka_papad.storage_explorer.DirectoryContents

@Composable
fun Files(
    contents: DirectoryContents,
    onDirectoryClicked: (Directory) -> Unit,
    onSongClicked: (index: Int) -> Unit,
    currentSong: Song?,
    onAddToPlaylistClicked: (MiniSong) -> Unit,
    onAddToQueueClicked: (MiniSong) -> Unit,
    onFolderAddToBlacklistRequest: (Directory) -> Unit,
){
    if (contents.directories.isEmpty() && contents.songs.isEmpty()){
        FullScreenSadMessage("Nothing here")
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues(),
    ){
        items(
            items = contents.directories,
            key = { it.absolutePath }
        ){
            Folder(
                folder = it,
                onDirectoryClicked = onDirectoryClicked,
                options = listOf(
                    FolderOptions.Blacklist { onFolderAddToBlacklistRequest(it) }
                )
            )
        }
        itemsIndexed(
            items = contents.songs,
            key = { index, song -> song.location }
        ){index, song ->
            MiniSongCard(
                song = song,
                onSongClicked = { onSongClicked(index) },
                songOptions = listOf(
                    SongOptions.AddToPlaylist{ onAddToPlaylistClicked(song) },
                    SongOptions.AddToQueue{ onAddToQueueClicked(song) },
                ),
                currentlyPlaying = (song.location == currentSong?.location)
            )
        }
    }
}

@Composable
fun Folder(
    folder: Directory,
    onDirectoryClicked: (Directory) -> Unit,
    options: List<FolderOptions>,
){
    val resource = painterResource(R.drawable.ic_baseline_folder_40)
    var showClickIndicator by remember { mutableStateOf(false) }
    var showFileMenu by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onDirectoryClicked(folder)
                showClickIndicator = true
            }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = resource,
            contentDescription = null,
            modifier = Modifier.size(50.dp)
        )
        Text(
            text = folder.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if(showClickIndicator){
            CircularProgressIndicator(
                modifier = Modifier.size(26.dp),
                strokeWidth = 2.dp,
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = null,
                modifier = Modifier
                    .size(26.dp)
                    .clickable(
                        onClick = {
                            showFileMenu = true
                        },
                        indication = rememberRipple(
                            bounded = false,
                            radius = 20.dp
                        ),
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                tint = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
    if (showFileMenu){
        OptionsAlertDialog(
            options = options,
            title = folder.name,
            onDismissRequest = {
                showFileMenu = false
            }
        )
    }
}