package cn.sqh.creativeworld.ui.videoDetail

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.databinding.FragmentVideoDetailLayoutBinding
import cn.sqh.creativeworld.repository.VideoRepository
import cn.sqh.creativeworld.ui.bottomNav.NavigationStore
import cn.sqh.creativeworld.ui.videoDetail.fragments.CommentFragment
import cn.sqh.creativeworld.ui.videoDetail.fragments.VideoDescriptionFragment
import cn.sqh.creativeworld.utils.spring
import cn.sqh.creativeworld.utils.themeColor
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialContainerTransform
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.VideoAllCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer

class VideoDetailFragment : Fragment() {

    private val args: VideoDetailFragmentArgs by navArgs()
    private val videoId: Long by lazy(LazyThreadSafetyMode.NONE) { args.videoId }
    private val detailViewModel: VideoDetailViewModel by viewModels()

    private lateinit var mDataBinding: FragmentVideoDetailLayoutBinding
    private lateinit var mVideoPlayer: StandardGSYVideoPlayer
    private val fragmentList: MutableList<Fragment> = ArrayList()
    private val titles = arrayOf("详情", "评论")
    private var mOrientationUtils: OrientationUtils? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val interpolator = AnimationUtils.loadInterpolator(
            context,
            android.R.interpolator.fast_out_slow_in
        )
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = 300L
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
            this.interpolator = interpolator
        }

        /*sharedElementReturnTransition = MaterialContainerTransform().apply {
            duration = 300L
            this.interpolator = interpolator
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mDataBinding = FragmentVideoDetailLayoutBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            detailViewModel.setVideoId(videoId)
            video = detailViewModel.video

//            this.video = video//todo 换成从仓库里获取video
            /*toolbar.setNavigationOnClickListener {
                backToHome()
            }*/

        }
        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackCallback
        )

        mDataBinding.run {
            videoPlayer.apply {
                mVideoPlayer = this
//                initViewPlayer()
            }
            fragmentList.add(VideoDescriptionFragment.newInstance(1))
            fragmentList.add(CommentFragment.newInstance(1))
            vpNavigation.apply {
                adapter = object : FragmentStateAdapter(this@VideoDetailFragment) {
                    override fun getItemCount() = fragmentList.size

                    override fun createFragment(position: Int): Fragment {
                        return fragmentList[position]
                    }
                }
            }
            TabLayoutMediator(
                tabNavigation,
                vpNavigation,
                TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                    tab.text = titles[position]
                }
            ).attach()

            /*video_player?.let {
                mVideoPlayer = it
                initViewPlayer()
            }*/
        }
    }

    private fun initViewPlayer() {
        mOrientationUtils = OrientationUtils(activity, mVideoPlayer)
        mVideoPlayer.setUp("http://159.75.31.178:5000/static/videos/1.mp4", true, "测试视频")
        ImageView(requireContext()).let { tmpImageView ->
            Glide.with(requireContext())
                .load("http://159.75.31.178:5000/static/videoCover/1.jpeg")
                .centerCrop()
                .into(tmpImageView)
            mVideoPlayer.thumbImageView = tmpImageView
        }
        mVideoPlayer.setVideoAllCallBack(object : VideoCallBackAdapter() {
            override fun onPrepared(url: String?, vararg objects: Any?) {
                LogUtils.d("视频准备完毕")
                mDataBinding.videoUploader.spring(
                    SpringAnimation.X,
                    stiffness = 350f,
                    damping = 0.6f
                ).animateToFinalPosition(mDataBinding.subProfileArea.x)
                mDataBinding.videoUploader.spring(
                    SpringAnimation.Y,
                    stiffness = 350f,
                    damping = 0.6f
                ).animateToFinalPosition(mDataBinding.subProfileArea.y)
                mDataBinding.tvUploaderName.spring(
                    SpringAnimation.X,
                    stiffness = 350f,
                    damping = 0.6f
                ).animateToFinalPosition(mDataBinding.subNameArea.x)
                mDataBinding.tvUploaderName.spring(
                    SpringAnimation.Y,
                    stiffness = 350f,
                    damping = 0.6f
                ).animateToFinalPosition(mDataBinding.subNameArea.y)
            }
        })
        mVideoPlayer.backButton.visibility = View.VISIBLE
        mVideoPlayer.titleTextView.visibility = View.VISIBLE
        mVideoPlayer.fullscreenButton.setOnClickListener {
            mOrientationUtils?.resolveByClick()
        }
        mVideoPlayer.backButton.setOnClickListener {
            backToHome()
        }
        mVideoPlayer.setIsTouchWiget(true)
        mVideoPlayer.startPlayLogic()
    }

    override fun onPause() {
        super.onPause()
        mVideoPlayer.onVideoPause();
    }

    override fun onResume() {
        super.onResume()
        mVideoPlayer.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        GSYVideoManager.releaseAllVideos();
        mOrientationUtils?.releaseListener();
    }

    val onBackCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            backToHome()
        }
    }

    private fun backToHome() {
        if (mOrientationUtils?.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mVideoPlayer.getFullscreenButton().performClick();
        }
        //释放所有
        mVideoPlayer.setVideoAllCallBack(null);
        findNavController().navigateUp()
    }

}