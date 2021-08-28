package cn.sqh.creativeworld.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cn.sqh.creativeworld.CreativeWorldApplication
import cn.sqh.creativeworld.core.data.*

@Database(
    entities = [
        User::class,
        Video::class,
        TweetEntity::class,
        VideoCommentEntity::class,
        TweetCommentEntity::class
    ],
    version = 1, exportSchema = false
)
@TypeConverters(DataTypeConverter::class)//注册转换器类
abstract class CreativeWorldDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun videoDao(): VideoDao
    abstract fun tweetDao(): TweetDao
    abstract fun videoCommentDao(): VideoCommentDao
    abstract fun tweetCommentDao(): TweetCommentDao

    companion object {

        private val DATABASE_NAME = "creative_world_database"

        private var instance: CreativeWorldDatabase? = null

        @Synchronized
        fun getDatabase(): CreativeWorldDatabase {
            instance?.let {
                return it
            }
            return Room.databaseBuilder(
                CreativeWorldApplication.context.applicationContext,//第一个参数必须使用applicationContext上下文，否则容易出现内存泄漏
                CreativeWorldDatabase::class.java, DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
//                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build().apply {
                    instance = this
                }
        }
    }

}