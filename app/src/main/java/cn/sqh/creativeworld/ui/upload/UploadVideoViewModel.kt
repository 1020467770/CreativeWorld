package cn.sqh.creativeworld.ui.upload

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.sqh.creativeworld.network.service.VideoService
import cn.sqh.creativeworld.repository.tweet.ITweetRepository
import cn.sqh.creativeworld.ui.home.data.VideoApiModel
import cn.sqh.creativeworld.ui.upload.data.UploadingVideo
import cn.sqh.creativeworld.utils.toResponseBody
import com.blankj.utilcode.util.LogUtils
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.*

class UploadVideoViewModel @ViewModelInject constructor(
    private val videoApi: VideoService
) : ViewModel() {


    private val _videoFile = MutableLiveData<LocalMedia?>()
    private val _coverPath = MutableLiveData<String>()

    val videoFile = _videoFile as LiveData<LocalMedia?>
    val coverPath = _coverPath as LiveData<String>

    val uploadingVideo = UploadingVideo()

    init {
        _videoFile.value = null
    }


    fun setVideoFile(file: LocalMedia?) {
        if (file != null && _videoFile.value != file) {
            _videoFile.value = file
        }
    }

    fun setCoverPath(path: String?) {
        if (path != null && _coverPath.value != path) {
            _coverPath.value = path!!
        }
    }

    @InternalCoroutinesApi
    fun uploadVideo(callback: suspend () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val videoFileInfo = videoFile.value
                val coverFile = uploadingVideo.coverFile
                var videoDesc = uploadingVideo.videoDesc
                if (videoDesc.isEmpty()) videoDesc = "无"
                if (videoFileInfo != null && coverFile != null) {
                    flow<VideoApiModel.OneVideoResult> {
                        val result = videoApi.uploadVideo(
                            uploadingVideo.videoTitle.toResponseBody(),
                            videoDesc.toResponseBody(),
                            createVideoPartBody(videoFileInfo.path),
                            createCoverPartBody(coverFile)
                        )
                        emit(result)
                    }.collect {
//                        LogUtils.d("服务器响应=${it.data}")
                        withContext(Dispatchers.Main) {
                            callback()
                        }
                    }
                }
            }
        }
    }


//    private fun createVideoMultipartBody(videoPath: String) {
//        val file = File(videoPath)
//        val requestBody = RequestBody.create(MediaType.parse("video/*"), file)
//    }

    private fun createVideoPartBody(videoPath: String) = File(videoPath).run {
        MultipartBody.Part.createFormData(
            "videoFile",
            this.name,
            RequestBody.create(MediaType.parse("video/mp4"), this)
        )
    }


    private fun createCoverPartBody(coverBytes: ByteArray) =
        MultipartBody.Part.createFormData(
            "coverFile",
            "pic_${UUID.randomUUID()}.jpg",
            RequestBody.create(MediaType.parse("image/jpg"), coverBytes)
        )

}




