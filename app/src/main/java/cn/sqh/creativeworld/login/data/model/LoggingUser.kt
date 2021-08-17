package cn.sqh.creativeworld.login.data.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


@Parcelize
data class LoggingUser(
    var email: String = "zhangsan@qq.com",
    var password: String = "123456"
): Parcelable