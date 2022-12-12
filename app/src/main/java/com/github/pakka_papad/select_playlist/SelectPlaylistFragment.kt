package com.github.pakka_papad.select_playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
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
import com.github.pakka_papad.ui.theme.ZenTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectPlaylistFragment: Fragment() {

    private lateinit var navController: NavController

    private val viewModel by activityViewModels<SharedViewModel>()

    private val args: SelectPlaylistFragmentArgs by navArgs()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        if (args.songLocation.isEmpty()){
            navController.popBackStack()
        }
        return ComposeView(requireContext()).apply {
            setContent {
                val themePreference by viewModel.theme.collectAsState()
                ZenTheme(
                    themePreference = themePreference
                ) {
                    val playlists = viewModel.playlistsWithSongCount.value
                    val selectList = viewModel.selectList
                    LaunchedEffect(key1 = Unit){
                        viewModel.updateSelectListSize(playlists.size)
                    }
                    Scaffold(
                        topBar = {
                            SelectPlaylistTopBar(
                                onCancelClicked = {
                                    viewModel.resetSelectList()
                                    navController.popBackStack()
                                },
                                onConfirmClicked = {
                                    viewModel.addSongToPlaylists(args.songLocation)
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