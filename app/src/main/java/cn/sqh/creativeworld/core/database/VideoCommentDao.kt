package cn.sqh.creativeworld.core.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.sqh.creativeworld.core.data.TweetEntity
import cn.sqh.creativeworld.core.data.VideoCommentEntity
import cn.sqh.creativeworld.core.data.VideoCommentId

@Dao
interface VideoCommentDao  {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(videoCommentEntity: VideoCommentEntity): VideoCommentId

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(commentList: List<VideoCommentEntity>)

    @Query("SELECT * FROM video_comment_table")
    fun getAllVideoComments(): PagingSource<Int, VideoCommentEntity>

    @Query("SELECT * FROM video_comment_table WHERE id=:id")
    suspend fun getVideoCommentById(id: VideoCommentId): VideoCommentEntity?

    @Query("DELETE FROM video_comment_table")
    suspend fun deleteAll()
}