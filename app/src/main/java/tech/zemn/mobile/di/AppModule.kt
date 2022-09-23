package tech.zemn.mobile.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tech.zemn.mobile.Constants
import tech.zemn.mobile.data.AppDatabase
import tech.zemn.mobile.data.DataManager
import tech.zemn.mobile.data.notification.ZemnNotificationManager
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
        notificationManager: ZemnNotificationManager,
        db: AppDatabase
    ): DataManager {
        return DataManager(
            context,
            notificationManager,
            db.songDao()
        )
    }

    @Singleton
    @Provides
    fun providesNotificationManager(@ApplicationContext context: Context): ZemnNotificationManager {
        return ZemnNotificationManager(context)
    }

    @Singleton
    @Provides
    fun providesExoPlayer(
        @ApplicationContext context: Context
    ): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }
}