package cn.sqh.creativeworld.ui.videoDetail

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import cn.sqh.creativeworld.core.data.Resource
import cn.sqh.creativeworld.core.data.Video
import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.repository.VideoRepository

class VideoDetailViewModel : ViewModel() {
    private val _videoIdLiveData = MutableLiveData<VideoId>()

    val video = _videoIdLiveData.switchMap { videoId ->
        VideoRepository.getVideoById(videoId)
    }

    fun setVideoId(videoId: VideoId) {
        if (_videoIdLiveData.value != videoId)
            _videoIdLiveData.value = videoId
    }

}