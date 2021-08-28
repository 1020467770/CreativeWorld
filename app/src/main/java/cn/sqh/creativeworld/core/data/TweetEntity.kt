package cn.sqh.creativeworld.core.data

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import cn.sqh.creativeworld.network.typeConverter.GsonDateTypeConverter
import cn.sqh.creativeworld.ui.tweets.data.TweetApiResult
import com.google.gson.annotations.Expose
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.util.*


typealias TweetId = Long

@Entity(
    tableName = "tweet_table"
)
data class TweetEntity(

    @Embedded(prefix = "owner_")
    var owner: User?,

    @PrimaryKey(autoGenerate = false)
    @Expose
    @SerializedName("tweetId")
    var id: TweetId,

    @Expose
    @SerializedName("ownerId")
    var uid: UserId,
    @Expose
    @SerializedName("ownerName")
    var ownerName: String,
    @Expose
    @SerializedName("tweetContent")
    var content: String,
    @Expose
    @SerializedName("tweetLikeNum")
    var likerCounts: Long,
    @Expose
    @SerializedName("tweetRetweetNum")
    var forwardNum: Long,
    @Expose
    @SerializedName("tweetDislikeNum")
    var dislikeCounts: TweetId,
    @Expose
    @SerializedName("tweetUploadDate")
    @JsonAdapter(GsonDateTypeConverter::class)
    var uploadDate: Date,

    var nextPage: Int = 0
)