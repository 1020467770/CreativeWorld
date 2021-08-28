package cn.sqh.creativeworld.ui.upload.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.File

data class UploadingVideo(
    var videoTitle: String = "",
    var videoDesc: String = "",
    var coverFile: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UploadingVideo

        if (videoTitle != other.videoTitle) return false
        if (videoDesc != other.videoDesc) return false
        if (coverFile != null) {
            if (other.coverFile == null) return false
            if (!coverFile.contentEquals(other.coverFile)) return false
        } else if (other.coverFile != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = videoTitle.hashCode()
        result = 31 * result + videoDesc.hashCode()
        result = 31 * result + (coverFile?.contentHashCode() ?: 0)
        return result
    }


}