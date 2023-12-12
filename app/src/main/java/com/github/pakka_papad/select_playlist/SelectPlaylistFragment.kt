package com.github.pakka_papad.select_playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.pakka_papad.components.BlockingProgressIndicator
import com.github.pakka_papad.components.CancelConfirmTopBar
import com.github.pakka_papad.components.Snackbar
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.ui.theme.ZenTheme
import com.github.pakka_papad.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SelectPlaylistFragment: Fragment() {

    private lateinit var navController: NavController

    private val viewModel: SelectPlaylistViewModel by viewModels()

    private val args: SelectPlaylistFragmentArgs by navArgs()

    @Inject
    lateinit var preferenceProvider: ZenPreferenceProvider

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

                    val snackbarHostState = remember { SnackbarHostState() }

                    val insertState by viewModel.insertState.collectAsStateWithLifecycle()

                    LaunchedEffect(key1 = insertState){
                        if (insertState is Resource.Idle || insertState is Resource.Loading) return@LaunchedEffect
                        if (insertState is Resource.Success){
                            insertState.data?.let {
                                snackbarHostState.showSnackbar(message = it)
                            }
                        } else {
                            insertState.message?.let {
                                snackbarHostState.showSnackbar(message = it)
                            }
                        }
                        navController.popBackStack()
                    }

                    Scaffold(
                        topBar = {
                            CancelConfirmTopBar(
                                onCancelClicked = {
                                    if (insertState is Resource.Idle){
                                        navController.popBackStack()
                                    }
                                },
                                onConfirmClicked = {
                                    viewModel.addSongsToPlaylists(args.songLocations)
                                },
                                title = "Select Playlists"
                            )
                        },
                        content = { paddingValues ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues),
                                contentAlignment = Alignment.Center
                            ){
                                if (selectList.size != playlists.size){
                                    CircularProgressIndicator()
                                } else {
                                    SelectPlaylistContent(
                                        playlists = playlists,
                                        selectList = selectList,
                                        onSelectChanged = viewModel::toggleSelectAtIndex
                                    )
                                    if (insertState is Resource.Loading){
                                        BlockingProgressIndicator()
                                    }
                                }
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
}