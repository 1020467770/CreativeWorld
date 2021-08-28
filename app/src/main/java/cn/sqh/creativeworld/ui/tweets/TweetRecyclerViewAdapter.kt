package cn.sqh.creativeworld.ui.tweets

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.DataBoundPagingAdapter
import cn.sqh.creativeworld.core.data.TweetId
import cn.sqh.creativeworld.databinding.ItemTweetLayoutBinding
import cn.sqh.creativeworld.ui.tweets.data.TweetPreviewModel

import cn.sqh.creativeworld.ui.tweets.dummy.DummyContent.DummyItem

class TweetRecyclerViewAdapter(
    private val onClickListener: TweetViewClickListener
) :
    DataBoundPagingAdapter<TweetPreviewModel, ItemTweetLayoutBinding>(TweetPreviewModel.diffUtil) {

    interface TweetViewClickListener {
        fun onClickTweet(view: View, tweetId: TweetId)
//        fun onLongPressedTweet(video: VideoPreviewModel): Boolean
    }

    override fun createBinding(parent: ViewGroup): ItemTweetLayoutBinding {
        val binding = DataBindingUtil.inflate<ItemTweetLayoutBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_tweet_layout,
            parent,
            false
        )
        return binding
    }

    override fun bind(binding: ItemTweetLayoutBinding, item: TweetPreviewModel) {
        binding.tweet = item
        binding.clickListener = onClickListener
    }

}