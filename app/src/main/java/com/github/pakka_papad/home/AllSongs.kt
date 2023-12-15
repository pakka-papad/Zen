package com.github.pakka_papad.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.window.DialogProperties
import com.github.pakka_papad.R
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.components.SongCardV1
import com.github.pakka_papad.components.more_options.SongOptions
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.formatToDate

@Composable
fun AllSongs(
    songs: List<Song>?,
    onSongClicked: (index: Int) -> Unit,
    listState: LazyListState,
    onFavouriteClicked: (Song) -> Unit,
    currentSong: Song?,
    onAddToQueueClicked: (Song) -> Unit,
    onPlayAllClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
    onAddToPlaylistsClicked: (Song) -> Unit,
    onBlacklistClicked: (Song) -> Unit,
) {
    if (songs == null) return
    if (songs.isEmpty()) {
        FullScreenSadMessage(
            message = stringResource(R.string.no_songs_found_on_this_device),
            paddingValues = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues()
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = listState,
            contentPadding = WindowInsets.systemBars.only(WindowInsetsSides.Bottom).asPaddingValues()
        ) {
            item {
                PlayShuffleCard(
                    onPlayAllClicked = onPlayAllClicked,
                    onShuffleClicked = onShuffleClicked,
                )
            }
            itemsIndexed(
                items = songs,
                key = { index, song ->
                    song.location
                }
            ) { index, song ->
                var infoVisible by remember { mutableStateOf(false) }
                SongCardV1(
                    song = song,
                    onSongClicked = {
                        onSongClicked(index)
                    },
                    onFavouriteClicked = onFavouriteClicked,
                    songOptions = listOf(
                        SongOptions.Info { infoVisible = true },
                        SongOptions.AddToQueue { onAddToQueueClicked(song) },
                        SongOptions.AddToPlaylist { onAddToPlaylistsClicked(song) },
                        SongOptions.Blacklist { onBlacklistClicked(song) }
                    ),
                    currentlyPlaying = (song.location == currentSong?.location)
                )
                if (infoVisible) {
                    SongInfo(song) { infoVisible = false }
                }
            }
        }
    }
}

@Composable
fun SongInfo(
    song: Song,
    onDismissRequest: () -> Unit,
) {
    AlertDialog(
        properties = DialogProperties(

        ),
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = song.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onDismissRequest,
            ) {
                Text(
                    text = stringResource(R.string.close),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        },
        text = {
            val spanStyle = SpanStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize*(1.1f)
            )
            val scrollState = rememberScrollState()
            Text(
                text = buildAnnotatedString {
                    append("${stringResource(R.string.location)}\n")
                    withStyle(spanStyle) { append(song.location) }
                    append("\n\n${stringResource(R.string.size)}\n")
                    withStyle(spanStyle) { append(song.size) }
                    append("\n\n${stringResource(R.string.album)}\n")
                    withStyle(spanStyle) { append(song.album) }
                    append("\n\n${stringResource(R.string.artist)}\n")
                    withStyle(spanStyle) { append(song.artist) }
                    append("\n\n${stringResource(R.string.album_artist)}\n")
                    withStyle(spanStyle) { append(song.albumArtist) }
                    append("\n\n${stringResource(R.string.composer)}\n")
                    withStyle(spanStyle) { append(song.composer) }
                    append("\n\n${stringResource(R.string.lyricist)}\n")
                    withStyle(spanStyle) { append(song.lyricist) }
                    append("\n\n${stringResource(R.string.genre)}\n")
                    withStyle(spanStyle) { append(song.genre) }
                    append("\n\n${stringResource(R.string.year)}\n")
                    withStyle(spanStyle) { append(if (song.year == 0) stringResource(R.string.unknown) else song.year.toString()) }
                    append("\n\n${stringResource(R.string.duration)}\n")
                    withStyle(spanStyle) { append(if (song.durationMillis == 0L) stringResource(R.string.unknown) else song.durationFormatted) }
                    append("\n\n${stringResource(R.string.play_count)}\n")
                    withStyle(spanStyle) { append(song.playCount.toString()) }
                    append("\n\n${stringResource(R.string.last_played_on)}\n")
                    withStyle(spanStyle) { append(if (song.lastPlayed == null) stringResource(R.string.never) else song.lastPlayed.formatToDate()) }
                    append("\n\n${stringResource(R.string.mime_type)}\n")
                    withStyle(spanStyle) { append(song.mimeType ?: stringResource(R.string.unknown)) }
                },
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
            )
        }
    )
}

