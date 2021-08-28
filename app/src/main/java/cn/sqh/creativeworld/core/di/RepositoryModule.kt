package cn.sqh.creativeworld.core.di

import cn.sqh.creativeworld.core.database.CreativeWorldDatabase
import cn.sqh.creativeworld.network.service.TweetService
import cn.sqh.creativeworld.network.service.VideoService
import cn.sqh.creativeworld.repository.CreativeWorldFactory
import cn.sqh.creativeworld.repository.comment.ICommentRepository
import cn.sqh.creativeworld.repository.tweet.ITweetRepository
import cn.sqh.creativeworld.repository.video.IVideoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
class RepositoryModule {

    @Singleton
    @Provides
    fun provideVideosRepository(
        api: VideoService,
        db: CreativeWorldDatabase
    ): IVideoRepository {
        return CreativeWorldFactory.makeVideoFactory(api, db)
    }

    @Singleton
    @Provides
    fun provideTweetRepository(
        api: TweetService,
        db: CreativeWorldDatabase
    ): ITweetRepository {
        return CreativeWorldFactory.makeTweetFactory(api, db)
    }

    @Singleton
    @Provides
    fun provideCommentsRepository(
        tweetApi: TweetService,
        videoApi: VideoService,
        db: CreativeWorldDatabase
    ): ICommentRepository {
        return CreativeWorldFactory.makeCommentsFactory(tweetApi, videoApi, db)
    }

}
