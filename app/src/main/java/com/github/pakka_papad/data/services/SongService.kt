package com.github.pakka_papad.data.services

import com.github.pakka_papad.data.daos.AlbumArtistDao
import com.github.pakka_papad.data.daos.AlbumDao
import com.github.pakka_papad.data.daos.ArtistDao
import com.github.pakka_papad.data.daos.ComposerDao
import com.github.pakka_papad.data.daos.GenreDao
import com.github.pakka_papad.data.daos.LyricistDao
import com.github.pakka_papad.data.daos.SongDao
import com.github.pakka_papad.data.music.Album
import com.github.pakka_papad.data.music.AlbumArtistWithSongCount
import com.github.pakka_papad.data.music.AlbumArtistWithSongs
import com.github.pakka_papad.data.music.AlbumWithSongs
import com.github.pakka_papad.data.music.ArtistWithSongCount
import com.github.pakka_papad.data.music.ArtistWithSongs
import com.github.pakka_papad.data.music.ComposerWithSongCount
import com.github.pakka_papad.data.music.ComposerWithSongs
import com.github.pakka_papad.data.music.GenreWithSongCount
import com.github.pakka_papad.data.music.GenreWithSongs
import com.github.pakka_papad.data.music.LyricistWithSongCount
import com.github.pakka_papad.data.music.LyricistWithSongs
import com.github.pakka_papad.data.music.Song
import kotlinx.coroutines.flow.Flow

interface SongService {
    val songs: Flow<List<Song>>
    val albums: Flow<List<Album>>
    val artists: Flow<List<ArtistWithSongCount>>
    val albumArtists: Flow<List<AlbumArtistWithSongCount>>
    val composers: Flow<List<ComposerWithSongCount>>
    val lyricists: Flow<List<LyricistWithSongCount>>
    val genres: Flow<List<GenreWithSongCount>>

    fun getAlbumWithSongsByName(albumName: String): Flow<AlbumWithSongs?>
    fun getArtistWithSongsByName(artistName: String): Flow<ArtistWithSongs?>
    fun getAlbumArtistWithSongsByName(albumArtistName: String): Flow<AlbumArtistWithSongs?>
    fun getComposerWithSongsByName(composerName: String): Flow<ComposerWithSongs?>
    fun getLyricistWithSongsByName(lyricistName: String): Flow<LyricistWithSongs?>
    fun getGenreWithSongsByName(genre: String): Flow<GenreWithSongs?>

    fun getFavouriteSongs(): Flow<List<Song>>

    suspend fun updateSong(song: Song)
}

class SongServiceImpl(
    private val songDao: SongDao,
    private val albumDao: AlbumDao,
    private val artistDao: ArtistDao,
    private val albumArtistDao: AlbumArtistDao,
    private val composerDao: ComposerDao,
    private val lyricistDao: LyricistDao,
    private val genreDao: GenreDao,
) :SongService {
    override val songs: Flow<List<Song>>
        = songDao.getAllSongs()

    override val albums: Flow<List<Album>>
        = albumDao.getAllAlbums()

    override val artists: Flow<List<ArtistWithSongCount>>
        = songDao.getAllArtistsWithSongCount()

    override val albumArtists: Flow<List<AlbumArtistWithSongCount>>
        = songDao.getAllAlbumArtistsWithSongCount()

    override val composers: Flow<List<ComposerWithSongCount>>
        = songDao.getAllComposersWithSongCount()

    override val lyricists: Flow<List<LyricistWithSongCount>>
        = songDao.getAllLyricistsWithSongCount()

    override val genres: Flow<List<GenreWithSongCount>>
        = songDao.getAllGenresWithSongCount()

    override fun getAlbumWithSongsByName(albumName: String): Flow<AlbumWithSongs?> {
        return albumDao.getAlbumWithSongsByName(albumName)
    }

    override fun getArtistWithSongsByName(artistName: String): Flow<ArtistWithSongs?> {
        return artistDao.getArtistWithSongsByName(artistName)
    }

    override fun getAlbumArtistWithSongsByName(albumArtistName: String): Flow<AlbumArtistWithSongs?> {
        return albumArtistDao.getAlbumArtistWithSongs(albumArtistName)
    }

    override fun getComposerWithSongsByName(composerName: String): Flow<ComposerWithSongs?> {
        return composerDao.getComposerWithSongs(composerName)
    }

    override fun getLyricistWithSongsByName(lyricistName: String): Flow<LyricistWithSongs?> {
        return lyricistDao.getLyricistWithSongs(lyricistName)
    }

    override fun getGenreWithSongsByName(genre: String): Flow<GenreWithSongs?> {
        return genreDao.getGenreWithSongs(genre)
    }

    override fun getFavouriteSongs(): Flow<List<Song>> {
        return songDao.getAllFavourites()
    }

    override suspend fun updateSong(song: Song) {
        songDao.updateSong(song)
    }
}