package cn.sqh.creativeworld.core.di

import android.app.Application
import androidx.room.Room
import cn.sqh.creativeworld.core.database.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object RoomModule {

    private const val DATABASE_NAME = "creative_world_database"

    @Provides
    @Singleton
    fun provideAppDataBase(application: Application): CreativeWorldDatabase {
        return Room
            .databaseBuilder(application, CreativeWorldDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun providerVideoDao(appDataBase: CreativeWorldDatabase): VideoDao {
        return appDataBase.videoDao()
    }

    @Provides
    @Singleton
    fun providerTweetDao(appDataBase: CreativeWorldDatabase): TweetDao {
        return appDataBase.tweetDao()
    }

    @Provides
    @Singleton
    fun providerVideoCommentDao(appDataBase: CreativeWorldDatabase): VideoCommentDao {
        return appDataBase.videoCommentDao()
    }

    @Provides
    @Singleton
    fun providerTweetCommentDao(appDataBase: CreativeWorldDatabase): TweetCommentDao {
        return appDataBase.tweetCommentDao()
    }


}
