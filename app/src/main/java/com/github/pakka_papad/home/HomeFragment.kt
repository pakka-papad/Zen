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
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.Constants
import com.github.pakka_papad.R
import com.github.pakka_papad.Screens
import com.github.pakka_papad.SharedViewModel
import com.github.pakka_papad.collection.CollectionType
import com.github.pakka_papad.data.music.*
import com.github.pakka_papad.player.ZenBroadcastReceiver
import com.github.pakka_papad.ui.theme.ZenTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var navController: NavController

    private val viewModel by activityViewModels<SharedViewModel>()

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
        ExperimentalLifecycleComposeApi::class
    )
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
                    val scope = rememberCoroutineScope()

                    val songs by viewModel.songs.collectAsState()
                    val allSongsListState = rememberLazyListState()

                    val currentSong by viewModel.currentSong.collectAsState()
                    val songPlaying by viewModel.currentSongPlaying.collectAsState()

                    val albums by viewModel.albums.collectAsState()
                    val allAlbumsGridState = rememberLazyGridState()

                    val personsWithSongCount by viewModel.personsWithSongCount.collectAsStateWithLifecycle()
                    val selectedPerson by viewModel.selectedPerson.collectAsStateWithLifecycle()
                    val allPersonsListState = rememberLazyListState()

                    val playlistsWithSongCount by viewModel.playlistsWithSongCount.collectAsState()
                    val allPlaylistsListState = rememberLazyListState()

                    val genresWithSongCount by viewModel.genresWithSongCount.collectAsStateWithLifecycle()
                    val allGenresListState = rememberLazyListState()

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
                                key3 = personsWithSongCount
                            ) {
                                dataRetrieved =
                                    songs != null && albums != null && personsWithSongCount != null
                            }
                            Box(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .windowInsetsPadding(
                                        WindowInsets.systemBars.only(
                                            WindowInsetsSides.Horizontal
                                        )
                                    )
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
                                                                arrayOf(it.location)
                                                            )
                                                        )
                                                    },
                                                    onBlacklistClicked = viewModel::onSongBlacklist
                                                )
                                            }
                                            Screens.Albums -> {
                                                Albums(
                                                    albums = albums,
                                                    gridState = allAlbumsGridState,
                                                    onAlbumClicked = this@HomeFragment::navigateToCollection
                                                )
                                            }
                                            Screens.Artists -> {
                                                Persons(
                                                    personsWithSongCount = personsWithSongCount,
                                                    onPersonClicked = this@HomeFragment::navigateToCollection,
                                                    listState = allPersonsListState,
                                                    selectedPerson = selectedPerson,
                                                    onPersonSelect = viewModel::onPersonSelect
                                                )
                                            }
                                            Screens.Playlists -> {
                                                Playlists(
                                                    playlistsWithSongCount = playlistsWithSongCount,
                                                    onPlaylistClicked = this@HomeFragment::navigateToCollection,
                                                    listState = allPlaylistsListState,
                                                    onPlaylistCreate = viewModel::onPlaylistCreate,
                                                    onFavouritesClicked = this@HomeFragment::navigateToCollection,
                                                    onDeletePlaylistClicked = viewModel::deletePlaylist,
                                                )
                                            }
                                            Screens.Genres -> {
                                                Genres(
                                                    genresWithSongCount = genresWithSongCount,
                                                    listState = allGenresListState,
                                                    onGenreClicked = this@HomeFragment::navigateToCollection
                                                )
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
                                    if (currentScreen == it){
                                        scope.launch {
                                            when(it){
                                                Screens.Songs -> allSongsListState.scrollToItem(0)
                                                Screens.Albums -> allAlbumsGridState.scrollToItem(0)
                                                Screens.Artists -> allPersonsListState.scrollToItem(0)
                                                Screens.Playlists -> allPlaylistsListState.scrollToItem(0)
                                                Screens.Genres -> allGenresListState.scrollToItem(0)
                                            }
                                        }
                                    } else {
                                        currentScreen = it
                                    }
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

    private fun navigateToCollection(album: Album){
        navController.navigate(
            HomeFragmentDirections.actionHomeFragmentToCollectionFragment(
                CollectionType(CollectionType.AlbumType,album.name)
            )
        )
    }

    private fun navigateToCollection(personWithSongCount: PersonWithSongCount) {
        when(personWithSongCount){
            is ArtistWithSongCount -> {
                navController.navigate(
                    HomeFragmentDirections.actionHomeFragmentToCollectionFragment(
                        CollectionType(CollectionType.ArtistType,personWithSongCount.name)
                    )
                )
            }
            is AlbumArtistWithSongCount -> {
                navController.navigate(
                    HomeFragmentDirections.actionHomeFragmentToCollectionFragment(
                        CollectionType(CollectionType.AlbumArtistType,personWithSongCount.name)
                    )
                )
            }
            is ComposerWithSongCount -> {
                navController.navigate(
                    HomeFragmentDirections.actionHomeFragmentToCollectionFragment(
                        CollectionType(CollectionType.ComposerType,personWithSongCount.name)
                    )
                )
            }
            is LyricistWithSongCount -> {
                navController.navigate(
                    HomeFragmentDirections.actionHomeFragmentToCollectionFragment(
                        CollectionType(CollectionType.LyricistType,personWithSongCount.name)
                    )
                )
            }
        }
    }

    private fun navigateToCollection(playlistId: Long){
        navController.navigate(
            HomeFragmentDirections.actionHomeFragmentToCollectionFragment(
                CollectionType(CollectionType.PlaylistType,playlistId.toString())
            )
        )
    }

    private fun navigateToCollection(genreWithSongCount: GenreWithSongCount){
        navController.navigate(
            HomeFragmentDirections.actionHomeFragmentToCollectionFragment(
                CollectionType(CollectionType.GenreType,genreWithSongCount.genreName)
            )
        )
    }

    private fun navigateToCollection(){
        navController.navigate(
            HomeFragmentDirections.actionHomeFragmentToCollectionFragment(
                CollectionType(CollectionType.FavouritesType)
            )
        )
    }
}