package com.github.pakka_papad.home

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
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

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
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
                ZenTheme(themePreference, systemUiController) {
                    var currentScreen by rememberSaveable { mutableStateOf(Screens.Songs) }

                    val songs by viewModel.songs.collectAsState()
                    val allSongsListState = rememberLazyListState()

                    val currentSong by viewModel.currentSong.collectAsState()
                    val songPlaying by viewModel.currentSongPlaying.collectAsState()

                    val albums by viewModel.albums.collectAsState()
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
                                    viewModel.updateQuery("")
                                    navController.navigate(R.id.action_homeFragment_to_searchFragment)
                                },
                            )
                        },
                        content = { paddingValues ->
                            var dataRetrieved by remember { mutableStateOf(false) }
                            LaunchedEffect(
                                key1 = songs,
                                key2 = albums,
                                key3 = artistsWithSongs
                            ) {
                                dataRetrieved =
                                    songs != null && albums != null && artistsWithSongs != null
                            }
                            Box(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal))
                                    .fillMaxSize(),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                if (!dataRetrieved) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                } else {
                                    AnimatedContent(targetState = currentScreen) { targetScreen ->
                                        when (targetScreen) {
                                            Screens.Songs -> {
                                                AllSongs(
                                                    songs = songs,
                                                    onSongClicked = {
                                                        viewModel.setQueue(songs, it)
                                                    },
                                                    listState = allSongsListState,
                                                    onFavouriteClicked = viewModel::changeFavouriteValue,
                                                    currentSong = currentSong,
                                                    onAddToQueueClicked = viewModel::addToQueue,
                                                    onPlayAllClicked = {
                                                        viewModel.setQueue(songs)
                                                    },
                                                    onShuffleClicked = {
                                                        viewModel.shufflePlay(songs)
                                                    },
                                                    onAddToPlaylistsClicked = {
                                                        navController.navigate(
                                                            HomeFragmentDirections.actionHomeFragmentToSelectPlaylistFragment(
                                                                it.location
                                                            )
                                                        )
                                                    },
                                                )
                                            }
                                            Screens.Albums -> {
                                                Albums(
                                                    albums = albums,
                                                    gridState = allAlbumsGridState,
                                                    onAlbumClicked = {
                                                        navController.navigate(
                                                            HomeFragmentDirections.actionHomeFragmentToCollectionFragment(
                                                                CollectionType.AlbumType(it.name)
                                                            )
                                                        )
                                                    }
                                                )
                                            }
                                            Screens.Artists -> {
                                                Artists(
                                                    artistsWithSongs = artistsWithSongs,
                                                    onArtistClicked = {
                                                        navController.navigate(
                                                            HomeFragmentDirections.actionHomeFragmentToCollectionFragment(
                                                                CollectionType.ArtistType(it.artist.name)
                                                            )
                                                        )
                                                    },
                                                    listState = allArtistsListState
                                                )
                                            }
                                            Screens.Playlists -> {
                                                Playlists(
                                                    playlists = playlists,
                                                    onPlaylistClicked = {
                                                        navController.navigate(
                                                            HomeFragmentDirections.actionHomeFragmentToCollectionFragment(
                                                                CollectionType.PlaylistType(it)
                                                            )
                                                        )
                                                    },
                                                    listState = allPlaylistsListState,
                                                    onPlaylistCreate = viewModel::onPlaylistCreate
                                                )
                                            }
                                            Screens.Genres -> {

                                            }
                                        }
                                    }
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