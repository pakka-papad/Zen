package com.github.pakka_papad.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
                    Scaffold(
                        topBar = {
                            SearchBar(
                                query = query,
                                onQueryChange = viewModel::search,
                                onBackArrowPressed = navController::popBackStack
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
                                    FullScreenSadMessage(searchResult.errorMsg)
                                } else {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentPadding = WindowInsets.systemBars.only(
                                            WindowInsetsSides.Bottom).asPaddingValues()
                                    ) {
                                        item("Songs") {
                                            SongResult(
                                                onSeeAllPressed = {  },
                                                onSongClicked = {  },
                                                songs = searchResult.songs.take(5)
                                            )
                                        }
                                        item("Albums") {
                                            AlbumResult(
                                                onSeeAllPressed = {  },
                                                onAlbumClicked = {  },
                                                albums = searchResult.albums.take(5)
                                            )
                                        }
//                                        item("Artists") {
//                                            ArtistResult(
//                                                onSeeAllPressed = {  },
//                                                onArtistClicked = {  },
//                                                artists = searchResult.artists.take(5)
//                                            )
//                                        }
//                                        item("Album Artists") {
//                                            AlbumArtistResult(
//                                                onSeeAllPressed = {  },
//                                                onAlbumArtistClicked = {  },
//                                                albumArtists = searchResult.albumArtists.take(5)
//                                            )
//                                        }
//                                        item("Composers") {
//                                            ComposerResult(
//                                                onSeeAllPressed = {  },
//                                                onComposerClicked = {  },
//                                                composers = searchResult.composers.take(5)
//                                            )
//                                        }
//                                        item("Lyricists") {
//                                            LyricistResult(
//                                                onSeeAllPressed = { },
//                                                onLyricistClicked = { },
//                                                lyricists = searchResult.lyricists.take(5)
//                                            )
//                                        }
//                                        item("Genres") {
//                                            GenreResult(
//                                                onSeeAllPressed = {  },
//                                                onGenreClicked = {  },
//                                                genres = searchResult.genres.take(5)
//                                            )
//                                        }
//                                        item("Playlists") {
//                                            PlaylistResult(
//                                                onSeeAllPressed = { },
//                                                onPlaylistClicked = { },
//                                                playlists = searchResult.playlists.take(5)
//                                            )
//                                        }
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