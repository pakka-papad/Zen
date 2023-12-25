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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.Constants
import com.github.pakka_papad.Screens
import com.github.pakka_papad.components.BottomSheet
import com.github.pakka_papad.components.Snackbar
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.nowplaying.NowPlayingOptions
import com.github.pakka_papad.nowplaying.NowPlayingScreen
import com.github.pakka_papad.nowplaying.NowPlayingTopBar
import com.github.pakka_papad.nowplaying.PlayerHelper
import com.github.pakka_papad.nowplaying.Queue
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

    @OptIn(
        ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class,
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

        val navHelper = HomeNavHelper(navController, lifecycle)
        val playerHelper = PlayerHelper(exoPlayer)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val systemUiController = rememberSystemUiController()
                val themePreference by preferenceProvider.theme.collectAsStateWithLifecycle()
                ZenTheme(themePreference, systemUiController) {
                    val selectedTabs by preferenceProvider.selectedTabs.collectAsStateWithLifecycle()
                    var currentScreen by rememberSaveable { mutableStateOf(Screens.Songs) }
                    val scope = rememberCoroutineScope()

                    val sortOrder by preferenceProvider.sortOrder.collectAsStateWithLifecycle()

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

                    val files by viewModel.filesInCurrentDestination.collectAsStateWithLifecycle()

                    val dataRetrieved by remember {
                        derivedStateOf {
                            songs != null && albums != null && personsWithSongCount != null
                        }
                    }

                    val swipeableState = rememberSwipeableState(initialValue = 0)
                    val bottomBarColor = MaterialTheme.colorScheme
                        .surfaceColorAtElevation(3.dp + LocalAbsoluteTonalElevation.current)

                    LaunchedEffect(currentSong) {
                        if (currentSong != null) return@LaunchedEffect
                        if (swipeableState.currentValue != 1) return@LaunchedEffect
                        swipeableState.animateTo(0)
                    }

                    val snackbarHostState = remember { SnackbarHostState() }

                    val message by viewModel.message.collectAsStateWithLifecycle()
                    LaunchedEffect(key1 = message){
                        if (message.isEmpty()) return@LaunchedEffect
                        snackbarHostState.showSnackbar(message)
                    }

                    val bottomBarYOffset by remember {
                        derivedStateOf {
                            val progress = if (swipeableState.progress.from == 0) {
                                // at 0 or moving away from 0
                                if (swipeableState.progress.to == 0) 1f
                                else if (swipeableState.progress.fraction < 0.25f) 1f - swipeableState.progress.fraction * 4
                                else 0f
                            } else {
                                // at 1 or moving away from 1
                                if (swipeableState.progress.to == 1) 0f
                                else if (swipeableState.progress.fraction > 0.75f) 1f - (1f - swipeableState.progress.fraction) * 4
                                else 0f
                            }
                            500 * (1f - progress)
                        }
                    }
                    val windowInsets = WindowInsets.systemBars.asPaddingValues()

                    val queue = viewModel.queue
                    val playbackParams by preferenceProvider.playbackParams.collectAsStateWithLifecycle()
                    val repeatMode by viewModel.repeatMode.collectAsStateWithLifecycle()
                    val playerScaffoldState = rememberBottomSheetScaffoldState(
                        bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = false)
                    )

                    val isExplorerAtRoot by viewModel.isExplorerAtRoot.collectAsStateWithLifecycle()

                    val isQueueBottomSheetExpanded by remember(playerScaffoldState.bottomSheetState) {
                        derivedStateOf {
                            playerScaffoldState.bottomSheetState.currentValue
                                .equals(SheetValue.Expanded)
                        }
                    }

                    BackHandler(
                        enabled = (currentScreen == Screens.Folders && !isExplorerAtRoot) || swipeableState.currentValue == 1,
                        onBack = {
                            if (swipeableState.currentValue == 1) {
                                scope.launch {
                                    if (isQueueBottomSheetExpanded) playerScaffoldState.bottomSheetState.hide()
                                    else swipeableState.animateTo(0)
                                }
                            } else {
                                viewModel.moveToParent()
                            }
                        }
                    )

                    val songScreenSongClicked = remember {
                        { index: Int -> viewModel.setQueue(songs, index) }
                    }
                    val songScreenPlayAllClicked = remember { { viewModel.setQueue(songs) } }
                    val songScreenShuffleClicked = remember { { viewModel.shufflePlay(songs) } }
                    val miniPlayerPlayPauseClicked = remember { {
                        if (swipeableState.currentValue == 0) { pendingPausePlayIntent.send() }
                    } }
                    val nowPlayingBackArrowClicked = remember<() -> Unit> { {
                        scope.launch { swipeableState.animateTo(0) }
                    } }
                    val expandQueueBottomSheet = remember<() -> Unit> {
                        { scope.launch { playerScaffoldState.bottomSheetState.expand() } }
                    }
                    val updateScreen = remember<(Screens) -> Unit> { {
                        if (currentScreen == it) {
                            scope.launch {
                                when (it) {
                                    Screens.Songs -> allSongsListState.scrollToItem(0)
                                    Screens.Albums -> allAlbumsGridState.scrollToItem(0)
                                    Screens.Artists -> allPersonsListState.scrollToItem(0)
                                    Screens.Playlists -> allPlaylistsListState.scrollToItem(0)
                                    Screens.Genres -> allGenresListState.scrollToItem(0)
                                    else -> {}
                                }
                            }
                        } else {
                            currentScreen = it
                        }
                    } }

                    val homeScreenBottomPadding by remember(currentSong) {
                        derivedStateOf {
                            if (currentSong == null) 88.dp else 146.dp
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Scaffold(
                            modifier = Modifier
                                .padding(bottom = homeScreenBottomPadding),
                            topBar = {
                                HomeTopBar(
                                    onSettingsClicked = navHelper::navigateToSettings,
                                    onSearchClicked = navHelper::navigateToSearch,
                                    currentScreen = currentScreen,
                                    onSortOptionChosen = viewModel::saveSortOption,
                                    currentSortOrder = sortOrder,
                                )
                            },
                            content = {
                                Box(
                                    modifier = Modifier
                                        .padding(
                                            top = it.calculateTopPadding(),
                                            start = windowInsets.calculateStartPadding(
                                                LayoutDirection.Ltr
                                            ),
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
                                        AnimatedContent(
                                            targetState = currentScreen,
                                            label = ""
                                        ) { targetScreen ->
                                            when (targetScreen) {
                                                Screens.Songs -> {
                                                    AllSongs(
                                                        songs = songs,
                                                        onSongClicked = songScreenSongClicked,
                                                        listState = allSongsListState,
                                                        onFavouriteClicked = viewModel::changeFavouriteValue,
                                                        currentSong = currentSong,
                                                        onAddToQueueClicked = viewModel::addToQueue,
                                                        onPlayAllClicked = songScreenPlayAllClicked,
                                                        onShuffleClicked = songScreenShuffleClicked,
                                                        onAddToPlaylistsClicked = navHelper::navigateToChoosePlaylist,
                                                        onBlacklistClicked = viewModel::onSongBlacklist
                                                    )
                                                }
                                                Screens.Albums -> {
                                                    Albums(
                                                        albums = albums,
                                                        gridState = allAlbumsGridState,
                                                        onAlbumClicked = navHelper::navigateToViewDetails
                                                    )
                                                }
                                                Screens.Artists -> {
                                                    Persons(
                                                        personsWithSongCount = personsWithSongCount,
                                                        onPersonClicked = navHelper::navigateToViewDetails,
                                                        listState = allPersonsListState,
                                                        selectedPerson = selectedPerson,
                                                        onPersonSelect = viewModel::onPersonSelect
                                                    )
                                                }
                                                Screens.Playlists -> {
                                                    Playlists(
                                                        playlistsWithSongCount = playlistsWithSongCount,
                                                        onPlaylistClicked = navHelper::navigateToViewDetails,
                                                        listState = allPlaylistsListState,
                                                        onPlaylistCreate = viewModel::onPlaylistCreate,
                                                        onFavouritesClicked = navHelper::navigateToViewDetails,
                                                        onDeletePlaylistClicked = viewModel::deletePlaylist,
                                                    )
                                                }
                                                Screens.Genres -> {
                                                    Genres(
                                                        genresWithSongCount = genresWithSongCount,
                                                        listState = allGenresListState,
                                                        onGenreClicked = navHelper::navigateToViewDetails
                                                    )
                                                }
                                                Screens.Folders -> {
                                                    Files(
                                                        contents = files,
                                                        onDirectoryClicked = viewModel::onFileClicked,
                                                        onSongClicked = viewModel::onFileClicked,
                                                        currentSong = currentSong,
                                                        onAddToPlaylistClicked = navHelper::navigateToChoosePlaylist,
                                                        onAddToQueueClicked = viewModel::addToQueue,
                                                        onFolderAddToBlacklistRequest = viewModel::onFolderBlacklist
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            snackbarHost = {
                                SnackbarHost(
                                    hostState = snackbarHostState,
                                    snackbar = { Snackbar(it) }
                                )
                            }
                        )
                        BottomSheet(
                            peekHeight = homeScreenBottomPadding + windowInsets.calculateBottomPadding(),
                            peekContent = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(bottomBarColor)
                                        .padding(
                                            start = windowInsets.calculateStartPadding(
                                                LayoutDirection.Ltr
                                            ),
                                            end = windowInsets.calculateEndPadding(LayoutDirection.Ltr),
                                        )
                                ) {
                                    MiniPlayer(
                                        onPausePlayPressed = miniPlayerPlayPauseClicked,
                                        song = currentSong,
                                        showPlayButton = songPlaying == false,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp)
                                    )

                                    var progress by remember { mutableStateOf(0f) }

                                    DisposableEffect(Unit) {
                                        progress =
                                            playerHelper.currentPosition/ playerHelper.duration
                                        val listener = object : Player.Listener {
                                            override fun onMediaItemTransition(
                                                mediaItem: MediaItem?,
                                                reason: Int
                                            ) {
                                                super.onMediaItemTransition(mediaItem, reason)
                                                progress = 0f
                                            }
                                        }
                                        playerHelper.addListener(listener)
                                        onDispose {
                                            playerHelper.removeListener(listener)
                                        }
                                    }
                                    if (songPlaying == true && swipeableState.currentValue == 0) {
                                        LaunchedEffect(Unit) {
                                            while (true) {
                                                progress =
                                                    playerHelper.currentPosition / playerHelper.duration
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
                                    BottomSheetScaffold(
                                        scaffoldState = playerScaffoldState,
                                        topBar = {
                                            NowPlayingTopBar(
                                                onBackArrowPressed = nowPlayingBackArrowClicked,
                                                title = it.title,
                                                options = listOf(
                                                    NowPlayingOptions.SaveToPlaylist {
                                                        if (swipeableState.currentValue == 1)
                                                            navHelper.navigateToChoosePlaylist(queue)
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
                                                playerHelper = playerHelper,
                                                currentSongPlaying = songPlaying,
                                                onFavouriteClicked = viewModel::changeFavouriteValue,
                                                onQueueClicked = expandQueueBottomSheet,
                                                repeatMode = repeatMode,
                                                toggleRepeatMode = viewModel::toggleRepeatMode,
                                                playbackParams = playbackParams,
                                                updatePlaybackParams = preferenceProvider::updatePlaybackParams
                                            )
                                        },
                                        sheetContent = {
                                            Queue(
                                                queue = queue,
                                                onFavouriteClicked = viewModel::changeFavouriteValue,
                                                currentSong = it,
                                                expanded = isQueueBottomSheetExpanded,
                                                playerHelper = playerHelper,
                                                onDrag = viewModel::onSongDrag
                                            )
                                        },
                                        sheetShape = RoundedCornerShape(
                                            topStart = 30.dp,
                                            topEnd = 30.dp,
                                            bottomStart = 0.dp,
                                            bottomEnd = 0.dp
                                        ),
                                        sheetPeekHeight = 0.dp,
                                        sheetContainerColor = MaterialTheme.colorScheme.secondaryContainer,
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
                        ) {
                            HomeBottomBar(
                                currentScreen = currentScreen,
                                onScreenChange = updateScreen,
                                bottomBarColor = bottomBarColor,
                                selectedTabs = selectedTabs,
                            )
                        }
                    }
                }
            }
        }
    }
}