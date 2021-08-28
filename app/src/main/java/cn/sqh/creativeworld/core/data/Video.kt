package cn.sqh.creativeworld.core.data

import androidx.recyclerview.widget.DiffUtil
import androidx.room.*
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
data class Video @JvmOverloads constructor(


    @Embedded(prefix = "owner_")
    var owner: User?,

    @PrimaryKey(autoGenerate = false)
    @Expose
    @SerializedName("videoId")
    var id: VideoId = 0,

    @Expose
    @SerializedName("videoName")
    var name: String = "",

    @Expose
    @SerializedName("ownerName")
    var uploader: String = "",

    @Expose
    @SerializedName("videoDescribe")
    var description: String? = "",

    @Expose
    @SerializedName("videoViewNum")
    var playNumber: Long = 0,

    @Expose
    @SerializedName("videoLikeNum")
    var likerNumber: Long = 0,

    @Expose
    @SerializedName("videoDislikeNum")
    var dislikeNumber: Long = 0,

    @Expose
    @SerializedName("videoCollectedNum")
    var collectorNumber: Long = 0,

    @Expose
    @SerializedName("videoRetweetNum")
    var forwarderNumber: Long = 0,

    @Expose
    @SerializedName("videoURL")
    var url: String = "",

    @Expose
    @SerializedName("coverURL")
    var coverUrl: String = "",

    @Expose
    @JsonAdapter(GsonDateTypeConverter::class)
    @SerializedName("videoUploadDate")
    var uploadDate: Date,

    var nextPage: Int = 0

)

object VideoDiff : DiffUtil.ItemCallback<Video>() {
    override fun areItemsTheSame(oldItem: Video, newItem: Video) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Video, newItem: Video) = oldItem == newItem
}

