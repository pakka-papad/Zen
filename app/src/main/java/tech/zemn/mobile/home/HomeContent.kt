package tech.zemn.mobile.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import tech.zemn.mobile.Screens
import tech.zemn.mobile.data.music.Album
import tech.zemn.mobile.data.music.Song

@Composable
fun HomeContent(
    currentScreen: Screens,
    onSongClicked: (Song) -> Unit,
    songs: List<Song>,
    allSongsListState: LazyListState,
    paddingValues: PaddingValues,
    albums: List<Album>,
    allAlbumsGridState: LazyGridState,
    onAlbumClicked: (String) -> Unit,
){
    when(currentScreen){
        is Screens.Home.AllSongs -> {
            AllSongs(
                songs = songs,
                onSongClicked = onSongClicked,
                paddingValues = paddingValues,
                listState = allSongsListState
            )
        }
        is Screens.Home.Albums -> {
            Albums(
                paddingValues = paddingValues,
                albums = albums,
                gridState = allAlbumsGridState,
                onAlbumClicked = onAlbumClicked
            )
        }
        is Screens.Home.Artists -> {

        }
        else -> throw RuntimeException("Invalid currentScreen parameter")
    }
}