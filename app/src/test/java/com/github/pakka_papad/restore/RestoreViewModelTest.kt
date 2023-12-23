package com.github.pakka_papad.restore

import com.github.pakka_papad.MainDispatcherRule
import com.github.pakka_papad.data.music.BlacklistedSong
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

class RestoreViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val messageStore = mockk<MessageStore>(relaxed = true)
    private val blacklistService = mockk<BlacklistService>()
    private lateinit var blacklistedSongs: List<BlacklistedSong>
    private lateinit var viewModel: RestoreViewModel

    @Before
    fun setup() {
        blacklistedSongs = buildList {
            repeat(5) {
                add(BlacklistedSong(
                    location = "/storage/emulated/0/song$it.mp3",
                    title = "Song $it",
                    artist = ""
                ))
            }
        }
        every { blacklistService.blacklistedSongs } returns MutableStateFlow(blacklistedSongs)
        viewModel = RestoreViewModel(
            messageStore = messageStore,
            blacklistService = blacklistService,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun TestScope.startCollection() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.blackListedSongs.collect()
        }
    }

    @Test
    fun `verify updateRestoreList when restoreState is Idle and index is valid`() = runTest {
        // Given
        startCollection()
        viewModel.restoreList.forEach { assertFalse(it) }

        // When
        viewModel.updateRestoreList(0, true)

        // Then
        assertTrue(viewModel.restoreList[0])
    }

    @Test
    fun `verify updateRestoreList when restoreState is Idle and index is invalid`() = runTest {
        // Given
        startCollection()
        viewModel.restoreList.forEach { assertFalse(it) }

        // When
        val index = viewModel.blackListedSongs.value.size
        viewModel.updateRestoreList(index, true)

        // Then
        viewModel.restoreList.forEach { assertFalse(it) }
    }

    @Test
    fun `verify updateRestoreList when restoreState is not Idle`() = runTest {
        // Given
        startCollection()
        viewModel.restoreList.forEach { assertFalse(it) }
        viewModel._restoreState.update { Resource.Loading() }

        // When
        viewModel.updateRestoreList(0, true)

        // Then
        viewModel.restoreList.forEach { assertFalse(it) }
    }

    @Test
    fun `verify restoreSongs when restoreState is Idle`() = runTest {
        // Given
        startCollection()
        viewModel.updateRestoreList(0, true)
        viewModel.updateRestoreList(1, true)
        coEvery { blacklistService.whitelistSongs(blacklistedSongs.take(2)) } returns Unit

        // When
        viewModel.restoreSongs()

        // Then
        coVerify(exactly = 1) {
            blacklistService.whitelistSongs(blacklistedSongs.take(2))
        }
    }

    @Test
    fun `verify restoreSongs when restoreState is not Idle`() = runTest {
        // Given
        startCollection()
        viewModel.updateRestoreList(0, true)
        viewModel.updateRestoreList(1, true)
        viewModel._restoreState.update { Resource.Loading() }

        // When
        viewModel.restoreSongs()

        // Then
        coVerify(exactly = 0) {
            blacklistService.whitelistSongs(any())
        }
    }
}