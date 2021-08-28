package cn.sqh.creativeworld.core.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import cn.sqh.creativeworld.network.typeConverter.GsonDateTypeConverter
import com.google.gson.annotations.Expose
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.util.*

typealias UserId = Long

@Entity(
    tableName = "user_table",
    indices = [
        Index("username")
    ]
)
data class User @JvmOverloads constructor(

    @Expose
    @SerializedName("userEmail")
    var email: String = "",

    @PrimaryKey(autoGenerate = false)
    @Expose
    @SerializedName("userId")
    var uid: UserId = 0,

    @Expose
    @SerializedName("userAvatarPath")
    var avatarPath: String = "",

    @Expose
    @SerializedName("userFansNumber")
    var fansCounts: Int = 0,

    @Expose
    @SerializedName("userFollowNumber")
    var followCounts: Int = 0,

    @Expose
    @SerializedName("userName")
    var username: String = "",


    @Expose
    @JsonAdapter(GsonDateTypeConverter::class)
    @SerializedName("userRegisterDate")
    var registerDate: Date ,

    @Expose
    @SerializedName("userSelfDescribe")
    var selfDescribe: String = "",

    @Expose
    @SerializedName("userTweetsNum")
    var tweetsCounts: Int = 0,//推文数

    @Expose
    @SerializedName("userVideosNum")
    var videoCounts: Int = 0
)