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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import coil.compose.AsyncImage
import com.github.pakka_papad.R
import com.github.pakka_papad.components.FullScreenSadMessage
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
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val themePreference by preferenceProvider.theme.collectAsStateWithLifecycle()
                ZenTheme(
                    themePreference = themePreference
                ) {
                    val collectionUi by viewModel.collectionUi.collectAsStateWithLifecycle()
                    val songsListState = rememberLazyListState()
                    val currentSong by viewModel.currentSong.collectAsStateWithLifecycle()
                    val insetsPadding =
                        WindowInsets.systemBars.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)
                            .asPaddingValues()
                    val topBarContainerAlpha by remember {
                        derivedStateOf {
                            if (songsListState.firstVisibleItemIndex == 0
                                && songsListState.firstVisibleItemScrollOffset <= 10) 0f else 1f
                        }
                    }
                    Box {
                        LazyColumn(
                            contentPadding = insetsPadding,
                            state = songsListState,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(240.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    val surface = MaterialTheme.colorScheme.surface
                                    val onSurface = MaterialTheme.colorScheme.onSurface
                                    var textColor by remember { mutableStateOf(onSurface) }
                                    var backgroundColor by remember { mutableStateOf(surface) }
                                    AsyncImage(
                                        model = collectionUi?.topBarBackgroundImageUri,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize(),
                                        onSuccess = { result ->
                                            Palette.Builder(result.result.drawable.toBitmap()).generate { palette ->
                                                palette?.let {
                                                    it.mutedSwatch?.let { vbs ->
                                                        backgroundColor = Color(vbs.rgb)
                                                        textColor = Color(vbs.titleTextColor).copy(alpha = 1f)
                                                    }
                                                }
                                            }
                                        }
                                    )
                                    Text(
                                        text = collectionUi?.topBarTitle ?: "",
                                        style = MaterialTheme.typography.headlineMedium,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                Brush.verticalGradient(
                                                    listOf(Color.Transparent, backgroundColor)
                                                )
                                            )
                                            .padding(10.dp),
                                        maxLines = 2,
                                        color = textColor,
                                        fontWeight = FontWeight.ExtraBold,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
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
                                }
                            )
                        )
                    }
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