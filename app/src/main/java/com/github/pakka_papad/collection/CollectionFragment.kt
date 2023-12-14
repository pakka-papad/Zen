package com.github.pakka_papad.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.LayoutDirection
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.pakka_papad.R
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.components.Snackbar
import com.github.pakka_papad.components.SortOptionChooser
import com.github.pakka_papad.components.SortOptions
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.data.music.Song
import com.github.pakka_papad.ui.theme.ZenTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CollectionFragment : Fragment() {

    private val viewModel: CollectionViewModel by viewModels()

    private lateinit var navController: NavController

    @Inject
    lateinit var preferenceProvider: ZenPreferenceProvider

    private val args: CollectionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        if (args.collectionType == null) {
            navController.popBackStack()
        }
        viewModel.loadCollection(args.collectionType)
        val sortOptions = listOf(
            SortOptions.Default,
            SortOptions.TitleASC,
            SortOptions.TitleDSC,
            SortOptions.YearASC,
            SortOptions.YearDSC,
            SortOptions.DurationASC,
            SortOptions.DurationDSC,
        )
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val themePreference by preferenceProvider.theme.collectAsStateWithLifecycle()
                ZenTheme(themePreference) {
                    val collectionUi by viewModel.collectionUi.collectAsStateWithLifecycle()
                    val songsListState = rememberLazyListState()
                    val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
                    val topBarContainerAlpha by remember {
                        derivedStateOf {
                            if (songsListState.firstVisibleItemIndex == 0
                                && songsListState.firstVisibleItemScrollOffset <= 10) 0f else 1f
                        }
                    }
                    var showSortOptions by remember { mutableStateOf(false) }
                    val chosenSortOrder by viewModel.chosenSortOrder.collectAsStateWithLifecycle()
                    val snackbarHostState = remember { SnackbarHostState() }

                    val message by viewModel.message.collectAsStateWithLifecycle()
                    LaunchedEffect(key1 = message){
                        if (message.isEmpty()) return@LaunchedEffect
                        snackbarHostState.showSnackbar(message)
                    }

                    Scaffold(
                        topBar = {
                            CollectionTopBar(
                                topBarTitle = collectionUi?.topBarTitle ?: "",
                                alpha = topBarContainerAlpha,
                                onBackArrowPressed = navController::popBackStack,
                                actions = listOf(
                                    CollectionActions.AddToQueue {
                                        collectionUi?.songs?.let { viewModel.addToQueue(it) }
                                    },
                                    CollectionActions.AddToPlaylist {
                                        addAllSongsToPlaylistClicked(collectionUi?.songs)
                                    },
                                    CollectionActions.Sort {
                                        showSortOptions = true
                                    }
                                )
                            )
                        },
                        content = { paddingValues ->
                            val padding by remember {
                                derivedStateOf {
                                    PaddingValues(
                                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                                        bottom = paddingValues.calculateBottomPadding(),
                                    )
                                }
                            }
                            LazyColumn(
                                contentPadding = padding,
                                state = songsListState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surface),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                item {
                                    CollectionImage(
                                        imageUri = collectionUi?.topBarBackgroundImageUri,
                                        title = collectionUi?.topBarTitle,
                                    )
                                }
                                if (collectionUi == null) {
                                    item {
                                        CircularProgressIndicator()
                                    }
                                } else if (collectionUi?.error != null) {
                                    item {
                                        FullScreenSadMessage(
                                            message = collectionUi?.error,
                                        )
                                    }
                                } else if (collectionUi?.songs?.isEmpty() == true) {
                                    item {
                                        FullScreenSadMessage(
                                            message = "No songs found",
                                        )
                                    }
                                } else {
                                    collectionContent(
                                        songs = collectionUi?.songs ?: emptyList(),
                                        onSongClicked = {
                                            viewModel.setQueue(collectionUi?.songs,it)
                                        },
                                        onSongFavouriteClicked = viewModel::changeFavouriteValue,
                                        currentSong = currentSong,
                                        onAddToQueueClicked = viewModel::addToQueue,
                                        onPlayAllClicked = {
                                            viewModel.setQueue(collectionUi?.songs,0)
                                        },
                                        onShuffleClicked = {
                                            viewModel.shufflePlay(collectionUi?.songs)
                                        },
                                        onAddToPlaylistsClicked = this@CollectionFragment::addToPlaylistClicked,
                                        isPlaylistCollection = args.collectionType?.type == CollectionType.PlaylistType,
                                        onRemoveFromPlaylistClicked = viewModel::removeFromPlaylist
                                    )
                                }
                            }
                            if (showSortOptions){
                                SortOptionChooser(
                                    options = sortOptions,
                                    selectedOption = chosenSortOrder,
                                    onOptionSelect = { option ->
                                        viewModel.updateSortOrder(option)
                                        showSortOptions = false
                                    },
                                    onChooserDismiss = {
                                        showSortOptions = false
                                    }
                                )
                            }
                        },
                        snackbarHost = {
                            SnackbarHost(
                                hostState = snackbarHostState,
                                snackbar = {
                                    Snackbar(it)
                                }
                            )
                        }
                    )
                }
            }
        }
    }

    private fun addAllSongsToPlaylistClicked(songs: List<Song>?){
        lifecycleScope.launch {
            if (songs == null) return@launch
            if (navController.currentDestination?.id != R.id.collectionFragment) return@launch
            val songLocations = songs.map { it.location }
            navController.navigate(
                CollectionFragmentDirections
                    .actionCollectionFragmentToSelectPlaylistFragment(songLocations.toTypedArray())
            )
        }
    }

    private fun addToPlaylistClicked(song: Song){
        addAllSongsToPlaylistClicked(listOf(song))
    }
}