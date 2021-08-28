package cn.sqh.creativeworld.ui.home

import androidx.lifecycle.*
import cn.sqh.creativeworld.core.data.Resource
import cn.sqh.creativeworld.core.data.Video
import cn.sqh.creativeworld.repository.old.VideoRepository
import cn.sqh.creativeworld.network.data.PageInfo
import com.blankj.utilcode.util.LogUtils

/**
 * 该版本ViewModel已废弃，供学习使用
 */
class HomeViewModel : ViewModel() {

    private val _videosLiveData = MediatorLiveData<Resource<List<Video>>>()
    private var _currentPage = 1

    /**
     * 原始版本需要很多变量来保证在分页时不重复获取视频，重构后只需由Paging进行判断
     */
    private var isAskForNextPageFinished: Boolean = true//在获取视频期间保证不会重复获取视频
    private var isRemoteServerFileEmpty: Boolean = false//如果服务器没视频可以看了就不能再获取视频了

    val videos = _videosLiveData as LiveData<Resource<List<Video>>>

    fun getVideos(
        page: Int = PageInfo.DEFAULT_PAGE_NUM,
        pageSize: Int = PageInfo.DEFAULT_PAGE_SIZE
    ) {
        _currentPage = page
        val liveData = VideoRepository.getVideos(page, pageSize)
        _videosLiveData.addSource(liveData) {
            LogUtils.d("addSource1执行了,result=${it}")
            _videosLiveData.value = it
            if (!isRemoteServerFileEmpty && it.status != Resource.Status.LOADING && !isAskForNextPageFinished) {
                if (it.data == null || it.data.isEmpty()) {
                    isRemoteServerFileEmpty = true
                }
                isAskForNextPageFinished = true
            }
        }
    }

    fun getNextPageVideos() {
        if (isAskForNextPageFinished && !isRemoteServerFileEmpty) {
            isAskForNextPageFinished = false
            getVideos(_currentPage.plus(1))
        }
    }

}