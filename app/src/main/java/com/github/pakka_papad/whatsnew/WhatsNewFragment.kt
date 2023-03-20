package com.github.pakka_papad.whatsnew

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.github.pakka_papad.components.FullScreenSadMessage
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.ui.theme.ZenTheme
import com.github.pakka_papad.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WhatsNewFragment: Fragment() {

    private lateinit var navController: NavController

    @Inject lateinit var preferenceProvider: ZenPreferenceProvider

    private val viewModel: WhatsNewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        navController = findNavController()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val theme by preferenceProvider.theme.collectAsStateWithLifecycle()
                val changelogsResource by viewModel.changelogsFlow.collectAsStateWithLifecycle()
                ZenTheme(theme) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surface)
                            .windowInsetsPadding(WindowInsets.systemBars),
                        contentAlignment = Alignment.Center
                    ){
                        when(changelogsResource){
                            is Resource.Success -> {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    contentPadding = PaddingValues(20.dp),
                                    verticalArrangement = Arrangement.spacedBy(20.dp)
                                ){
                                    items(
                                        items = changelogsResource.data ?: emptyList(),
                                        key = { it.versionCode }
                                    ){changelog ->
                                        ChangelogUi(changelog)
                                    }
                                }
                            }
                            is Resource.Error -> {
                                FullScreenSadMessage(message = changelogsResource.message)
                            }
                            else -> {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}