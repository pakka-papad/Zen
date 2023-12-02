package com.github.pakka_papad.restore_folder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
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
import com.github.pakka_papad.components.CancelConfirmTopBar
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.ui.theme.ZenTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RestoreFolderFragment: Fragment() {

    private val viewModel: RestoreFolderViewModel by viewModels()

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
                val folders by viewModel.folders.collectAsStateWithLifecycle()
                val selectList = viewModel.restoreFolderList

                val restored by viewModel.restored.collectAsStateWithLifecycle()
                LaunchedEffect(key1 = restored){
                    if (restored){
                        navController.popBackStack()
                    }
                }

                ZenTheme(themePreference) {

                    Scaffold(
                        topBar = {
                            CancelConfirmTopBar(
                                onCancelClicked = navController::popBackStack,
                                onConfirmClicked = viewModel::restoreFolders,
                                title = "Restore folders"
                            )
                        },
                        content = { paddingValues ->
                            val insetsPadding =
                                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal).asPaddingValues()
                            if (selectList.size != folders.size) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingValues),
                                    contentAlignment = Alignment.Center
                                ){
                                    CircularProgressIndicator()
                                }
                            } else {
                                RestoreFoldersContent(
                                    folders = folders,
                                    paddingValues = PaddingValues(
                                        top = paddingValues.calculateTopPadding(),
                                        start = insetsPadding.calculateStartPadding(LayoutDirection.Ltr),
                                        end = insetsPadding.calculateEndPadding(LayoutDirection.Ltr),
                                        bottom = insetsPadding.calculateBottomPadding()
                                    ),
                                    selectList = selectList,
                                    onSelectChanged = viewModel::updateRestoreList
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}