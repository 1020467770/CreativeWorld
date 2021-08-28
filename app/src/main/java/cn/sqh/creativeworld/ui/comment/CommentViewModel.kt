package cn.sqh.creativeworld.ui.comment

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import cn.sqh.creativeworld.repository.comment.ICommentRepository
import cn.sqh.creativeworld.repository.tweet.ITweetRepository
import kotlinx.coroutines.flow.flatMapLatest

class CommentViewModel @ViewModelInject constructor(
    private val commentRepository: ICommentRepository
) : ViewModel() {

    private val _targetIdLiveData = MutableLiveData<Long>()

    //    val videoCommentsFlow = commentRepository.getVideoComments().cachedIn(vid)
    val videoCommentsFlow = _targetIdLiveData.asFlow().flatMapLatest {
        commentRepository.getVideoComments(it)
    }.cachedIn(viewModelScope)

    val tweetCommentsFlow = _targetIdLiveData.asFlow().flatMapLatest {
        commentRepository.getTweetComments(it)
    }.cachedIn(viewModelScope)

    fun setTargetId(targetId: Long) {
        _targetIdLiveData.value = targetId
    }

}