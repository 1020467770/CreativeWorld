package cn.sqh.creativeworld.core.mapper

import cn.sqh.creativeworld.CreativeWorldApplication
import cn.sqh.creativeworld.core.data.TweetCommentEntity
import cn.sqh.creativeworld.core.data.VideoCommentEntity
import cn.sqh.creativeworld.ui.comment.data.CommentModel
import cn.sqh.creativeworld.utils.localFormat

class TweetCommentEntity2CommentModelMapper : Mapper<TweetCommentEntity, CommentModel> {
    override fun map(commentEntity: TweetCommentEntity): CommentModel = commentEntity.run {
        CommentModel(
            commenter = owner?.username ?: "",
            commenterAvatarPath = owner?.avatarPath
                ?: "http://159.75.31.178:5000/static/avatars/0.jpg",
            commentTime = commentDate.localFormat(CreativeWorldApplication.context),
            content = content
        )
    }
}