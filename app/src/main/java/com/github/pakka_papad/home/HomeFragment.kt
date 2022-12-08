package com.github.pakka_papad.home

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.LayoutDirection
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.Constants
import com.github.pakka_papad.R
import com.github.pakka_papad.Screens
import com.github.pakka_papad.SharedViewModel
import com.github.pakka_papad.collection.CollectionType
import com.github.pakka_papad.player.ZenBroadcastReceiver
import com.github.pakka_papad.ui.theme.ZenTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var navController: NavController

    private val viewModel by activityViewModels<SharedViewModel>()

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        val pendingPausePlayIntent = PendingIntent.getBroadcast(
            context, ZenBroadcastReceiver.PAUSE_PLAY_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZenBroadcastReceiver.AUDIO_CONTROL,
                ZenBroadcastReceiver.ZEN_PLAYER_PAUSE_PLAY
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
        return ComposeView(requireContext()).apply {
            setContent {
                val systemUiController = rememberSystemUiController(requireActivity().window)
                val themePreference by viewModel.theme.collectAsState()
                ZenTheme(
                    themePreference = themePreference,
                    systemUiController = systemUiController
                ) {
                    var currentScreen by rememberSaveable { mutableStateOf(Screens.Songs) }
                    val songs by viewModel.songs.collectAsState()
                    val allSongsListState = rememberLazyListState()
                    val currentSong by viewModel.currentSong.collectAsState()
                    val songPlaying by viewModel.currentSongPlaying.collectAsState()
                    val albumsWithSongs by viewModel.albumsWithSongs.collectAsState()
                    val allAlbumsGridState = rememberLazyGridState()
                    val artistsWithSongs by viewModel.artistsWithSongs.collectAsState()
                    val allArtistsListState = rememberLazyListState()
                    val playlists by viewModel.playlists.collectAsState()
                    val allPlaylistsListState = rememberLazyListState()
                    Scaffold(
                        topBar = {
                            HomeTopBar(
                                onSettingsClicked = {
                                    navController.navigate(R.id.action_homeFragment_to_settingsFragment)
                                },
                                onSearchClicked = {
                                    viewModel.search("")
                                    navController.navigate(R.id.action_homeFragment_to_searchFragment)
                                },
                            )
                        },
                        content = { paddingValues ->
                            val insetsPadding =
                                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal).asPaddingValues()
                            var dataRetrieved by remember { mutableStateOf(false) }
                            LaunchedEffect(
                                key1 = songs,
                                key2 = albumsWithSongs,
                                key3 = artistsWithSongs
                            ) {
                                dataRetrieved =
                                    songs != null && albumsWithSongs != null && artistsWithSongs != null
                            }
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                if (!dataRetrieved) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                } else {
                                    HomeContent(
                                        currentScreen = currentScreen,
                                        onSongClicked = { index ->
                                            viewModel.setQueue(songs!!, index)
                                        },
                                        songs = songs!!,
                                        allSongsListState = allSongsListState,
                                        paddingValues = PaddingValues(
                                            bottom = paddingValues.calculateBottomPadding(),
                                            top = paddingValues.calculateTopPadding(),
                                            start = insetsPadding.calculateStartPadding(LayoutDirection.Ltr),
                                            end = insetsPadding.calculateEndPadding(LayoutDirection.Ltr)
                                        ),
                                        albumsWithSongs = albumsWithSongs!!,
                                        allAlbumsGridState = allAlbumsGridState,
                                        onAlbumClicked = { albumWithSongs ->
                                            navController.navigate(
                                                HomeFragmentDirections.actionHomeFragmentToCollectionFragment(
                                                    CollectionType.AlbumType(albumWithSongs.album.name)
                                                )
                                            )
                                        },
                                        artistsWithSongs = artistsWithSongs!!,
                                        onArtistClicked = { artistWithSongs ->
                                            navController.navigate(
                                                HomeFragmentDirections.actionHomeFragmentToCollectionFragment(
                                                    CollectionType.ArtistType(artistWithSongs.artist.name)
                                                )
                                            )
                                        },
                                        allArtistsListState = allArtistsListState,
                                        onSongFavouriteClicked = viewModel::changeFavouriteValue,
                                        currentSong = currentSong,
                                        onAddToQueueClicked = viewModel::addToQueue,
                                        onPlayAllClicked = {
                                            viewModel.setQueue(songs!!, 0)
                                        },
                                        onShuffleClicked = {
                                            viewModel.shufflePlay(songs!!)
                                        },
                                        playlists = playlists,
                                        allPlaylistListState = allPlaylistsListState,
                                        onPlaylistClicked = { playlistId ->
                                            navController.navigate(
                                                HomeFragmentDirections.actionHomeFragmentToCollectionFragment(
                                                    CollectionType.PlaylistType(playlistId)
                                                )
                                            )
                                        },
                                        onPlaylistCreate = viewModel::onPlaylistCreate,
                                        onAddToPlaylistsClicked = {
                                            navController.navigate(
                                                HomeFragmentDirections.actionHomeFragmentToSelectPlaylistFragment(it.location)
                                            )
                                        },
                                    )
                                }
                            }
                        },
                        bottomBar = {

                            HomeBottomBar(
                                currentScreen = currentScreen,
                                onScreenChange = {
                                    currentScreen = it
                                },
                                songPlaying = songPlaying,
                                onPlayPausePressed = pendingPausePlayIntent::send,
                                currentSong = currentSong,
                                onMiniPlayerClicked = {
                                    navController.navigate(R.id.action_homeFragment_to_nowPlaying)
                                },
                                mediaPlayer = exoPlayer,
                                systemUiController = systemUiController,
                            )
                        },
                    )
                }
            }
        }
    }
}