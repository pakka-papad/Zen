package com.github.pakka_papad.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.SharedViewModel
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.ui.theme.ZenTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var navController: NavController

    private val viewModel by activityViewModels<SharedViewModel>()

    @Inject
    lateinit var exoPlayer: ExoPlayer

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
                    BackHandler {
                        viewModel.search("")
                        navController.popBackStack()
                    }
                    Scaffold(
                        topBar = {
                            SearchBar(
                                query = query,
                                onQueryChange = viewModel::search,
                                onBackArrowPressed = {
                                    viewModel.search("")
                                    navController.popBackStack()
                                }
                            )
                        },
                        content = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(it)
                                    .windowInsetsPadding(
                                        WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
                                    )
                            ) {
                                if (searchResult.errorMsg != null) {
                                    FullScreenSadMessage(
                                        paddingValues = it,
                                        message = searchResult.errorMsg
                                    )
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentPadding = WindowInsets.systemBars.only(
                                            WindowInsetsSides.Bottom).asPaddingValues()
                                    ) {
                                        item {
                                            TextResult(
                                                texts = searchResult.songs.take(5).map { it2 -> it2.title },
                                                title = "Songs"
                                            )
                                        }
                                        item {
                                            TextResult(
                                                texts = searchResult.albums.take(5).map { it2 -> it2.name },
                                                title = "Albums"
                                            )
                                        }
                                        item {
                                            TextResult(
                                                texts = searchResult.artists.take(5).map { it2 -> it2.name },
                                                title = "Artists"
                                            )
                                        }
                                        item {
                                            TextResult(
                                                texts = searchResult.albumArtists.take(5).map { it2 -> it2.name },
                                                title = "Album artists"
                                            )
                                        }
                                        item {
                                            TextResult(
                                                texts = searchResult.composers.take(5).map { it2 -> it2.name },
                                                title = "Composers"
                                            )
                                        }
                                        item {
                                            TextResult(
                                                texts = searchResult.lyricists.take(5).map { it2 -> it2.name },
                                                title = "Lyricists"
                                            )
                                        }
                                        item {
                                            TextResult(
                                                texts = searchResult.playlists.take(5).map { it2 -> it2.playlistName },
                                                title = "Playlists"
                                            )
                                        }
                                        item {
                                            TextResult(
                                                texts = searchResult.genres.take(5).map { it2 -> it2.genre },
                                                title = "Genres"
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}