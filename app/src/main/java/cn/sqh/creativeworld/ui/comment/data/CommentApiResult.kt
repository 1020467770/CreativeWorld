package cn.sqh.creativeworld.ui.comment.data

import cn.sqh.creativeworld.core.data.TweetCommentEntity
import cn.sqh.creativeworld.core.data.User
import cn.sqh.creativeworld.core.data.VideoCommentEntity
import cn.sqh.creativeworld.network.data.ApiMultiResult
import cn.sqh.creativeworld.network.data.ApiSingleResult
import com.google.gson.annotations.Expose

sealed class CommentApiResult {

    class VideoCommentsResult : ApiMultiResult<VideoCommentApiInfo>()
    class OneVideoCommentResult : ApiSingleResult<VideoCommentEntity>()

    data class VideoCommentApiInfo(
        @Expose
        var user: User,
        @Expose
        var comment: VideoCommentEntity
    )

    class TweetCommentsResult : ApiMultiResult<TweetCommentApiInfo>()

    data class TweetCommentApiInfo(
        @Expose
        var user: User,
        @Expose
        var comment: TweetCommentEntity
    )

}