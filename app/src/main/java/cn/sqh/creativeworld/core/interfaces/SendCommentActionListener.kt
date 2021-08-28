package cn.sqh.creativeworld.core.interfaces

interface SendCommentActionListener {
    fun sendComment(targetId: Long, content: String, callback: suspend () -> Unit)
}