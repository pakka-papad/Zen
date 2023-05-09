package com.github.pakka_papad.settings.folder_scan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.components.TopBarWithBackArrow
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.ui.theme.ZenTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class FolderToScanFragment: Fragment() {

    private val viewModel: FolderScanViewModel by viewModels()

    private lateinit var navController: NavController

    @Inject
    lateinit var preferenceProvider: ZenPreferenceProvider

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val themePreference by preferenceProvider.theme.collectAsStateWithLifecycle()
                val getFolder = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult(),
                    onResult = {
                        Timber.d("result data " + it.data?.dataString)
                    }
                )
                val folders by viewModel.folders.collectAsStateWithLifecycle()

                ZenTheme(themePreference) {

                    Scaffold(
                        topBar = {
                            TopBarWithBackArrow(
                                onBackArrowPressed = navController::popBackStack,
                                title = "Folders to scan",
                                actions = {}
                            )
                        },
                        content = {
                            LazyColumn(
                                contentPadding = it,
                                content = {
                                    items(
                                        items = folders
                                    ){ folder ->
                                        Text(text = folder.path)
                                    }
                                }
                            )
                        },
                        floatingActionButton = {
                            FloatingActionButton(
                                onClick = { getFolder.launch(intent) },
                                content = {
                                    Icon(
                                        imageVector = Icons.Outlined.Add,
                                        contentDescription = "add folder button"
                                    )
                                }
                            )
                        },
                        floatingActionButtonPosition = FabPosition.Center
                    )
                }
            }
        }
    }
}