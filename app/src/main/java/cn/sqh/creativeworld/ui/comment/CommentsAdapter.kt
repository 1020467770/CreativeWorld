package cn.sqh.creativeworld.ui.comment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.DataBoundPagingAdapter
import cn.sqh.creativeworld.core.data.VideoCommentEntity
import cn.sqh.creativeworld.databinding.ItemCommentLayoutBinding
import cn.sqh.creativeworld.databinding.ItemTweetLayoutBinding
import cn.sqh.creativeworld.ui.comment.data.CommentModel
import cn.sqh.creativeworld.ui.tweets.data.TweetPreviewModel

class CommentsAdapter() :
    DataBoundPagingAdapter<CommentModel, ItemCommentLayoutBinding>(CommentModel.diffUtil) {
    override fun createBinding(parent: ViewGroup): ItemCommentLayoutBinding {
        val binding = DataBindingUtil.inflate<ItemCommentLayoutBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_comment_layout,
            parent,
            false
        )
        return binding
    }

    override fun bind(binding: ItemCommentLayoutBinding, item: CommentModel) {
        binding.comment = item
    }
}