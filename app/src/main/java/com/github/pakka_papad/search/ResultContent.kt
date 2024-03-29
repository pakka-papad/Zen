package com.github.pakka_papad.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.pakka_papad.components.SongCardV3
import com.github.pakka_papad.data.music.Album
import com.github.pakka_papad.data.music.AlbumArtist
import com.github.pakka_papad.data.music.Artist
import com.github.pakka_papad.data.music.Composer
import com.github.pakka_papad.data.music.Genre
import com.github.pakka_papad.data.music.Lyricist
import com.github.pakka_papad.data.music.Playlist
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.home.AlbumCard

@Composable
fun TextCard(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(12.dp),
        style = MaterialTheme.typography.titleMedium,
    )
}

@Composable
fun ResultContent(
    contentPadding: PaddingValues,
    searchResult: SearchResult,
    showGrid: Boolean,
    searchType: SearchType,
    onSongClicked: (Song) -> Unit,
    onAlbumClicked: (Album) -> Unit,
    onArtistClicked: (Artist) -> Unit,
    onAlbumArtistClicked: (AlbumArtist) -> Unit,
    onComposerClicked: (Composer) -> Unit,
    onLyricistClicked: (Lyricist) -> Unit,
    onGenreClicked: (Genre) -> Unit,
    onPlaylistClicked: (Playlist) -> Unit,
){
    LazyVerticalGrid(
        columns = if (showGrid) GridCells.Adaptive(150.dp) else GridCells.Fixed(1),
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        when(searchType){
            SearchType.Songs -> {
                items(
                    items = searchResult.songs,
                    key = { it.location }
                ){ song ->
                    SongCardV3(
                        song = song,
                        onSongClicked = onSongClicked,
                    )
                }
            }
            SearchType.Albums -> {
                items(
                    items = searchResult.albums,
                    key = { it.name }
                ){ album ->
                    AlbumCard(
                        album = album,
                        onAlbumClicked = onAlbumClicked,
                    )
                }
            }
            SearchType.Artists -> {
                items(
                    items = searchResult.artists,
                    key = { it.name }
                ){ artist ->
                    TextCard(
                        text = artist.name,
                        onClick = { onArtistClicked(artist) },
                    )
                }
            }
            SearchType.AlbumArtists -> {
                items(
                    items = searchResult.albumArtists,
                    key = { it.name }
                ){ albumArtist ->
                    TextCard(
                        text = albumArtist.name,
                        onClick = { onAlbumArtistClicked(albumArtist) },
                    )
                }
            }
            SearchType.Lyricists -> {
                items(
                    items = searchResult.lyricists,
                    key = { it.name }
                ){ lyricist ->
                    TextCard(
                        text = lyricist.name,
                        onClick = { onLyricistClicked(lyricist) },
                    )
                }
            }
            SearchType.Composers -> {
                items(
                    items = searchResult.composers,
                    key = { it.name }
                ){ composer ->
                    TextCard(
                        text = composer.name,
                        onClick = { onComposerClicked(composer) },
                    )
                }
            }
            SearchType.Genres -> {
                items(
                    items = searchResult.genres,
                    key = { it.genre }
                ){ genre ->
                    TextCard(
                        text = genre.genre,
                        onClick = { onGenreClicked(genre) },
                    )
                }
            }
            SearchType.Playlists -> {
                items(
                    items = searchResult.playlists,
                    key = { it.playlistId }
                ){ playlist ->
                    TextCard(
                        text = playlist.playlistName,
                        onClick = { onPlaylistClicked(playlist) },
                    )
                }
            }
        }
    }
}