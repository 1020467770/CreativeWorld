package cn.sqh.creativeworld.ui.tweets.data

import androidx.recyclerview.widget.DiffUtil
import cn.sqh.creativeworld.core.data.TweetId

data class TweetPreviewModel(
    val id: TweetId,
    val ownerName: String,
    val uploadTime: String,
    val recommendNumber: String,
    val forwardNumber: String,
    val commentNumber: String,
    val content: String,
    val profileUrl: String,
    val hasAttachments: Boolean
) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<TweetPreviewModel>() {
            override fun areItemsTheSame(
                oldItem: TweetPreviewModel,
                newItem: TweetPreviewModel
            ): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: TweetPreviewModel,
                newItem: TweetPreviewModel
            ): Boolean = oldItem == newItem

        }
    }
}