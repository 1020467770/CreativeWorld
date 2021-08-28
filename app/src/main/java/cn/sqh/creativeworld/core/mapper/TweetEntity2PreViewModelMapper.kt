package cn.sqh.creativeworld.core.mapper

import cn.sqh.creativeworld.CreativeWorldApplication
import cn.sqh.creativeworld.core.data.TweetEntity
import cn.sqh.creativeworld.ui.tweets.data.TweetPreviewModel
import cn.sqh.creativeworld.utils.localFormat
import java.util.*

class TweetEntity2PreViewModelMapper : Mapper<TweetEntity, TweetPreviewModel> {
    override fun map(tweetEntity: TweetEntity): TweetPreviewModel = tweetEntity.run {
        TweetPreviewModel(
            id = id,
            ownerName = ownerName,
            uploadTime = uploadDate.localFormat(CreativeWorldApplication.context),
            recommendNumber = tweetEntity.likerCounts.toString(),//这里可以用自定义方法转换成符合本地格式的显示形式，例如中文的1万或者英文的1 thousand等等
            forwardNumber = tweetEntity.forwardNum.toString(),
            commentNumber = "0",//后端没有实现评论，显示的评论数都定为0
            content = content,
            profileUrl = owner?.avatarPath ?: "http://159.75.31.178:5000/static/avatars/0.jpg",
            hasAttachments = false
        )
    }
}