package cn.sqh.creativeworld.ui.tweets

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.sqh.creativeworld.network.data.PageInfo
import cn.sqh.creativeworld.repository.tweet.ITweetRepository
import cn.sqh.creativeworld.ui.tweets.data.TweetPreviewModel
import com.blankj.utilcode.util.LogUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect

@FlowPreview
@ExperimentalCoroutinesApi
class TweetsPreviewViewModel @ViewModelInject constructor(
    private val tweetRepository: ITweetRepository
) : ViewModel() {

    private val _tweetPreviewLiveData = MutableLiveData<PagingData<TweetPreviewModel>>()
    val tweetPreviewLiveData = _tweetPreviewLiveData as LiveData<PagingData<TweetPreviewModel>>

    /*init {
        refreshTweets()
    }*/

    /*fun getTweets(
        pageSize: Int = PageInfo.DEFAULT_PAGE_SIZE
    ): LiveData<PagingData<TweetPreviewModel>> {
        return tweetRepository.getTweetsPreview(pageSize).cachedIn(viewModelScope).asLiveData()
    }*/

    val tweetsFlow = tweetRepository.getTweetsPreview().cachedIn(viewModelScope)

    fun refreshTweets() {
        /*getTweets().map {
            _tweetPreviewLiveData.value = it
        }*/
    }
}