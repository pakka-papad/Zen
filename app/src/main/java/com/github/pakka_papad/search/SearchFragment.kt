package com.github.pakka_papad.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.SharedViewModel
import com.github.pakka_papad.collection.CollectionType
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.data.music.*
import com.github.pakka_papad.data.music.Composer
import com.github.pakka_papad.ui.theme.ZenTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var navController: NavController

    private val viewModel by activityViewModels<SharedViewModel>()

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLifecycleComposeApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        return ComposeView(requireContext()).apply {
            setContent {
                val themePreference by viewModel.theme.collectAsStateWithLifecycle()
                ZenTheme(themePreference) {
                    val query by viewModel.query.collectAsStateWithLifecycle()
                    val searchResult by viewModel.searchResult.collectAsStateWithLifecycle()
                    val searchType by viewModel.searchType.collectAsStateWithLifecycle()

                    val showGrid by remember {
                        derivedStateOf {
                            ((searchType == SearchType.Songs) || (searchType == SearchType.Albums))
                        }
                    }
                    Scaffold(
                        topBar = {
                            SearchBar(
                                query = query,
                                onQueryChange = viewModel::updateQuery,
                                onBackArrowPressed = navController::popBackStack,
                                currentType = searchType,
                                onSearchTypeSelect = viewModel::updateType,
                                onClearRequest = viewModel::clearQueryText
                            )
                        },
                        content = { paddingValues ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues)
                                    .windowInsetsPadding(
                                        WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
                                    )
                            ) {
                                if (searchResult.errorMsg != null) {
                                    FullScreenSadMessage(searchResult.errorMsg)
                                } else {
                                    ResultContent(
                                        searchResult = searchResult,
                                        showGrid = showGrid,
                                        searchType = searchType,
                                        onSongClicked = this@SearchFragment::handleClick,
                                        onAlbumClicked = this@SearchFragment::handleClick,
                                        onArtistClicked = this@SearchFragment::handleClick,
                                        onAlbumArtistClicked = this@SearchFragment::handleClick,
                                        onComposerClicked = this@SearchFragment::handleClick,
                                        onLyricistClicked = this@SearchFragment::handleClick,
                                        onGenreClicked = this@SearchFragment::handleClick,
                                        onPlaylistClicked = this@SearchFragment::handleClick,
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun handleClick(song: Song){
        viewModel.setQueue(listOf(song))
    }

    private fun handleClick(album: Album){
        navController.navigate(
            SearchFragmentDirections
                .actionSearchFragmentToCollectionFragment(CollectionType.AlbumType(album.name))
        )
    }

    private fun handleClick(artist: Artist){
        navController.navigate(
            SearchFragmentDirections
                .actionSearchFragmentToCollectionFragment(CollectionType.ArtistType(artist.name))
        )
    }

    private fun handleClick(albumArtist: AlbumArtist){
        navController.navigate(
            SearchFragmentDirections
                .actionSearchFragmentToCollectionFragment(CollectionType.AlbumArtistType(albumArtist.name))
        )
    }

    private fun handleClick(composer: Composer){
        navController.navigate(
            SearchFragmentDirections
                .actionSearchFragmentToCollectionFragment(CollectionType.ComposerType(composer.name))
        )
    }

    private fun handleClick(lyricist: Lyricist){
        navController.navigate(
            SearchFragmentDirections.actionSearchFragmentToCollectionFragment(
                CollectionType.LyricistType(lyricist.name)
            )
        )
    }

    private fun handleClick(genre: Genre){
        navController.navigate(
            SearchFragmentDirections.actionSearchFragmentToCollectionFragment(CollectionType.GenreType(genre.genre))
        )
    }

    private fun handleClick(playlist: Playlist){
        navController.navigate(
            SearchFragmentDirections
                .actionSearchFragmentToCollectionFragment(CollectionType.PlaylistType(playlist.playlistId))
        )
    }
}