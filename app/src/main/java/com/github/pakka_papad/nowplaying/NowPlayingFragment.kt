package com.github.pakka_papad.nowplaying

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.Constants
import com.github.pakka_papad.SharedViewModel
import com.github.pakka_papad.player.ZenBroadcastReceiver
import com.github.pakka_papad.ui.theme.ZenTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NowPlayingFragment : Fragment() {

    private val viewModel by activityViewModels<SharedViewModel>()

    private lateinit var navController: NavController

    @Inject
    lateinit var exoPlayer: ExoPlayer

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            setContent {
                val themePreference by viewModel.theme.collectAsState()
                ZenTheme(
                    themePreference = themePreference
                ) {
                    val song by viewModel.currentSong.collectAsState()
                    val songPlaying by viewModel.currentSongPlaying.collectAsState()
                    val queue by viewModel.queue.collectAsState()
                    val scope = rememberCoroutineScope()
                    val scaffoldState = rememberBottomSheetScaffoldState()
                    BackHandler(
                        enabled = scaffoldState.bottomSheetState.isExpanded,
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
                                title = song!!.title
                            )
                        },
                        content = { paddingValues ->
                            NowPlayingScreen(
                                paddingValues = paddingValues,
                                song = song!!,
                                onPausePlayPressed = pendingPausePlayIntent::send,
                                onPreviousPressed = pendingPreviousIntent::send,
                                onNextPressed = pendingNextIntent::send,
                                showPlayButton = !songPlaying!!,
                                exoPlayer = exoPlayer,
                                onFavouriteClicked = viewModel::changeFavouriteValue,
                                onQueueClicked = {
                                    scope.launch {
                                        scaffoldState.bottomSheetState.expand()
                                    }
                                }
                            )
                        },
                        sheetContent = {
                            Queue(
                                queue = queue,
                                onSongClicked = { index ->

                                },
                                onFavouriteClicked = viewModel::changeFavouriteValue,
                                currentSong = song,
                                onDownArrowClicked = {
                                    scope.launch {
                                        scaffoldState.bottomSheetState.collapse()
                                    }
                                },
                                expanded = scaffoldState.bottomSheetState.isExpanded,
                                exoPlayer = exoPlayer
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
}