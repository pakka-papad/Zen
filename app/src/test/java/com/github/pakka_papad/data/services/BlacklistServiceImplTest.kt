package com.github.pakka_papad.data.services

import com.github.pakka_papad.data.daos.AlbumArtistDao
import com.github.pakka_papad.data.daos.AlbumDao
import com.github.pakka_papad.data.daos.ArtistDao
import com.github.pakka_papad.data.daos.BlacklistDao
import com.github.pakka_papad.data.daos.BlacklistedFolderDao
import com.github.pakka_papad.data.daos.ComposerDao
import com.github.pakka_papad.data.daos.GenreDao
import com.github.pakka_papad.data.daos.LyricistDao
import com.github.pakka_papad.data.daos.SongDao
import com.github.pakka_papad.data.music.BlacklistedFolder
import com.github.pakka_papad.data.music.BlacklistedSong
import com.github.pakka_papad.data.music.Song
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class BlacklistServiceImplTest {

    private val blacklistDao = mockk<BlacklistDao>()
    private val blacklistedFolderDao = mockk<BlacklistedFolderDao>()
    private val songDao = mockk<SongDao>()
    private val albumDao = mockk<AlbumDao>()
    private val artistDao = mockk<ArtistDao>()
    private val albumArtistDao = mockk<AlbumArtistDao>()
    private val composerDao = mockk<ComposerDao>()
    private val lyricistDao = mockk<LyricistDao>()
    private val genreDao = mockk<GenreDao>()

    private val initialBlacklistedSongs: List<BlacklistedSong>
    private val initialBlacklistedFolders: List<BlacklistedFolder>

    private val blacklistedSongsFlow: MutableStateFlow<List<BlacklistedSong>>
    private val blacklistedFoldersFlow: MutableStateFlow<List<BlacklistedFolder>>

    init {
        initialBlacklistedSongs = buildList {
            repeat(8){
                add(
                    BlacklistedSong(
                        location = "/storage/emulated/0/folder0/song$it.mp3",
                        title = "Song$it",
                        artist = "Artist$it"
                    )
                )
            }
        }
        initialBlacklistedFolders = buildList {
            repeat(7){
                add(
                    BlacklistedFolder("/storage/emulated/0/blacklist_folder$it")
                )
            }
        }
        blacklistedSongsFlow = MutableStateFlow(initialBlacklistedSongs)
        blacklistedFoldersFlow = MutableStateFlow(initialBlacklistedFolders)
    }

    @Before
    fun setup() {
        every { blacklistDao.getBlacklistedSongsFlow() } returns blacklistedSongsFlow
        every { blacklistedFolderDao.getAllFolders() } returns blacklistedFoldersFlow
    }

    @Test
    fun `verify blacklisting song feature`() = runTest {
        // Given
        val recentBlacklistSongs = mutableListOf<BlacklistedSong>()
        val toBlacklist = buildList {
            repeat(4){
                val mockSong = mockk<Song>(relaxed = true)
                every { mockSong.location } returns "/storage/emulated/0/folder1/song$it.mp3"
                every { mockSong.title } returns "New Song$it"
                every { mockSong.artist } returns "New Artist$it"
                recentBlacklistSongs.add(
                    BlacklistedSong(mockSong.location, mockSong.title, mockSong.artist)
                )
                add(mockSong)
            }
        }

        val blacklistDaoSongSlots = mutableListOf<BlacklistedSong>()
        coEvery { blacklistDao.addSong(capture(blacklistDaoSongSlots)) } answers {
            blacklistedSongsFlow.update { oldList ->
                oldList + firstArg<BlacklistedSong>()
            }
        }
        val songDaoSongSlots = mutableListOf<Song>()
        coEvery { songDao.deleteSong(capture(songDaoSongSlots)) } returns Unit

        val service = BlacklistServiceImpl(
            blacklistDao = blacklistDao,
            blacklistedFolderDao = blacklistedFolderDao,
            songDao = songDao,
            albumDao = albumDao,
            artistDao = artistDao,
            albumArtistDao = albumArtistDao,
            composerDao = composerDao,
            lyricistDao = lyricistDao,
            genreDao = genreDao,
        )

        assertEquals(service.blacklistedSongs.first(), initialBlacklistedSongs)

        // When
        service.blacklistSongs(toBlacklist)

        // Then
        val currBlacklistedSongs = service.blacklistedSongs.first()
        assertEquals(currBlacklistedSongs.size, initialBlacklistedSongs.size + toBlacklist.size)
        initialBlacklistedSongs.forEach {
            assertContains(currBlacklistedSongs, it)
        }
        recentBlacklistSongs.forEach {
            assertContains(currBlacklistedSongs, it)
        }
        coVerify(exactly = toBlacklist.size) { blacklistDao.addSong(capture(blacklistDaoSongSlots)) }
        coVerify(exactly = toBlacklist.size) { songDao.deleteSong(capture(songDaoSongSlots)) }
        songDaoSongSlots.forEach {
            assertContains(toBlacklist, it)
        }
    }

    @Test
    fun `verify blacklisting folder feature`() = runTest {
        // Given
        val recentBlacklistFolders = mutableListOf<BlacklistedFolder>()
        val toBlacklist = buildList {
            repeat(5){
                add("/storage/emulated/0/folder2/blacklist_folder$it")
                recentBlacklistFolders.add(BlacklistedFolder("/storage/emulated/0/folder2/blacklist_folder$it"))
            }
        }

        val blacklistFolderDaoSlots = mutableListOf<BlacklistedFolder>()
        coEvery { blacklistedFolderDao.insertFolder(capture(blacklistFolderDaoSlots)) } answers {
            blacklistedFoldersFlow.update { oldList ->
                oldList + firstArg<BlacklistedFolder>()
            }
        }
        println(blacklistFolderDaoSlots)
        val songDaoSlots = mutableListOf<String>()
        coEvery { songDao.deleteSongsWithPathPrefix(capture(songDaoSlots)) } returns Unit

        coEvery { albumDao.cleanAlbumTable() } returns Unit
        coEvery { artistDao.cleanArtistTable() } returns Unit
        coEvery { albumArtistDao.cleanAlbumArtistTable() } returns Unit
        coEvery { composerDao.cleanComposerTable() } returns Unit
        coEvery { lyricistDao.cleanLyricistTable() } returns Unit
        coEvery { genreDao.cleanGenreTable() } returns Unit


        val service = BlacklistServiceImpl(
            blacklistDao = blacklistDao,
            blacklistedFolderDao = blacklistedFolderDao,
            songDao = songDao,
            albumDao = albumDao,
            artistDao = artistDao,
            albumArtistDao = albumArtistDao,
            composerDao = composerDao,
            lyricistDao = lyricistDao,
            genreDao = genreDao,
        )

        assertEquals(service.blacklistedFolders.first(), initialBlacklistedFolders)

        // When
        service.blacklistFolders(toBlacklist)

        // Then
        val currBlacklistedFolders = service.blacklistedFolders.first()
        assertEquals(currBlacklistedFolders.size, initialBlacklistedFolders.size + toBlacklist.size, currBlacklistedFolders.toString())
        initialBlacklistedFolders.forEach {
            assertContains(currBlacklistedFolders, it)
        }
        recentBlacklistFolders.forEach {
            assertContains(currBlacklistedFolders, it)
        }
        coVerify(exactly = toBlacklist.size) {
            blacklistedFolderDao.insertFolder(capture(blacklistFolderDaoSlots))
        }
        coVerify(exactly = toBlacklist.size) {
            songDao.deleteSongsWithPathPrefix(capture(songDaoSlots))
        }
        songDaoSlots.forEach {
            assertContains(toBlacklist, it)
        }
        coVerify(atLeast = 1) {
            albumDao.cleanAlbumTable()
            artistDao.cleanArtistTable()
            albumArtistDao.cleanAlbumArtistTable()
            lyricistDao.cleanLyricistTable()
            composerDao.cleanComposerTable()
            genreDao.cleanGenreTable()
        }
    }

    @Test
    fun `verify whitelisting song feature`() = runTest {
        // Given
        val toWhitelist = initialBlacklistedSongs.take(3)

        val blacklistDaoSlots = mutableListOf<BlacklistedSong>()
        coEvery { blacklistDao.deleteBlacklistedSong(capture(blacklistDaoSlots)) } answers {
            blacklistedSongsFlow.update { oldList ->
                val arg = firstArg<BlacklistedSong>()
                oldList.filter { it != arg }
            }
        }

        val service = BlacklistServiceImpl(
            blacklistDao = blacklistDao,
            blacklistedFolderDao = blacklistedFolderDao,
            songDao = songDao,
            albumDao = albumDao,
            artistDao = artistDao,
            albumArtistDao = albumArtistDao,
            composerDao = composerDao,
            lyricistDao = lyricistDao,
            genreDao = genreDao,
        )

        assertEquals(service.blacklistedSongs.first(), initialBlacklistedSongs)

        // When
        service.whitelistSongs(toWhitelist)

        // Then
        val currBlacklistedSongs = service.blacklistedSongs.first()
        coVerify(exactly = toWhitelist.size) { blacklistDao.deleteBlacklistedSong(capture(blacklistDaoSlots)) }
        toWhitelist.forEach { w ->
            assertEquals(null, currBlacklistedSongs.find { it == w })
        }
    }

    @Test
    fun `verify whitelisting folder feature`() = runTest {
        // Given
        val toWhitelist = initialBlacklistedFolders.take(2)

        val slots = mutableListOf<BlacklistedFolder>()
        coEvery { blacklistedFolderDao.deleteFolder(capture(slots)) } answers {
            blacklistedFoldersFlow.update { oldList ->
                val arg = firstArg<BlacklistedFolder>()
                oldList.filter { it != arg }
            }
        }

        val service = BlacklistServiceImpl(
            blacklistDao = blacklistDao,
            blacklistedFolderDao = blacklistedFolderDao,
            songDao = songDao,
            albumDao = albumDao,
            artistDao = artistDao,
            albumArtistDao = albumArtistDao,
            composerDao = composerDao,
            lyricistDao = lyricistDao,
            genreDao = genreDao,
        )

        assertEquals(service.blacklistedFolders.first(), initialBlacklistedFolders)

        // When
        service.whitelistFolders(toWhitelist)

        // Then
        val currBlacklistedFolders = service.blacklistedFolders.first()
        coVerify(exactly = toWhitelist.size) { blacklistedFolderDao.deleteFolder(capture(slots)) }
        toWhitelist.forEach { w ->
            assertEquals(null, currBlacklistedFolders.find { it == w })
        }
    }
}