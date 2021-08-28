package cn.sqh.creativeworld.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.sqh.creativeworld.repository.video.IVideoRepository
import cn.sqh.creativeworld.network.data.PageInfo
import cn.sqh.creativeworld.repository.old.UserRepository
import cn.sqh.creativeworld.ui.home.data.VideoPreviewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


/**
 * 重构之后的ViewModel，使用Hilt进行Repository注入，减少内存占用
 * 同时使用Flow进行与仓库层的通信
 */
@FlowPreview
@ExperimentalCoroutinesApi
class HomeViewModel2 @ViewModelInject constructor(
    private val videoRepository: IVideoRepository
) : ViewModel() {

    val loginUser = UserRepository.loggedInUser?.user

    fun getVideos(
        pageSize: Int = PageInfo.DEFAULT_PAGE_SIZE
    ): LiveData<PagingData<VideoPreviewModel>> {
        return videoRepository.getVideos(pageSize).cachedIn(viewModelScope).asLiveData()
    }

    val videosFlow = videoRepository.getVideos().cachedIn(viewModelScope)
}