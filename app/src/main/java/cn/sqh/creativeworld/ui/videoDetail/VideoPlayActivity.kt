package cn.sqh.creativeworld.ui.videoDetail

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import cn.sqh.creativeworld.R
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import tv.danmaku.ijk.media.player.IjkMediaPlayer

class VideoPlayActivity : AppCompatActivity() {

    private lateinit var videoPlayer: StandardGSYVideoPlayer
    private lateinit var orientationUtils: OrientationUtils

    private val isTransition by lazy { intent.getBooleanExtra(KEY_IS_TRANSITION, true) }
    private val videoTitle by lazy { intent.getBundleExtra(KEY_BUNDLE)?.getString(BUNDLE_VIDEO_TITLE) }
    private val videoUrl by lazy { intent.getBundleExtra(KEY_BUNDLE)?.getString(BUNDLE_VIDEO_URL) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_play)
        videoPlayer = findViewById(R.id.video_player)
        orientationUtils = OrientationUtils(this, videoPlayer)
        initVideoPlayer()
    }

    private fun initVideoPlayer() {
        LogUtils.d("videoUrl=$videoUrl ,videoTitle=$videoTitle")
//        IjkPlayerManager.setLogLevel(IjkMediaPlayer.IJK_LOG_SILENT)
        videoPlayer.setUp(videoUrl, true, videoTitle)
        videoPlayer.backButton.visibility = View.VISIBLE
        videoPlayer.titleTextView.visibility = View.VISIBLE
        videoPlayer.fullscreenButton.setOnClickListener {
            orientationUtils.resolveByClick()
        }
        videoPlayer.backButton.setOnClickListener {
            onBackPressed()
        }
        videoPlayer.setIsTouchWiget(true)
        videoPlayer.startPlayLogic()
    }

    override fun onPause() {
        super.onPause()
        videoPlayer.onVideoPause();
    }

    override fun onResume() {
        super.onResume()
        videoPlayer.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos();
        orientationUtils.releaseListener();
    }

    override fun onBackPressed() {
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.getFullscreenButton().performClick();
            return;
        }
        videoPlayer.setVideoAllCallBack(null);
        super.onBackPressed()
    }

    companion object {
        private const val KEY_IS_TRANSITION = "transition"
        private const val KEY_BUNDLE = "video-bundle"

        const val BUNDLE_VIDEO_TITLE = "video-title"
        const val BUNDLE_VIDEO_URL = "video-url"

        fun start(context: Context, isTransition: Boolean, videoInfo: Bundle) {
            Intent(context, VideoPlayActivity::class.java).apply {
                putExtra(KEY_IS_TRANSITION, isTransition)
                putExtra(KEY_BUNDLE, videoInfo)
            }.also { intent ->
                context.startActivity(intent)
            }
        }
    }
}