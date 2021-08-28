package cn.sqh.creativeworld.ui.videoDetail

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckedTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cn.sqh.creativeworld.MainActivity
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.data.UserId
import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.core.interfaces.FollowToUserListener
import cn.sqh.creativeworld.core.interfaces.SendCommentActionListener
import cn.sqh.creativeworld.databinding.FragmentVideoDetailLayoutBinding
import cn.sqh.creativeworld.ui.comment.CommentFragment
import cn.sqh.creativeworld.ui.videoDetail.fragments.VideoDescriptionFragment
import cn.sqh.creativeworld.utils.themeColor
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.transition.MaterialContainerTransform
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

@AndroidEntryPoint
class VideoDetailFragment : Fragment(), FollowToUserListener, SendCommentActionListener {

    private val args: VideoDetailFragmentArgs by navArgs()
    private val videoId: VideoId by lazy(LazyThreadSafetyMode.NONE) { args.videoId }

    //和VideoDescFragment共享一个ViewModel
    private val detailViewModel: VideoDetailViewModel by activityViewModels()

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
//            detailViewModel.setVideoId(videoId)
//            video = detailViewModel.video
            followListener = this@VideoDetailFragment
        }
        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailViewModel.failure.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })

        mDataBinding.run {
            viewModel = detailViewModel.apply {
                /**
                 * 最合理的情况应该是VideoDetailModel再分出一个静态Model存放不易变化的值，比如作者头像url，作者名字，
                 * 作为data class传给视图层，不用观察
                 * 以及一个存放易变值的Model，比如关注数，粉丝数，视频数等，使用LiveData包裹或者
                 * 使用Observe变量传给视图层进行观察。
                 * 不过那样太麻烦了，要多写几个类，时间不太够了，先都当做易变值处理吧。
                 */
                getVideoDetail(videoId).observe(viewLifecycleOwner) { videoDetailInfo ->
                    setupVideoPlayer(
                        videoDetailInfo.name,
                        videoDetailInfo.url,
                        videoDetailInfo.videoCoverUrl
                    )
                }
            }

            videoPlayer.apply {
                mVideoPlayer = this
            }
            fragmentList.add(VideoDescriptionFragment.newInstance(1, videoId))
            fragmentList.add(
                CommentFragment.newInstance(
                    videoId,
                    CommentFragment.CommentType.VideoComment
                )
            )
            vpNavigation.apply {
                adapter = object : FragmentStateAdapter(this@VideoDetailFragment) {
                    override fun getItemCount() = fragmentList.size

                    override fun createFragment(position: Int): Fragment {
                        return fragmentList[position]
                    }
                }
                this.offscreenPageLimit = 1
                registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        val activity = requireActivity() as MainActivity
                        val view = fragmentList[position].view
                        view?.let {
                            updatePagerHeightForChild(view, vpNavigation)
                        }
                        if (position == 1) {
                            activity.onCommentFragmentShow(videoId, this@VideoDetailFragment)
                        } else {
                            activity.onCommentFragmentHide()
                        }
                    }
                })
            }
            TabLayoutMediator(
                tabNavigation,
                vpNavigation,
                TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                    tab.text = titles[position]
                }
            ).attach()

        }
    }

    private fun updatePagerHeightForChild(view: View, pager: ViewPager2) {
        view.post {
            val wMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
            val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(wMeasureSpec, hMeasureSpec)

            if (pager.layoutParams.height != view.measuredHeight) {
                pager.layoutParams = (pager.layoutParams)
                    .also { lp ->
                        lp.height = view.measuredHeight
                    }
            }
        }

    }

    private fun setupVideoPlayer(videoTitle: String, videoUrl: String, coverUrl: String) {
        mVideoPlayer.setUp(videoUrl, true, videoTitle)
        ImageView(requireContext()).let { tmpImageView ->
            Glide.with(requireContext())
                .load(coverUrl)
                .into(tmpImageView)
            mVideoPlayer.thumbImageView = tmpImageView
        }
        /*mVideoPlayer.setVideoAllCallBack(object : VideoCallBackAdapter() {

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
        })*/
        mVideoPlayer.startButton.setOnClickListener {
            val videoInfo =
                Bundle().apply {
                    putString(VideoPlayActivity.BUNDLE_VIDEO_TITLE, videoTitle)
                    putString(VideoPlayActivity.BUNDLE_VIDEO_URL, videoUrl)
                }
            VideoPlayActivity.start(requireContext(), true, videoInfo)
        }
        mVideoPlayer.backButton.visibility = View.VISIBLE
        mVideoPlayer.titleTextView.visibility = View.VISIBLE
        mVideoPlayer.fullscreenButton.setOnClickListener {
            mOrientationUtils?.resolveByClick()
        }
        mVideoPlayer.backButton.setOnClickListener {
            backToHome()
        }
    }

    private fun backToHome() {
        fragmentList[1].onPause()
        findNavController().navigateUp()
    }

    override fun onFollowAction(view: View, userId: UserId) {
        if (view is CheckedTextView) {
            if (view.isChecked == false) {
                view.isChecked = true
                detailViewModel.followTo(userId)
            } else {
                view.isChecked = false
            }
        }
    }

    override fun sendComment(targetId: Long, content: String, callback: suspend () -> Unit) {
        detailViewModel.makeComment(targetId, content, callback)
    }

}

