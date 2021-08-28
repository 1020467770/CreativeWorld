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

typealias TweetCommentId = Long

@Entity(
    tableName = "tweet_comment_table",
)
data class TweetCommentEntity(

    @Embedded(prefix = "owner_")
    var owner: User? = null,

    @Expose
    @SerializedName("comment")
    var content: String = "",

    @Expose
    @SerializedName("userId")
    var userId: UserId = 0L,

    @Expose
    @JsonAdapter(GsonDateTypeConverter::class)
    @SerializedName("date")
    var commentDate: Date = Date(),

    var nextPage: Int = 0,

    @Expose
    @SerializedName("commentId")
    @PrimaryKey
    var id: TweetCommentId,

    @Expose
    @SerializedName("tweetId")
    var tweetId: TweetId

)
