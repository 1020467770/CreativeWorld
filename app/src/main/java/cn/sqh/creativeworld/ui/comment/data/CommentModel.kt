package cn.sqh.creativeworld.ui.comment.data

import androidx.recyclerview.widget.DiffUtil
import cn.sqh.creativeworld.ui.tweets.data.TweetPreviewModel

data class CommentModel(
    var commenter: String,
    var commenterAvatarPath: String,
    var commentTime: String,
    var content: String
) {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<CommentModel>() {
            override fun areItemsTheSame(
                oldItem: CommentModel,
                newItem: CommentModel
            ): Boolean =
                oldItem.commenter == newItem.commenter
                        && oldItem.content == newItem.content

            override fun areContentsTheSame(
                oldItem: CommentModel,
                newItem: CommentModel
            ): Boolean = oldItem == newItem

        }
    }
}
