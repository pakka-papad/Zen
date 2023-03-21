package com.github.pakka_papad.select_playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.LayoutDirection
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.ui.theme.ZenTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectPlaylistFragment: Fragment() {

    private lateinit var navController: NavController

    private val viewModel: SelectPlaylistViewModel by viewModels()

    private val args: SelectPlaylistFragmentArgs by navArgs()

    @Inject
    lateinit var preferenceProvider: ZenPreferenceProvider

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        if (args.songLocations.isEmpty()){
            navController.popBackStack()
        }
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val theme by preferenceProvider.theme.collectAsStateWithLifecycle()
                ZenTheme(theme) {
                    val playlists by viewModel.playlistsWithSongCount.collectAsStateWithLifecycle()
                    val selectList = viewModel.selectList
                    Scaffold(
                        topBar = {
                            SelectPlaylistTopBar(
                                onCancelClicked = navController::popBackStack,
                                onConfirmClicked = {
                                    viewModel.addSongsToPlaylists(args.songLocations)
                                    navController.popBackStack()
                                }
                            )
                        },
                        content = { paddingValues ->
                            val insetsPadding =
                                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal).asPaddingValues()
                            if (selectList.size != playlists.size) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingValues),
                                    contentAlignment = Alignment.Center
                                ){
                                    CircularProgressIndicator()
                                }
                            } else {
                                SelectPlaylistContent(
                                    playlists = playlists,
                                    selectList = selectList,
                                    paddingValues = PaddingValues(
                                        top = paddingValues.calculateTopPadding(),
                                        start = insetsPadding.calculateStartPadding(LayoutDirection.Ltr),
                                        end = insetsPadding.calculateEndPadding(LayoutDirection.Ltr),
                                        bottom = insetsPadding.calculateBottomPadding()
                                    ),
                                    onSelectChanged = viewModel::toggleSelectAtIndex
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}