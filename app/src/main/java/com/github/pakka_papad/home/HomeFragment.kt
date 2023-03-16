package com.github.pakka_papad.home

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.Constants
import com.github.pakka_papad.R
import com.github.pakka_papad.Screens
import com.github.pakka_papad.collection.CollectionType
import com.github.pakka_papad.components.BottomSheet
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.data.music.*
import com.github.pakka_papad.nowplaying.*
import com.github.pakka_papad.player.ZenBroadcastReceiver
import com.github.pakka_papad.ui.theme.ZenTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var navController: NavController

    private val viewModel: HomeViewModel by viewModels()

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var preferenceProvider: ZenPreferenceProvider

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
        ExperimentalMaterialApi::class
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
        val pendingPreviousIntent = PendingIntent.getBroadcast(
            context, ZenBroadcastReceiver.PREVIOUS_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZenBroadcastReceiver.AUDIO_CONTROL,
                ZenBroadcastReceiver.ZEN_PLAYER_PREVIOUS
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
        val pendingNextIntent = PendingIntent.getBroadcast(
            context, ZenBroadcastReceiver.NEXT_ACTION_REQUEST_CODE,
            Intent(Constants.PACKAGE_NAME).putExtra(
                ZenBroadcastReceiver.AUDIO_CONTROL,
                ZenBroadcastReceiver.ZEN_PLAYER_NEXT
            ),
            PendingIntent.FLAG_IMMUTABLE
        )
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val systemUiController = rememberSystemUiController()
                val themePreference by preferenceProvider.theme.collectAsStateWithLifecycle()
                ZenTheme(themePreference, systemUiController) {
                    var currentScreen by rememberSaveable { mutableStateOf(Screens.Songs) }
                    val scope = rememberCoroutineScope()

                    val songs by viewModel.songs.collectAsStateWithLifecycle()
                    val allSongsListState = rememberLazyListState()

                    val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
                    val songPlaying by viewModel.currentSongPlaying.collectAsStateWithLifecycle()

                    val albums by viewModel.albums.collectAsStateWithLifecycle()
                    val allAlbumsGridState = rememberLazyGridState()

                    val personsWithSongCount by viewModel.personsWithSongCount.collectAsStateWithLifecycle()
                    val selectedPerson by viewModel.selectedPerson.collectAsStateWithLifecycle()
                    val allPersonsListState = rememberLazyListState()

                    val playlistsWithSongCount by viewModel.playlistsWithSongCount.collectAsStateWithLifecycle()
                    val allPlaylistsListState = rememberLazyListState()

                    val genresWithSongCount by viewModel.genresWithSongCount.collectAsStateWithLifecycle()
                    val allGenresListState = rememberLazyListState()

                    val swipeableState = rememberSwipeableState(initialValue = 0)
                    val bottomBarColor = MaterialTheme.colorScheme
                        .surfaceColorAtElevation(3.dp + LocalAbsoluteTonalElevation.current)

                    LaunchedEffect(currentSong) {
                        if (currentSong != null) return@LaunchedEffect
                        if (swipeableState.currentValue != 1) return@LaunchedEffect
                        swipeableState.animateTo(0)
                    }

                    val bottomBarYOffset by remember {
                        derivedStateOf {
                            val progress = if (swipeableState.progress.from == 0){
                                // at 0 or moving away from 0
                                if (swipeableState.progress.to == 0) 1f
                                else if (swipeableState.progress.fraction < 0.25f) 1f-swipeableState.progress.fraction*4
                                else 0f
                            } else {
                                // at 1 or moving away from 1
                                if (swipeableState.progress.to == 1) 0f
                                else if (swipeableState.progress.fraction > 0.75f) 1f-(1f-swipeableState.progress.fraction)*4
                                else 0f
                            }
                            500*(1f-progress)
                        }
                    }
                    val windowInsets = WindowInsets.systemBars.asPaddingValues()

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ){
                        Scaffold(
                            topBar = {
                                HomeTopBar(
                                    onSettingsClicked = {
                                        navController.navigate(R.id.action_homeFragment_to_settingsFragment)
                                    },
                                    onSearchClicked = {
                                        navController.navigate(R.id.action_homeFragment_to_searchFragment)
                                    },
                                )
                            },
                            content = {
                                val dataRetrieved by remember {
                                    derivedStateOf {
                                        songs != null && albums != null && personsWithSongCount != null
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .padding(
                                            top = it.calculateTopPadding(),
                                            bottom = if (currentSong == null) 88.dp else 146.dp,
                                            start = windowInsets.calculateStartPadding(LayoutDirection.Ltr),
                                            end = windowInsets.calculateEndPadding(LayoutDirection.Ltr)
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
                                                        onSongClicked = { index ->
                                                            viewModel.setQueue(songs, index)
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
                                                        onAddToPlaylistsClicked = this@HomeFragment::addToPlaylistClicked,
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
                        )
                        BottomSheet(
                            peekHeight = ( if(currentSong == null) 88.dp else 146.dp) + windowInsets.calculateBottomPadding(),
                            peekContent = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(bottomBarColor)
                                        .padding(
                                            start = windowInsets.calculateStartPadding(LayoutDirection.Ltr),
                                            end = windowInsets.calculateEndPadding(LayoutDirection.Ltr),
                                        )
                                ) {
                                    MiniPlayer(
                                        onPausePlayPressed = {
                                            if (swipeableState.currentValue == 0){
                                                pendingPausePlayIntent.send()
                                            }
                                        },
                                        song = currentSong,
                                        showPlayButton = songPlaying == false,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                    )

                                    var progress by remember { mutableStateOf(0f) }

                                    DisposableEffect(Unit) {
                                        progress = exoPlayer.currentPosition.toFloat() / exoPlayer.duration.toFloat()
                                        val listener = object : Player.Listener {
                                            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                                                super.onMediaItemTransition(mediaItem, reason)
                                                progress = 0f
                                            }
                                        }
                                        exoPlayer.addListener(listener)
                                        onDispose {
                                            exoPlayer.removeListener(listener)
                                        }
                                    }
                                    if (songPlaying == true && swipeableState.currentValue == 0) {
                                        LaunchedEffect(Unit) {
                                            while (true) {
                                                progress = exoPlayer.currentPosition.toFloat() / exoPlayer.duration.toFloat()
                                                delay(40)
                                            }
                                        }
                                    }
                                    LinearProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(2.dp),
                                        progress = progress
                                    )
                                }
                            },
                            content = {
                                currentSong?.let {
                                    val queue = viewModel.queue
                                    val playbackSpeed by preferenceProvider.playbackSpeed.collectAsStateWithLifecycle()
                                    val repeatMode by viewModel.repeatMode.collectAsStateWithLifecycle()
                                    val scaffoldState = rememberBottomSheetScaffoldState()
                                    BackHandler(
                                        enabled = swipeableState.currentValue == 1,
                                        onBack = {
                                            scope.launch {
                                                if (scaffoldState.bottomSheetState.isExpanded) scaffoldState.bottomSheetState.collapse()
                                                else swipeableState.animateTo(0)
                                            }
                                        }
                                    )
                                    BottomSheetScaffold(
                                        scaffoldState = scaffoldState,
                                        topBar = {
                                            NowPlayingTopBar(
                                                onBackArrowPressed = {
                                                    scope.launch {
                                                        swipeableState.animateTo(0)
                                                    }
                                                },
                                                title = it.title,
                                                options = listOf(
                                                    NowPlayingOptions.SaveToPlaylist {
                                                        if(swipeableState.currentValue == 1) saveToPlaylistClicked(queue)
                                                    }
                                                )
                                            )
                                        },
                                        content = { paddingValues ->
                                            NowPlayingScreen(
                                                paddingValues = paddingValues,
                                                song = it,
                                                onPausePlayPressed = pendingPausePlayIntent::send,
                                                onPreviousPressed = pendingPreviousIntent::send,
                                                onNextPressed = pendingNextIntent::send,
                                                songPlaying = songPlaying,
                                                exoPlayer = exoPlayer,
                                                onFavouriteClicked = viewModel::changeFavouriteValue,
                                                onQueueClicked = {
                                                    scope.launch {
                                                        scaffoldState.bottomSheetState.expand()
                                                    }
                                                },
                                                playbackSpeed = playbackSpeed,
                                                updatePlaybackSpeed = preferenceProvider::updatePlaybackSpeed,
                                                repeatMode = repeatMode,
                                                toggleRepeatMode = viewModel::toggleRepeatMode
                                            )
                                        },
                                        sheetContent = {
                                            Queue(
                                                queue = queue,
                                                onFavouriteClicked = viewModel::changeFavouriteValue,
                                                currentSong = it,
                                                onDownArrowClicked = {
                                                    scope.launch {
                                                        scaffoldState.bottomSheetState.collapse()
                                                    }
                                                },
                                                expanded = scaffoldState.bottomSheetState.isExpanded,
                                                exoPlayer = exoPlayer,
                                                onDrag = viewModel::onSongDrag
                                            )
                                        },
                                        sheetShape = RoundedCornerShape(
                                            topStart = 30.dp,
                                            topEnd = 30.dp,
                                            bottomStart = 0.dp,
                                            bottomEnd = 0.dp
                                        ),
                                        sheetElevation = 20.dp,
                                        sheetPeekHeight = 0.dp,
                                        sheetGesturesEnabled = true,
                                    )
                                }
                            },
                            swipeableState = swipeableState,
                        )
                        Box(
                            modifier = Modifier
                                .graphicsLayer {
                                    translationY = bottomBarYOffset
                                }
                        ){
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
                                bottomBarColor = bottomBarColor
                            )
                        }
                    }
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

    private fun saveToPlaylistClicked(queue: List<Song>){
        lifecycleScope.launch {
            val songLocations = queue.map { it.location }
            navController.navigate(
                HomeFragmentDirections
                    .actionHomeFragmentToSelectPlaylistFragment(songLocations.toTypedArray())
            )
        }
    }

    private fun addToPlaylistClicked(song: Song){
        saveToPlaylistClicked(listOf(song))
    }
}