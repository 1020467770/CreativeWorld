package cn.sqh.creativeworld.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Environment
import android.text.format.DateFormat
import android.util.TypedValue
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.use
import androidx.core.os.EnvironmentCompat
import cn.sqh.creativeworld.R
import com.blankj.utilcode.util.LogUtils
import com.danikula.videocache.file.Md5FileNameGenerator
import okhttp3.MediaType
import okhttp3.RequestBody
import java.security.MessageDigest
import java.util.*

/**
 * Material推荐的获取主题style的工具类
 */
@ColorInt
@SuppressLint("Recycle")
fun Context.themeColor(
    @AttrRes themeAttrId: Int
): Int {
    return obtainStyledAttributes(
        intArrayOf(themeAttrId)
    ).use {
        it.getColor(0, Color.MAGENTA)
    }
}

@StyleRes
fun Context.themeStyle(@AttrRes attr: Int): Int {
    val tv = TypedValue()
    theme.resolveAttribute(attr, tv, true)
    return tv.data
}

@SuppressLint("Recycle")
fun Context.themeInterpolator(@AttrRes attr: Int): Interpolator {
    return AnimationUtils.loadInterpolator(
        this,
        obtainStyledAttributes(intArrayOf(attr)).use {//线程安全
            it.getResourceId(0, android.R.interpolator.fast_out_slow_in)
        }
    )
}

fun Context.getDrawableOrNull(@DrawableRes id: Int?): Drawable? {
    return if (id == null || id == 0) null else AppCompatResources.getDrawable(this, id)
}

@Suppress("DEPRECATION")
@SuppressLint("MissingPermission")
fun Context.isConnectedNetwork(): Boolean = run {
    val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    activeNetwork?.isConnectedOrConnecting == true
}

const val DATE_FORMAT = "yyyy-MM-dd"
fun Date.localFormat(context: Context): String = run {
    //这里是因为后端传的日期有误，不应该是GMT时间，应该是GTM+8，不然我的Date解析时会多八个小时
    val currentTimeMillis = System.currentTimeMillis() + TimeZone.getTimeZone("GMT+8:00").rawOffset
    val differenceInMills = currentTimeMillis - this.time
    val differenceInSeconds = differenceInMills / 1000
    val differenceInMinutes = differenceInSeconds / 60
    val differenceInHours = differenceInMinutes / 60
    val differenceInDays = differenceInHours / 24
    var formatTime: String = ""
    if (differenceInSeconds <= 0) {
        formatTime = DateFormat.format(DATE_FORMAT, this).toString()
    } else if (differenceInSeconds < 60) {
        formatTime =
            String.format(context.getString(R.string.seconds_ago), differenceInSeconds)
    } else if (differenceInMinutes < 60) {
        formatTime =
            String.format(context.getString(R.string.minutes_ago), differenceInMinutes)
    } else if (differenceInHours < 24) {
        formatTime =
            String.format(context.getString(R.string.hours_ago), differenceInHours)
    } else if (differenceInDays <= 3) {
        formatTime =
            String.format(context.getString(R.string.days_ago), differenceInDays)
    } else {
        formatTime = DateFormat.format(DATE_FORMAT, this).toString()
    }
    formatTime
}

fun Date.toFormatString(): String = run {
    DateFormat.format(DATE_FORMAT, this).toString()
}

fun Bitmap.saveToStorage(context: Context, fileName: String) {
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        //SD卡已装入
//        val encodedFileName = fileName.encodeByMD5()
//        val imageFileName = "JPEG_cover_$encodedFileName.jpg"
        val storageDir = context.getExternalFilesDir("uploading_video_cover_tmp")
        LogUtils.d("$storageDir, isDir=${storageDir?.isDirectory}")
    }


}

fun String.encodeByMD5() = buildString {
    MessageDigest.getInstance("MD5").apply {
        update(this@encodeByMD5.toByteArray())
        digest().forEach {
            this.apply {
                append(Integer.toHexString(it.toInt() and 0xff))
            }
        }
    }
}

fun String.toResponseBody() = RequestBody.create(MediaType.parse("text/plain"), this)