package cn.sqh.creativeworld.ui.comment

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.Slide
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.interfaces.SendCommentActionListener
import cn.sqh.creativeworld.databinding.FragmentCommentListBinding
import cn.sqh.creativeworld.ui.common.pagingFooter.FooterAdapter
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.flow.collectLatest
import java.lang.RuntimeException

@AndroidEntryPoint
class CommentFragment private constructor(var showCommentListener: ShowCommentListener? = null) :
    Fragment() {

    private lateinit var mBinding: FragmentCommentListBinding

    private val commentViewModel: CommentViewModel by viewModels()

    //video或tweet的Id
    private val targetId: Long by lazy { requireArguments().getLong(ARG_TARGET_ID) }
    private val commentType: CommentType? by lazy {
        requireArguments().getParcelable(
            ARG_COMMENT_TYPE
        )
    }

    private val commentsAdapter = CommentsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.sqh_motion_duration_medium).toLong()
            addTarget(R.id.comment_recyclerview)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentCommentListBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
//            commentRecyclerview.isNestedScrollingEnabled = true
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        lifecycleScope.launchWhenCreated {
            when (commentType) {
                CommentType.TweetComment -> {
                    commentViewModel.tweetCommentsFlow.collectLatest {
                        commentsAdapter.submitData(it)
                    }
                }
                CommentType.VideoComment -> {
                    commentViewModel.videoCommentsFlow.collectLatest {
                        commentsAdapter.submitData(it)
                    }
                }
                else -> throw RuntimeException("评论类型转换失败")
            }
        }
        commentViewModel.setTargetId(targetId)
        mBinding.run {
            commentRecyclerview.apply {
                adapter = commentsAdapter.withLoadStateHeaderAndFooter(
                    FooterAdapter(commentsAdapter),
                    FooterAdapter(commentsAdapter)
                )
            }
        }
    }

    fun scrollToBottom() {
        mBinding.commentRecyclerview.scrollToPosition(commentsAdapter.itemCount - 1)
    }


    override fun onResume() {
        super.onResume()
        //切换到评论fragment才回调
//        showCommentListener?.onCommentFragmentShow(targetId)
    }

    override fun onPause() {
        super.onPause()
//        showCommentListener?.onCommentFragmentHide()
    }


    companion object {

        private const val ARG_TARGET_ID = "target-id"
        private const val ARG_COMMENT_TYPE = "comment-type"

        @JvmStatic
        fun newInstance(
            targetId: Long,
            targetType: CommentType,
            showCommentListener: ShowCommentListener? = null
        ) =
            CommentFragment(showCommentListener).apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_COMMENT_TYPE, targetType)
                    putLong(ARG_TARGET_ID, targetId)
                }
            }
    }

    interface ShowCommentListener {
        fun onCommentFragmentShow(
            targetId: Long,
            sendCommentActionListener: SendCommentActionListener?
        )

        fun onCommentFragmentHide()
    }

    @Parcelize
    enum class CommentType : Parcelable {
        VideoComment, TweetComment
    }
}

