package cn.sqh.creativeworld.core.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cn.sqh.creativeworld.core.data.User
import cn.sqh.creativeworld.core.data.UserId
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User): UserId

    @Query("SELECT * FROM user_table WHERE uid=:uid")
    fun findByUserId(uid: UserId): LiveData<User>

    @Query("SELECT * FROM user_table WHERE email =:email")
    fun findByEmail(email: String): LiveData<User>

    /**
     * Flow版本
     */
    @Query("SELECT * FROM user_table WHERE uid=:uid")
    fun findByUserId_Flow(uid: UserId): Flow<User>

    @Query("SELECT * FROM user_table")
    fun findAll_Flow(): Flow<List<User>>

}