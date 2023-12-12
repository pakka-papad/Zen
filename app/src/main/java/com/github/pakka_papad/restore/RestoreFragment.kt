package com.github.pakka_papad.restore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
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
import com.github.pakka_papad.R
import com.github.pakka_papad.components.BlockingProgressIndicator
import com.github.pakka_papad.components.CancelConfirmTopBar
import com.github.pakka_papad.components.Snackbar
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.ui.theme.ZenTheme
import com.github.pakka_papad.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RestoreFragment: Fragment() {

    private val viewModel: RestoreViewModel by viewModels()

    private lateinit var navController: NavController

    @Inject
    lateinit var preferenceProvider: ZenPreferenceProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        navController = findNavController()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val theme by preferenceProvider.theme.collectAsStateWithLifecycle()
                ZenTheme(theme) {
                    val songs by viewModel.blackListedSongs.collectAsStateWithLifecycle()
                    val selectList = viewModel.restoreList

                    val snackbarHostState = remember { SnackbarHostState() }

                    val restoreState by viewModel.restoreState.collectAsStateWithLifecycle()
                    LaunchedEffect(key1 = restoreState){
                        if (restoreState is Resource.Idle || restoreState is Resource.Loading) return@LaunchedEffect
                        if (restoreState is Resource.Success){
                            snackbarHostState.showSnackbar(
                                message = getString(R.string.done_rescan_to_see_all_the_restored_songs),
                            )
                        } else {
                            restoreState.message?.let {
                                snackbarHostState.showSnackbar(message = it)
                            }
                        }
                        navController.popBackStack()
                    }

                    BackHandler(
                        enabled = restoreState is Resource.Loading,
                        onBack = {  }
                    )

                    Scaffold(
                        topBar = {
                            CancelConfirmTopBar(
                                onCancelClicked = {
                                    if (restoreState is Resource.Idle) {
                                        navController.popBackStack()
                                    }
                                },
                                onConfirmClicked = viewModel::restoreSongs,
                                title = "Restore songs"
                            )
                        },
                        content = { paddingValues ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues),
                                contentAlignment = Alignment.Center
                            ){
                                if (selectList.size != songs.size){
                                    CircularProgressIndicator()
                                } else {
                                    RestoreContent(
                                        songs = songs,
                                        selectList = selectList,
                                        onSelectChanged = viewModel::updateRestoreList
                                    )
                                    if (restoreState is Resource.Loading){
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