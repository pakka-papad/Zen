package com.github.pakka_papad.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.github.pakka_papad.Constants
import com.github.pakka_papad.data.*
import com.github.pakka_papad.data.components.DaoCollection
import com.github.pakka_papad.data.notification.ZenNotificationManager
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
            )
        )
    }

    @Singleton
    @Provides
    fun providesNotificationManager(@ApplicationContext context: Context): ZenNotificationManager {
        return ZenNotificationManager(context)
    }

    @Singleton
    @Provides
    fun providesExoPlayer(
        @ApplicationContext context: Context
    ): ExoPlayer {
        return ExoPlayer.Builder(context).build()
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
    ): ZenPreferenceProvider {
        return ZenPreferenceProvider(
            userPreferences = userPreferences,
            coroutineScope = coroutineScope,
        )
    }
}