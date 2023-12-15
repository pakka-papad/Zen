package com.github.pakka_papad.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.github.pakka_papad.R
import com.github.pakka_papad.components.SortOptions
import com.github.pakka_papad.data.ZenPreferenceProvider
import com.github.pakka_papad.data.music.*
import com.github.pakka_papad.data.services.BlacklistService
import com.github.pakka_papad.data.services.PlayerService
import com.github.pakka_papad.data.services.PlaylistService
import com.github.pakka_papad.data.services.QueueService
import com.github.pakka_papad.data.services.SongService
import com.github.pakka_papad.storage_explorer.*
import com.github.pakka_papad.storage_explorer.Directory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val context: Application,
    private val exoPlayer: ExoPlayer,
    private val songExtractor: SongExtractor,
    private val prefs: ZenPreferenceProvider,
    private val playlistService: PlaylistService,
    private val blacklistService: BlacklistService,
    private val songService: SongService,
    private val queueService: QueueService,
    private val playerService: PlayerService,
) : ViewModel() {

    val songs = songService.songs
        .combine(prefs.songSortOrder){ songs, sortOrder ->
            when(sortOrder){
                SortOptions.TitleASC.ordinal -> songs.sortedBy { it.title }
                SortOptions.TitleDSC.ordinal -> songs.sortedByDescending { it.title }
                SortOptions.AlbumASC.ordinal -> songs.sortedBy { it.album }
                SortOptions.AlbumDSC.ordinal -> songs.sortedByDescending { it.album }
                SortOptions.ArtistASC.ordinal -> songs.sortedBy { it.artist }
                SortOptions.ArtistDSC.ordinal -> songs.sortedByDescending { it.artist }
                SortOptions.YearASC.ordinal -> songs.sortedBy { it.year }
                SortOptions.YearDSC.ordinal -> songs.sortedByDescending { it.year }
                SortOptions.DurationASC.ordinal -> songs.sortedBy { it.durationMillis }
                SortOptions.DurationDSC.ordinal -> songs.sortedByDescending { it.durationMillis }
                else -> songs
            }
        }.catch { exception ->
            Timber.e(exception)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val albums = songService.albums
        .combine(prefs.albumSortOrder){ albums, sortOrder ->
            when(sortOrder){
                SortOptions.TitleASC.ordinal -> albums.sortedBy { it.name }
                SortOptions.TitleDSC.ordinal -> albums.sortedByDescending { it.name }
                else -> albums
            }
        }.catch { exception ->
            Timber.e(exception)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val _selectedPerson = MutableStateFlow(Person.Artist)
    val selectedPerson = _selectedPerson.asStateFlow()

    fun onPersonSelect(person: Person) {
        _selectedPerson.update { person }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val personsWithSongCount = _selectedPerson
        .flatMapLatest {
            when (it) {
                Person.Artist -> songService.artists
                Person.AlbumArtist -> songService.albumArtists
                Person.Composer -> songService.composers
                Person.Lyricist -> songService.lyricists
            }
        }.combine(prefs.artistSortOrder){ artists, sortOrder ->
            when(sortOrder){
                SortOptions.NameASC.ordinal -> artists.sortedBy { it.name }
                SortOptions.NameDSC.ordinal -> artists.sortedByDescending { it.name }
                SortOptions.SongsCountASC.ordinal -> artists.sortedBy { it.count }
                SortOptions.SongsCountDSC.ordinal -> artists.sortedByDescending { it.count }
                else -> artists
            }
        }.catch { exception ->
            Timber.e(exception)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    val playlistsWithSongCount = playlistService.playlists
        .combine(prefs.playlistSortOrder){ playlists, sortOrder ->
            when(sortOrder){
                SortOptions.NameASC.ordinal -> playlists.sortedBy { it.playlistName }
                SortOptions.NameDSC.ordinal -> playlists.sortedByDescending { it.playlistName }
                SortOptions.SongsCountASC.ordinal -> playlists.sortedBy { it.count }
                SortOptions.SongsCountDSC.ordinal -> playlists.sortedByDescending { it.count }
                else -> playlists
            }
        }.catch { exception ->
            Timber.e(exception)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val genresWithSongCount = songService.genres
        .combine(prefs.genreSortOrder){ genres, sortOrder ->
            when(sortOrder){
                SortOptions.NameASC.ordinal -> genres.sortedBy { it.genreName }
                SortOptions.NameDSC.ordinal -> genres.sortedByDescending { it.genreName }
                SortOptions.SongsCountASC.ordinal -> genres.sortedBy { it.count }
                SortOptions.SongsCountDSC.ordinal -> genres.sortedByDescending { it.count }
                else -> genres
            }
        }.catch { exception ->
            Timber.e(exception)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun saveSortOption(screen: Int, option: Int){
        prefs.updateSortOrder(screen, option)
    }

    val currentSong = queueService.currentSong as StateFlow

    val queue = queueService.queue as StateFlow

    val repeatMode = queueService.repeatMode as StateFlow

    fun toggleRepeatMode(){
        queueService.updateRepeatMode(repeatMode.value.next())
    }

    private val _currentSongPlaying = MutableStateFlow<Boolean?>(null)
    val currentSongPlaying = _currentSongPlaying.asStateFlow()

    private val exoPlayerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _currentSongPlaying.update { isPlaying }
        }
    }

    init {
        _currentSongPlaying.update { exoPlayer.isPlaying }
        exoPlayer.addListener(exoPlayerListener)
    }

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    private fun showMessage(message: String){
        viewModelScope.launch {
            _message.update { message }
            delay(3500)
            _message.update { "" }
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.removeListener(exoPlayerListener)
        explorer.removeListener(directoryChangeListener)
    }

    /**
     * Shuffle the queue and start playing from first song
     */
    fun shufflePlay(songs: List<Song>?) = setQueue(songs?.shuffled(), 0)

    fun onSongBlacklist(song: Song) {
        viewModelScope.launch {
            try {
                blacklistService.blacklistSongs(listOf(song))
                showMessage(context.getString(R.string.done))
            } catch (e: Exception) {
                Timber.e(e)
                showMessage(context.getString(R.string.some_error_occurred))
            }
        }
    }

    fun onFolderBlacklist(folder: Directory){
        viewModelScope.launch {
            try {
                blacklistService.blacklistFolders(listOf(folder.absolutePath))
                showMessage(context.getString(R.string.done))
            } catch (_: Exception){
                showMessage(context.getString(R.string.some_error_occurred))
            }
        }
    }

    fun onPlaylistCreate(playlistName: String) {
        viewModelScope.launch {
            playlistService.createPlaylist(playlistName)
        }
    }

    fun deletePlaylist(playlistWithSongCount: PlaylistWithSongCount) {
        viewModelScope.launch {
            try {
                playlistService.deletePlaylist(playlistWithSongCount.playlistId)
                showMessage(context.getString(R.string.done))
            } catch (e: Exception) {
                Timber.e(e)
                showMessage(context.getString(R.string.some_error_occurred))
            }
        }
    }

    /**
     * Adds a song to the end of queue
     */
    fun addToQueue(song: Song) {
        if (queue.value.isEmpty()) {
//            manager.setQueue(listOf(song), 0)
            queueService.setQueue(listOf(song),0)
            playerService.startServiceIfNotRunning(listOf(song), 0)
        } else {
            val result = queueService.append(song)
            if (result) {
                showMessage(context.getString(R.string.added_to_queue, song.title))
            } else {
                showMessage(context.getString(R.string.song_already_in_queue))
            }
        }
    }

    fun addToQueue(song: MiniSong) {
        val resolvedSong = songExtractor.resolveSong(song.location) ?: return
        addToQueue(resolvedSong)
    }

    /**
     * Create and set a new queue in exoplayer.
     * Old queue is discarded.
     * Playing starts immediately
     * @param songs queue items
     * @param startPlayingFromIndex index of song from which playing should start
     */
    fun setQueue(songs: List<Song>?, startPlayingFromIndex: Int = 0) {
        if (songs == null) return
//        manager.setQueue(songs, startPlayingFromIndex)
        queueService.setQueue(songs, startPlayingFromIndex)
        playerService.startServiceIfNotRunning(songs, startPlayingFromIndex)
        showMessage(context.getString(R.string.playing))
    }

    /**
     * Toggle the favourite value of a song
     */
    fun changeFavouriteValue(song: Song? = currentSong.value) {
        if (song == null) return
        val updatedSong = song.copy(favourite = !song.favourite)
        viewModelScope.launch(Dispatchers.IO) {
//            manager.updateSong(updatedSong)
            queueService.update(updatedSong)
            songService.updateSong(updatedSong)
        }
    }

    fun onSongDrag(fromIndex: Int, toIndex: Int) = queueService.moveSong(fromIndex, toIndex)


    private val _filesInCurrentDestination = MutableStateFlow(DirectoryContents())
    val filesInCurrentDestination = _filesInCurrentDestination
        .combine(prefs.folderSortOrder){ files, sortOrder ->
            when(sortOrder){
                SortOptions.NameASC.ordinal -> {
                    DirectoryContents(
                        directories = files.directories.sortedBy { it.name },
                        songs = files.songs.sortedBy { it.title }
                    )
                }
                SortOptions.NameDSC.ordinal -> {
                    DirectoryContents(
                        directories = files.directories.sortedByDescending { it.name },
                        songs = files.songs.sortedByDescending { it.title }
                    )
                }
                else -> files
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DirectoryContents()
        )

    private val explorer = MusicFileExplorer(songExtractor)

    private val _isExplorerAtRoot = MutableStateFlow(true)
    val isExplorerAtRoot = _isExplorerAtRoot.asStateFlow()

    private val directoryChangeListener = object : MusicFileExplorer.DirectoryChangeListener {
        override fun onDirectoryChanged(path: String, files: DirectoryContents) {
            _filesInCurrentDestination.update { files }
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            explorer.addListener(directoryChangeListener)
        }
    }

    fun onFileClicked(songIndex: Int){
        viewModelScope.launch(Dispatchers.IO) {
            if(songIndex < 0 || songIndex >= filesInCurrentDestination.value.songs.size) return@launch
            val song = songExtractor.resolveSong(filesInCurrentDestination.value.songs[songIndex].location)
            song?.let {
                setQueue(listOf(song))
            }
        }
    }

    fun onFileClicked(file: Directory){
        viewModelScope.launch(Dispatchers.IO) {
            explorer.moveInsideDirectory(file.absolutePath)
            _isExplorerAtRoot.update { explorer.isRoot }
        }
    }

    fun moveToParent() {
        viewModelScope.launch(Dispatchers.IO) {
            explorer.moveToParent()
            _isExplorerAtRoot.update { explorer.isRoot }
        }
    }
}