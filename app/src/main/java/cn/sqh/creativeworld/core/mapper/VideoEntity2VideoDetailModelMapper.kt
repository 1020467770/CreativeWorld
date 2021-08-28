package cn.sqh.creativeworld.core.mapper

import cn.sqh.creativeworld.CreativeWorldApplication
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.data.Video
import cn.sqh.creativeworld.ui.videoDetail.VideoDetailModel
import cn.sqh.creativeworld.utils.localFormat
import cn.sqh.creativeworld.utils.toFormatString

class VideoEntity2VideoDetailModelMapper : Mapper<Video, VideoDetailModel> {
    override fun map(video: Video): VideoDetailModel = video.run {
        VideoDetailModel(
            id = id,
            owner = video.owner,
            name = name,
            fansCounts = owner?.fansCounts ?: 0,
            videoCounts = owner?.videoCounts ?: 0,
            videoCoverUrl = coverUrl,
            profileUrl = video.owner?.avatarPath
                ?: "http://159.75.31.178:5000/static/avatars/0.jpg",
            uploader = uploader,
            description = description ?: CreativeWorldApplication.context.getString(R.string.empty_video_description),
            playNumber = playNumber.toString(),
            likerNumber = likerNumber.toString(),
            dislikeNumber = dislikeNumber.toString(),
            collectorNumber = collectorNumber.toString(),
            forwarderNumber = forwarderNumber.toString(),
            url = url,
            uploadDate = uploadDate.toFormatString()
        )
    }
}