package com.github.pakka_papad.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.LayoutDirection
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.pakka_papad.SharedViewModel
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.ui.theme.ZenTheme

class CollectionFragment : Fragment() {

    private val viewModel by activityViewModels<SharedViewModel>()

    private lateinit var navController: NavController

    private val args: CollectionFragmentArgs by navArgs()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        if (args.collectionType == null) {
            navController.popBackStack()
        }
        viewModel.loadCollection(args.collectionType)
        return ComposeView(requireContext()).apply {
            setContent {
                val themePreference by viewModel.theme.collectAsState()
                ZenTheme(
                    themePreference = themePreference
                ) {
                    val collectionUi by viewModel.collectionUi.collectAsState()
                    val songsListState = rememberLazyListState()
                    val currentSong by viewModel.currentSong.collectAsState()
                    Scaffold(
                        topBar = {
                            CollectionTopBar(
                                topBarTitle = collectionUi?.topBarTitle ?: "",
                                topBarBackgroundImageUri = collectionUi?.topBarBackgroundImageUri ?: "",
                                onBackArrowPressed = navController::popBackStack,
                                themePreference = themePreference,
                                actions = listOf(
                                    CollectionActions.AddToQueue {
                                        collectionUi?.songs?.let { viewModel.addToQueue(it) }
                                    },
                                )
                            )
                        },
                        content = { paddingValues ->
                            val insetsPadding =
                                WindowInsets.systemBars.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
                                    .asPaddingValues()
                            Box(
                                modifier = Modifier
                                    .padding(
                                        top = paddingValues.calculateTopPadding(),
                                        start = insetsPadding.calculateStartPadding(LayoutDirection.Ltr),
                                        end = insetsPadding.calculateEndPadding(LayoutDirection.Ltr),
                                    )
                                    .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (collectionUi == null) {
                                    CircularProgressIndicator()
                                } else if (collectionUi!!.error != null) {
                                    FullScreenSadMessage(
                                        paddingValues = paddingValues,
                                        message = collectionUi!!.error
                                    )
                                } else {
                                    CollectionContent(
                                        paddingValues = PaddingValues(bottom = insetsPadding.calculateBottomPadding()),
                                        songs = collectionUi?.songs ?: emptyList(),
                                        songsListState = songsListState,
                                        onSongClicked = { index ->
                                            viewModel.setQueue(collectionUi?.songs, index)
                                        },
                                        onSongFavouriteClicked = viewModel::changeFavouriteValue,
                                        currentSong = currentSong,
                                        onAddToQueueClicked = viewModel::addToQueue,
                                        onPlayAllClicked = {
                                            viewModel.setQueue(collectionUi?.songs, 0)
                                        },
                                        onShuffleClicked = {
                                            viewModel.shufflePlay(collectionUi?.songs)
                                        },
                                        onAddToPlaylistsClicked = {
                                            navController.navigate(
                                                CollectionFragmentDirections.actionCollectionFragmentToSelectPlaylistFragment(it.location)
                                            )
                                        },
                                    )
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}