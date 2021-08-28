package cn.sqh.creativeworld.ui.videoDetail

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import cn.sqh.creativeworld.core.data.UserId
import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.core.data.doFailure
import cn.sqh.creativeworld.core.data.doSuccess
import cn.sqh.creativeworld.network.service.UserService
import cn.sqh.creativeworld.network.service.VideoService
import cn.sqh.creativeworld.repository.old.UserRepository
import cn.sqh.creativeworld.repository.old.VideoRepository
import cn.sqh.creativeworld.repository.video.IVideoRepository
import cn.sqh.creativeworld.ui.tweets.data.TweetPreviewModel
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class VideoDetailViewModel @ViewModelInject constructor(
    private val videoRepository: IVideoRepository,
    private val userApi: UserService
) : ViewModel() {
    private val _videoIdLiveData = MutableLiveData<VideoId>()

    private val _videoLiveData = MutableLiveData<VideoDetailModel>()

    val videoLiveData = _videoLiveData as LiveData<VideoDetailModel>

    private val _failure = MutableLiveData<String>()
    val failure = _failure

    val video = _videoIdLiveData.switchMap { videoId ->
        VideoRepository.getVideoById(videoId)
    }

    fun setVideoId(videoId: VideoId) {
        if (_videoIdLiveData.value != videoId)
            _videoIdLiveData.value = videoId
    }

    fun getVideoDetail(videoId: VideoId) = liveData<VideoDetailModel> {
        videoRepository.getVideoInfo(videoId)
            .collectLatest { result ->
                result.doSuccess { tweetModel ->
                    _videoLiveData.postValue(tweetModel)
                    emit(tweetModel)
                }
                result.doFailure { failure ->
                    _failure.postValue(failure?.message ?: "未知异常，获取Tweet失败")
                }
            }

    }

    fun followTo(userId: UserId) {
        viewModelScope.launch {//使用ViewModel的Scope，viewModel的生命期结束会自动取消网络请求，不会继续收发
            withContext(Dispatchers.IO) {//去IO线程发送网络请求
                try {
//                    LogUtils.d("userId=${userId},token=${UserRepository.loggedInToken}")
                    userApi.follow(userId, UserRepository.loggedInToken)
                } catch (e: Exception) {
                    LogUtils.e(e)
                    _failure.postValue(e.message)
                }
            }
        }
    }

    fun makeComment(videoId: VideoId, content: String, callback: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                videoRepository.commentToVideo(videoId, content, callback)
            } catch (e: Exception) {
                LogUtils.e(e)
                _failure.postValue(e.message)
            }
        }
    }


}