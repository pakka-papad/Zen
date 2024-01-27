package com.github.pakka_papad.di

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC
import androidx.media3.common.C.USAGE_MEDIA
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.extractor.DefaultExtractorsFactory
import androidx.media3.extractor.mp3.Mp3Extractor
import androidx.room.Room
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.*
import com.github.pakka_papad.data.music.SongExtractor
import com.github.pakka_papad.data.services.*
import com.github.pakka_papad.player.ZenBroadcastReceiver
import com.github.pakka_papad.util.MessageStore
import com.github.pakka_papad.util.MessageStoreImpl
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            Constants.DATABASE_NAME
        ).build()
    }

    @SuppressLint("UnsafeOptInUsageError")
    @Singleton
    @Provides
    fun providesExoPlayer(
        @ApplicationContext context: Context
    ): ExoPlayer {
        val extractorsFactory = DefaultExtractorsFactory().apply {
            setMp3ExtractorFlags(Mp3Extractor.FLAG_DISABLE_ID3_METADATA)
        }
        val audioAttributes = AudioAttributes.Builder().apply {
            setContentType(AUDIO_CONTENT_TYPE_MUSIC)
            setUsage(USAGE_MEDIA)
        }.build()
        return ExoPlayer.Builder(context).apply {
            setMediaSourceFactory(DefaultMediaSourceFactory(context, extractorsFactory))
            setAudioAttributes(audioAttributes, true)
            setHandleAudioBecomingNoisy(true)
        }.build()
    }

    @Singleton
    @Provides
    fun providesUserPreferencesDatastore(
        @ApplicationContext context: Context,
        userPreferencesSerializer: UserPreferencesSerializer,
    ): DataStore<UserPreferences> {
        return DataStoreFactory.create(
            serializer = userPreferencesSerializer,
            produceFile = {
                context.dataStoreFile("user_preferences.pb")
            }
        )
    }

    @Singleton
    @Provides
    fun providesQueueStateDatastore(
        @ApplicationContext context: Context,
    ): DataStore<QueueState> {
        return DataStoreFactory.create(
            serializer = QueueStateSerializer,
            produceFile = {
                context.dataStoreFile(Constants.QUEUE_STATE_FILE)
            }
        )
    }

    @Singleton
    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Singleton
    @Provides
    fun providesZenPreferencesDatastore(
        userPreferences: DataStore<UserPreferences>,
        coroutineScope: CoroutineScope,
        crashReporter: ZenCrashReporter,
    ): ZenPreferenceProvider {
        return ZenPreferenceProvider(
            userPreferences = userPreferences,
            coroutineScope = coroutineScope,
            crashReporter = crashReporter,
        )
    }

    @Singleton
    @Provides
    fun providesZenCrashReporter(): ZenCrashReporter {
        return ZenCrashReporter(
            firebase = FirebaseCrashlytics.getInstance()
        )
    }

    @Singleton
    @Provides
    fun providesSongExtractor(
        @ApplicationContext context: Context,
        crashReporter: ZenCrashReporter,
        db: AppDatabase,
    ): SongExtractor {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        return SongExtractor(
            scope = scope,
            context = context,
            crashReporter = crashReporter,
            songDao = db.songDao(),
            albumDao = db.albumDao(),
            artistDao = db.artistDao(),
            albumArtistDao = db.albumArtistDao(),
            composerDao = db.composerDao(),
            lyricistDao = db.lyricistDao(),
            genreDao = db.genreDao(),
            blacklistDao = db.blacklistDao(),
            blacklistedFolderDao = db.blacklistedFolderDao(),
        )
    }

    @Singleton
    @Provides
    fun providesBlacklistService(
        db: AppDatabase
    ): BlacklistService {
        return BlacklistServiceImpl(
            blacklistDao = db.blacklistDao(),
            blacklistedFolderDao = db.blacklistedFolderDao(),
            songDao = db.songDao(),
            albumDao = db.albumDao(),
            artistDao = db.artistDao(),
            albumArtistDao = db.albumArtistDao(),
            composerDao = db.composerDao(),
            lyricistDao = db.lyricistDao(),
            genreDao = db.genreDao(),
        )
    }

    @Singleton
    @Provides
    fun providesPlaylistService(
        db: AppDatabase
    ): PlaylistService {
        return PlaylistServiceImpl(
            playlistDao = db.playlistDao(),
            thumbnailDao = db.thumbnailDao(),
        )
    }

    @Singleton
    @Provides
    fun providesSongService(
        db: AppDatabase
    ): SongService {
        return SongServiceImpl(
            songDao = db.songDao(),
            albumDao = db.albumDao(),
            artistDao = db.artistDao(),
            albumArtistDao = db.albumArtistDao(),
            composerDao = db.composerDao(),
            lyricistDao = db.lyricistDao(),
            genreDao = db.genreDao(),
        )
    }

    @Singleton
    @Provides
    fun providesQueueService(): QueueService {
        return QueueServiceImpl()
    }

    @Singleton
    @Provides
    fun providesPlayerService(
        @ApplicationContext context: Context,
        queueService: QueueService,
        preferenceProvider: ZenPreferenceProvider,
    ): PlayerService {
        return PlayerServiceImpl(
            context = context,
            queueService = queueService,
            preferenceProvider = preferenceProvider,
        )
    }

    @Singleton
    @Provides
    fun providesAnalyticsService(
        db: AppDatabase,
    ): AnalyticsService {
        return AnalyticsServiceImpl(
            playHistoryDao = db.playHistoryDao(),
            scope = CoroutineScope(Job() + Dispatchers.IO)
        )
    }

    @Singleton
    @Provides
    fun providesSearchService(
        db: AppDatabase,
    ): SearchService {
        return SearchServiceImpl(
            songDao = db.songDao(),
            albumDao = db.albumDao(),
            artistDao = db.artistDao(),
            albumArtistDao = db.albumArtistDao(),
            composerDao = db.composerDao(),
            lyricistDao = db.lyricistDao(),
            genreDao = db.genreDao(),
            playlistDao = db.playlistDao()
        )
    }

    @Singleton
    @Provides
    fun providesMessageStore(
        @ApplicationContext context: Context,
    ): MessageStore {
        return MessageStoreImpl(
            context = context,
        )
    }

    @Singleton
    @Provides
    fun providesSleepTimerService(
        @ApplicationContext context: Context,
    ): SleepTimerService {
        return SleepTimerServiceImpl(
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
            closeIntent = PendingIntent.getBroadcast(
                context, ZenBroadcastReceiver.CANCEL_ACTION_REQUEST_CODE,
                Intent(Constants.PACKAGE_NAME).putExtra(
                    ZenBroadcastReceiver.AUDIO_CONTROL,
                    ZenBroadcastReceiver.ZEN_PLAYER_CANCEL
                ),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    @Singleton
    @Provides
    fun providesThumbnailService(
        @ApplicationContext context: Context,
        db: AppDatabase
    ): ThumbnailService {
        return ThumbnailServiceImpl(
            context = context,
            thumbnailDao = db.thumbnailDao(),
        )
    }
}