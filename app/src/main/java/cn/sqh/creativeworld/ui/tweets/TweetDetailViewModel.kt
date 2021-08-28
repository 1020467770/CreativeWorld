package cn.sqh.creativeworld.ui.tweets

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import cn.sqh.creativeworld.core.data.TweetId
import cn.sqh.creativeworld.core.data.doFailure
import cn.sqh.creativeworld.core.data.doSuccess
import cn.sqh.creativeworld.repository.tweet.ITweetRepository
import cn.sqh.creativeworld.ui.tweets.data.TweetPreviewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest

@FlowPreview
@ExperimentalCoroutinesApi
class TweetDetailViewModel @ViewModelInject constructor(
    private val tweetRepository: ITweetRepository
) : ViewModel() {

    private val _tweetLiveData = MutableLiveData<TweetPreviewModel>()

    //给dataBinding用的LiveData
    val tweetLiveData = _tweetLiveData as LiveData<TweetPreviewModel>

    private val _failure = MutableLiveData<String>()
    val failure = _failure

    //给Fragment观察的LiveData，因为没人观察LiveData，LiveData不会被启动，binding的LiveData也就不会更新
    fun getTweetDetail(tweetId: TweetId) = liveData<TweetPreviewModel> {
        tweetRepository.getTweetInfo(tweetId)
            .collectLatest { result ->
                result.doSuccess { tweetModel ->
                    _tweetLiveData.postValue(tweetModel)
                    emit(tweetModel)
                }
                result.doFailure { failure ->
                    _failure.postValue(failure?.message ?: "未知异常，获取Tweet失败")
                }
            }

    }
}