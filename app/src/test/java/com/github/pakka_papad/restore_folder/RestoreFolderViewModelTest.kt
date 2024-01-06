package com.github.pakka_papad.restore_folder

import com.github.pakka_papad.MainDispatcherRule
import com.github.pakka_papad.data.music.BlacklistedFolder
import com.github.pakka_papad.data.services.BlacklistService
import com.github.pakka_papad.util.MessageStore
import com.github.pakka_papad.util.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RestoreFolderViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val messageStore = mockk<MessageStore>()
    private val blacklistService = mockk<BlacklistService>()
    private lateinit var blacklistedFolders: List<BlacklistedFolder>
    private lateinit var viewModel: RestoreFolderViewModel

    @Before
    fun setup() {
        blacklistedFolders = buildList {
            repeat(5) {
                add(BlacklistedFolder("/storage/emulated/0/folder$it"))
            }
        }
        every { blacklistService.blacklistedFolders } returns MutableStateFlow(blacklistedFolders)
        viewModel = RestoreFolderViewModel(
            messageStore = messageStore,
            blacklistService = blacklistService,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.startCollection() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.folders.collect()
        }
    }

    @Test
    fun `verify updated restore list when restore is Idle`() = runTest {
        // Given
        startCollection()

        // When
        val index = 1
        viewModel.updateRestoreList(index, true)

        // Then
        assertTrue(viewModel.restoreFolderList[index])
    }

    @Test
    fun `verify updated restore list when restore is Idle and index is invalid`() = runTest {
        // Given
        startCollection()

        // When
        val index = 10
        viewModel.updateRestoreList(index, true)

        // Then
        viewModel.restoreFolderList.forEach { assertFalse(it) }
    }

    @Test
    fun `verify updated restore list when restore is not Idle`() = runTest {
        // Given
        startCollection()
        viewModel._restored.update { Resource.Loading() }

        // When
        val index = 1
        viewModel.updateRestoreList(index, true)

        // Then
        viewModel.restoreFolderList.forEach { assertFalse(it) }
    }

    @Test
    fun `verify restoreFolders when restore is Idle`() = runTest {
        // Given
        startCollection()
        viewModel.updateRestoreList(0, true)
        viewModel.updateRestoreList(1, true)
        coEvery { blacklistService.whitelistFolders(blacklistedFolders.take(2)) } returns Unit

        // When
        viewModel.restoreFolders()

        // Then
        coVerify(exactly = 1) {
            blacklistService.whitelistFolders(blacklistedFolders.take(2))
        }
    }

    @Test
    fun `verify restoreFolders when restore is not Idle`() = runTest {
        // Given
        startCollection()
        viewModel.updateRestoreList(0, true)
        viewModel.updateRestoreList(1, true)
        viewModel._restored.update { Resource.Loading() }
        coEvery { blacklistService.whitelistFolders(blacklistedFolders.take(2)) } returns Unit

        // When
        viewModel.restoreFolders()

        // Then
        coVerify(exactly = 0) {
            blacklistService.whitelistFolders(any())
        }
    }
}