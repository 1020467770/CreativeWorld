package cn.sqh.creativeworld.core.mapper

import cn.sqh.creativeworld.core.data.Video
import cn.sqh.creativeworld.ui.home.data.VideoPreviewModel

class VideoEntity2VideoPreviewModelMapper : Mapper<Video, VideoPreviewModel> {
    override fun map(video: Video): VideoPreviewModel = video.run {
        VideoPreviewModel(
            id = id,
            videoName = name,
            uploaderName = owner?.username ?: "无名氏",
            playNumber = playNumber,
            likerNumber = likerNumber,
            previewImageUrl = coverUrl,
            profileImageUrl = owner?.avatarPath ?: "http://159.75.31.178:5000/static/avatars/0.jpg"
        )
    }
}