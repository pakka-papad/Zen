package com.github.pakka_papad.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import com.github.pakka_papad.Screens
import com.github.pakka_papad.data.music.AlbumWithSongs
import com.github.pakka_papad.data.music.ArtistWithSongs
import com.github.pakka_papad.data.music.Song

@Composable
fun HomeContent(
    currentScreen: Screens,
    onSongClicked: (index: Int) -> Unit,
    songs: List<Song>,
    allSongsListState: LazyListState,
    paddingValues: PaddingValues,
    albumsWithSongs: List<AlbumWithSongs>,
    allAlbumsGridState: LazyGridState,
    onAlbumClicked: (AlbumWithSongs) -> Unit,
    artistsWithSongs: List<ArtistWithSongs>,
    allArtistsListState: LazyListState,
    onArtistClicked: (ArtistWithSongs) -> Unit,
    onSongFavouriteClicked: (Song) -> Unit,
    currentSong: Song?,
    onAddToQueueClicked: (Song) -> Unit,
    onPlayAllClicked: () -> Unit,
    onShuffleClicked: () -> Unit,
){
    when(currentScreen){
        Screens.AllSongs -> {
            AllSongs(
                songs = songs,
                onSongClicked = onSongClicked,
                paddingValues = paddingValues,
                listState = allSongsListState,
                onFavouriteClicked = onSongFavouriteClicked,
                currentSong = currentSong,
                onAddToQueueClicked = onAddToQueueClicked,
                onPlayAllClicked = onPlayAllClicked,
                onShuffleClicked = onShuffleClicked,
            )
        }
        Screens.Albums -> {
            Albums(
                paddingValues = paddingValues,
                albumsWithSongs = albumsWithSongs,
                gridState = allAlbumsGridState,
                onAlbumClicked = onAlbumClicked
            )
        }
        Screens.Artists -> {
            Artists(
                paddingValues = paddingValues,
                artistsWithSongs = artistsWithSongs,
                onArtistClicked = onArtistClicked,
                listState = allArtistsListState
            )
        }
    }
}