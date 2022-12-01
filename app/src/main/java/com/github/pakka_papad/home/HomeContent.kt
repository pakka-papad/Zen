package com.github.pakka_papad.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import com.github.pakka_papad.Screens
import com.github.pakka_papad.data.music.*

@Composable
fun HomeContent(
    // common
    currentScreen: Screens,
    paddingValues: PaddingValues,

    // songs list
    songs: List<Song>,
    onSongClicked: (index: Int) -> Unit,
    allSongsListState: LazyListState,
    onSongFavouriteClicked: (Song) -> Unit,
    currentSong: Song?,
    onAddToQueueClicked: (Song) -> Unit,
    onPlayAllClicked: () -> Unit,
    onShuffleClicked: () -> Unit,

    // albums list
    albumsWithSongs: List<AlbumWithSongs>,
    allAlbumsGridState: LazyGridState,
    onAlbumClicked: (AlbumWithSongs) -> Unit,

    // artists list
    artistsWithSongs: List<ArtistWithSongs>,
    allArtistsListState: LazyListState,
    onArtistClicked: (ArtistWithSongs) -> Unit,

    // playlists list
    playlists: List<Playlist>,
    allPlaylistListState: LazyListState,
    onPlaylistClicked: (Long) -> Unit,
    onPlaylistCreate: (String) -> Unit,
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
        Screens.Playlists -> {
            Playlists(
                paddingValues = paddingValues,
                playlists = playlists,
                onPlaylistClicked = onPlaylistClicked,
                listState = allPlaylistListState,
                onPlaylistCreate = onPlaylistCreate
            )
        }
    }
}