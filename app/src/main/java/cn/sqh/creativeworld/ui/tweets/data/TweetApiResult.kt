package cn.sqh.creativeworld.ui.tweets.data

import cn.sqh.creativeworld.network.data.ApiMultiResult
import cn.sqh.creativeworld.network.data.ApiSingleResult
import cn.sqh.creativeworld.core.data.TweetEntity
import cn.sqh.creativeworld.core.data.User
import com.google.gson.annotations.Expose

sealed class TweetApiResult {

    class TweetsResult : ApiMultiResult<TweetApiInfo>()

    class OneTweetResult : ApiSingleResult<TweetApiInfo>()

    data class TweetApiInfo(
        @Expose
        var user: User?,
        @Expose
        var tweet: TweetEntity?
    )

}

