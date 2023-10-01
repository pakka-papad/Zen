package com.github.pakka_papad.di

import android.annotation.SuppressLint
import android.content.Context
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.*
import com.github.pakka_papad.data.components.DaoCollection
import com.github.pakka_papad.data.music.SongExtractor
import com.github.pakka_papad.data.notification.ZenNotificationManager
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    @Singleton
    @Provides
    fun providesDataManager(
        @ApplicationContext context: Context,
        notificationManager: ZenNotificationManager,
        db: AppDatabase,
        scope: CoroutineScope,
        extractor: SongExtractor,
    ): DataManager {
        return DataManager(
            context = context,
            notificationManager = notificationManager,
            daoCollection = DaoCollection(
                songDao = db.songDao(),
                albumDao = db.albumDao(),
                artistDao = db.artistDao(),
                albumArtistDao = db.albumArtistDao(),
                composerDao = db.composerDao(),
                lyricistDao = db.lyricistDao(),
                genreDao = db.genreDao(),
                playlistDao = db.playlistDao(),
                blacklistDao = db.blacklistDao(),
                blacklistedFolderDao = db.blacklistedFolderDao(),
                playHistoryDao = db.playHistoryDao()
            ),
            scope = scope,
            songExtractor = extractor
        )
    }

    @Singleton
    @Provides
    fun providesNotificationManager(@ApplicationContext context: Context): ZenNotificationManager {
        return ZenNotificationManager(context)
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
            setMediaSourceFactory(DefaultMediaSourceFactory(context,extractorsFactory))
            setAudioAttributes(audioAttributes,true)
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
    ): SongExtractor {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        return SongExtractor(
            scope = scope,
            context = context,
        )
    }
}