package cn.sqh.creativeworld.core.database

import androidx.annotation.IntRange
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.sqh.creativeworld.core.data.User
import cn.sqh.creativeworld.core.data.UserId
import cn.sqh.creativeworld.core.data.Video
import cn.sqh.creativeworld.core.data.VideoId

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(video: Video): VideoId

    @Query("SELECT * FROM video_table WHERE id=:id")
    fun findByVideoId(id: VideoId): LiveData<Video>

    @Query("SELECT * FROM video_table WHERE id=:uid")
    fun findVideosByOwnerId(uid: UserId): LiveData<List<Video>>

    @Query("SELECT * FROM video_table")
    fun findAll(): LiveData<List<Video>>

    @Query("SELECT * FROM video_table LIMIT :pageSize OFFSET ((:page-1)*:pageSize)")
    fun findVideosByPage(
        @IntRange(from = 1L) page: Int,
        @IntRange(from = 1L) pageSize: Int
    ): LiveData<List<Video>>

}