package com.github.pakka_papad.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.SharedViewModel
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.home.AlbumCard
import com.github.pakka_papad.ui.theme.ZenTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var navController: NavController

    private val viewModel by activityViewModels<SharedViewModel>()

    @OptIn(ExperimentalMaterial3Api::class,)
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
                                    LazyVerticalGrid(
                                        columns = if (showGrid) GridCells.Adaptive(150.dp) else GridCells.Fixed(1),
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentPadding = WindowInsets.systemBars.only(
                                            WindowInsetsSides.Bottom).asPaddingValues(),
                                    ) {
                                        when(searchType){
                                            SearchType.Songs -> {
                                                items(
                                                    items = searchResult.songs,
                                                    key = { it.location }
                                                ){
                                                    SongCardV3(song = it) {

                                                    }
                                                }
                                            }
                                            SearchType.Albums -> {
                                                items(
                                                    items = searchResult.albums,
                                                    key = { it.name }
                                                ){
                                                    AlbumCard(album = it){

                                                    }
                                                }
                                            }
                                            SearchType.Artists -> {
                                                items(
                                                    items = searchResult.artists,
                                                    key = { it.name }
                                                ){
                                                    TextCard(text = it.name){

                                                    }
                                                }
                                            }
                                            SearchType.AlbumArtists -> {
                                                items(
                                                    items = searchResult.albumArtists,
                                                    key = { it.name }
                                                ){
                                                    TextCard(text = it.name){

                                                    }
                                                }
                                            }
                                            SearchType.Lyricists -> {
                                                items(
                                                    items = searchResult.lyricists,
                                                    key = { it.name }
                                                ){
                                                    TextCard(text = it.name){

                                                    }
                                                }
                                            }
                                            SearchType.Composers -> {
                                                items(
                                                    items = searchResult.composers,
                                                    key = { it.name }
                                                ){
                                                    TextCard(text = it.name){

                                                    }
                                                }
                                            }
                                            SearchType.Genres -> {
                                                items(
                                                    items = searchResult.genres,
                                                    key = { it.genre }
                                                ){
                                                    TextCard(text = it.genre){

                                                    }
                                                }
                                            }
                                            SearchType.Playlists -> {
                                                items(
                                                    items = searchResult.playlists,
                                                    key = { it.playlistId }
                                                ){
                                                    TextCard(text = it.playlistName){

                                                    }
                                                }
                                            }
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