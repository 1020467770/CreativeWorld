package cn.sqh.creativeworld.core.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.sqh.creativeworld.core.data.*

@Dao
interface TweetCommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tweetCommentEntity: TweetCommentEntity): TweetCommentId

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(commentList: List<TweetCommentEntity>)

    @Query("SELECT * FROM tweet_comment_table")
    fun getAllTweetComments(): PagingSource<Int, TweetCommentEntity>

    @Query("SELECT * FROM tweet_comment_table WHERE id=:id")
    suspend fun getTweetCommentById(id: VideoCommentId): TweetCommentEntity?

    @Query("DELETE FROM tweet_comment_table")
    suspend fun deleteAll()
}