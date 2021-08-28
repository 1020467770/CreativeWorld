package cn.sqh.creativeworld.core.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.sqh.creativeworld.core.data.TweetEntity
import cn.sqh.creativeworld.core.data.TweetId
import cn.sqh.creativeworld.core.data.Video

@Dao
interface TweetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(tweetEntity: TweetEntity): TweetId

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(tweetList: List<TweetEntity>)

    @Query("SELECT * FROM tweet_table")
    fun getAllTweets(): PagingSource<Int, TweetEntity>

    @Query("SELECT * FROM tweet_table WHERE id=:id")
    suspend fun getTweetById(id: TweetId): TweetEntity?

    @Query("DELETE FROM tweet_table")
    suspend fun deleteAll()
}