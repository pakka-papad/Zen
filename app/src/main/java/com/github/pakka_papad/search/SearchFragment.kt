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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.SharedViewModel
import com.github.pakka_papad.collection.CollectionType
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.ui.theme.ZenTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var navController: NavController

    private val viewModel by activityViewModels<SharedViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        return ComposeView(requireContext()).apply {
            setContent {
                val themePreference by viewModel.theme.collectAsState()
                ZenTheme(themePreference) {
                    val query by viewModel.query.collectAsState()
                    val searchResult by viewModel.searchResult.collectAsState()
                    val searchType by viewModel.searchType.collectAsState()

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
                                onSearchTypeSelect = viewModel::updateType
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
                                        onSongClicked = {
                                            viewModel.setQueue(listOf(it))
                                        },
                                        onAlbumClicked = {
                                            navController.navigate(
                                                SearchFragmentDirections
                                                    .actionSearchFragmentToCollectionFragment(
                                                        CollectionType.AlbumType(it.name)
                                                    )
                                            )
                                        },
                                        onArtistClicked = {
                                            navController.navigate(
                                                SearchFragmentDirections
                                                    .actionSearchFragmentToCollectionFragment(
                                                        CollectionType.ArtistType(it.name)
                                                    )
                                            )
                                        },
                                        onAlbumArtistClicked = {},
                                        onComposerClicked = {},
                                        onLyricistClicked = {},
                                        onGenreClicked = {},
                                        onPlaylistClicked = {
                                            navController.navigate(
                                                SearchFragmentDirections
                                                    .actionSearchFragmentToCollectionFragment(
                                                        CollectionType.PlaylistType(it.playlistId)
                                                    )
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}