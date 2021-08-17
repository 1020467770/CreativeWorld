package cn.sqh.creativeworld.core.data

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import cn.sqh.creativeworld.network.typeConverter.GsonDateTypeConverter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.util.*

typealias VideoId = Long

@Entity(
    tableName = "video_table",
    indices = [
        Index("url")
    ]
)
data class Video(

    @Embedded(prefix = "owner_")
    var owner: User?,

    @PrimaryKey(autoGenerate = false)
    @Expose
    @SerializedName("videoId")
    val id: VideoId,

    @Expose
    @SerializedName("videoName")
    val name: String,

    @Expose
    @SerializedName("ownerName")
    val uploader: String,

    @Expose
    @SerializedName("videoDescribe")
    val description: String?,

    var playNumber: Long,

    @Expose
    @SerializedName("videoLikeNum")
    var likerNumber: Long,

    @Expose
    @SerializedName("videoDislikeNum")
    var dislikeNumber: Long,

    @Expose
    @SerializedName("videoCollectedNum")
    var collectorNumber: Long,

    @Expose
    @SerializedName("videoRetweetNum")
    var forwarderNumber: Long,

    @Expose
    @SerializedName("videoURL")
    var url: String,

    @Expose
    @JsonAdapter(GsonDateTypeConverter::class)
    @SerializedName("videoUploadDate")
    var uploadDate: Date,
) {
//    val transitionName = "$id$name"
}

object VideoDiff : DiffUtil.ItemCallback<Video>() {
    override fun areItemsTheSame(oldItem: Video, newItem: Video) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Video, newItem: Video) = oldItem == newItem
}

/*data class Video2(
    @Expose
    @SerializedName("videoId")
    val id: VideoId,

    @Expose
    @SerializedName("videoName")
    val name: String,

    @Expose
    @SerializedName("ownerName")
    val uploader: String,

    @Expose
    @SerializedName("videoDescribe")
    val description: String,

    var playNumber: Long,

    @Expose
    @SerializedName("videoLikeNum")
    var likerNumber: Long,

) {
    val transitionName = "$id$name"
}*/

/*
val videos = listOf(
    Video2(
        id = 0L,
        name = "奥特曼和怪兽的恋爱故事",
        uploader = "zhangsan",
        description = "没什么好描述的",
        playNumber = 12345,
        likerNumber = 1234
    ),
    Video2(
        id = 1L,
        name = "史诗级人类高质量对局",
        uploader = "lisi",
        description = "没什么好描述的",
        playNumber = 11111,
        likerNumber = 1223
    ),
    Video2(
        id = 2L,
        name = "【震惊】一女人与狗竟在广场上干出如此骇人之事！！",
        uploader = "wangwu",
        description = "没什么好描述的",
        playNumber = 10087,
        likerNumber = 1234
    ),
    Video2(
        id = 3L,
        name = "【大新闻】我是标题党",
        uploader = "zhaoliu",
        description = "没什么好描述的",
        playNumber = 10086,
        likerNumber = 123
    ),
    Video2(
        id = 4L,
        name = "爱了！不愧是我的牙签哥哥",
        uploader = "zhangsan",
        description = "没什么好描述的",
        playNumber = 1600,
        likerNumber = 1234
    ),
    Video2(
        id = 5L,
        name = "东 京 奥 运 会 ！ ！ ！",
        uploader = "zhangsan",
        description = "没什么好描述的",
        playNumber = 12345,
        likerNumber = 1234
    ),
    Video2(
        id = 6L,
        name = "我的英语可好了，看我给你秀一段rap：My English is very good good ",
        uploader = "zhangsan",
        description = "没什么好描述的",
        playNumber = 11233,
        likerNumber = 36
    )
)
*/

/*
object VideoRepo {
    fun getVideo(id: VideoId) = videos.find { it.id == id } ?: videos.last()
}
*/
