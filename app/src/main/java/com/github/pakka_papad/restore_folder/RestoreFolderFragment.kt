package com.github.pakka_papad.restore_folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.stringResource
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
class RestoreFolderFragment: Fragment() {

    private val viewModel: RestoreFolderViewModel by viewModels()

    private lateinit var navController: NavController

    @Inject
    lateinit var preferenceProvider: ZenPreferenceProvider

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val themePreference by preferenceProvider.theme.collectAsStateWithLifecycle()
                val folders by viewModel.folders.collectAsStateWithLifecycle()
                val selectList = viewModel.restoreFolderList

                val snackbarHostState = remember { SnackbarHostState() }

                val restoreState by viewModel.restored.collectAsStateWithLifecycle()
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

                ZenTheme(themePreference) {

                    Scaffold(
                        topBar = {
                            CancelConfirmTopBar(
                                onCancelClicked = {
                                    if (restoreState is Resource.Idle){
                                        navController.popBackStack()
                                    }
                                },
                                onConfirmClicked = viewModel::restoreFolders,
                                title = stringResource(R.string.restore_folders)
                            )
                        },
                        content = { paddingValues ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(paddingValues),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectList.size != folders.size){
                                    CircularProgressIndicator()
                                } else {
                                    RestoreFoldersContent(
                                        folders = folders,
                                        selectList = selectList,
                                        onSelectChanged = viewModel::updateRestoreList
                                    )
                                    if(restoreState is Resource.Loading){
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