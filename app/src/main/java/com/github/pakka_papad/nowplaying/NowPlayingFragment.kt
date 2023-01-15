package com.github.pakka_papad.nowplaying

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.player.ZenBroadcastReceiver
import com.github.pakka_papad.ui.theme.ZenTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NowPlayingFragment : Fragment() {

    private val viewModel: NowPlayingViewModel by viewModels()

    private lateinit var navController: NavController

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @Inject
    lateinit var preferenceProvider: ZenPreferenceProvider

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        if (viewModel.currentSong.value == null) {
            navController.popBackStack()
        }
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
                val themePreference by preferenceProvider.theme.collectAsState()
                ZenTheme(
                    themePreference = themePreference
                ) {
                    val song by viewModel.currentSong.collectAsState()
                    val songPlaying by viewModel.currentSongPlaying.collectAsState()
                    val queue = viewModel.queue
                    val scope = rememberCoroutineScope()
                    val scaffoldState = rememberBottomSheetScaffoldState()
                    LaunchedEffect(key1 = song) {
                        if (song != null) return@LaunchedEffect
                        navController.popBackStack()
                    }
                    BackHandler(
                        enabled = if (song != null) scaffoldState.bottomSheetState.isExpanded else false,
                        onBack = {
                            scope.launch {
                                scaffoldState.bottomSheetState.collapse()
                            }
                        }
                    )
                    BottomSheetScaffold(
                        scaffoldState = scaffoldState,
                        topBar = {
                            NowPlayingTopBar(
                                onBackArrowPressed = navController::popBackStack,
                                title = song?.title ?: "",
                                options = listOf(
                                    NowPlayingOptions.SaveToPlaylist {
                                        saveToPlaylistClicked(queue)
                                    }
                                )
                            )
                        },
                        content = { paddingValues ->
                            Box {
                                NowPlayingScreen(
                                    paddingValues = paddingValues,
                                    song = song,
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
                                    }
                                )
                            }
                        },
                        sheetContent = {
                            Queue(
                                queue = queue,
                                onFavouriteClicked = viewModel::changeFavouriteValue,
                                currentSong = song,
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
            }
        }
    }

    private fun saveToPlaylistClicked(queue: List<Song>){
        lifecycleScope.launch {
            val songLocations = queue.map { it.location }
            navController.navigate(
                NowPlayingFragmentDirections
                    .actionNowPlayingToSelectPlaylistFragment(songLocations.toTypedArray())
            )
        }
    }
}