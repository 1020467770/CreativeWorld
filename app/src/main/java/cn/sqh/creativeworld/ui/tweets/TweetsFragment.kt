package cn.sqh.creativeworld.ui.tweets

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.ConcatAdapter
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.data.TweetId
import cn.sqh.creativeworld.databinding.FragmentTweetsBinding
import cn.sqh.creativeworld.ui.common.pagingFooter.FooterAdapter
import cn.sqh.creativeworld.ui.home.HomeFragmentDirections
import cn.sqh.creativeworld.ui.tweets.dummy.DummyContent
import cn.sqh.creativeworld.utils.contentView
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TweetsFragment : Fragment(), TweetRecyclerViewAdapter.TweetViewClickListener {

    private lateinit var mBinding: FragmentTweetsBinding
    private val tweetsViewModel: TweetsPreviewViewModel by viewModels()

    private val headerAdapter = TweetHeaderAdapter()
    private val tweetAdapter = TweetRecyclerViewAdapter(this)
    private val concatAdapter = ConcatAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
        }
        concatAdapter.addAdapter(headerAdapter)
        concatAdapter.addAdapter(
            tweetAdapter.withLoadStateHeaderAndFooter(
                FooterAdapter(tweetAdapter),
                FooterAdapter(tweetAdapter)
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        LogUtils.d(tweetsViewModel)
        mBinding = FragmentTweetsBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        lifecycleScope.launchWhenCreated {
            tweetsViewModel.tweetsFlow.collectLatest {
                tweetAdapter.submitData(it)
            }
        }
        mBinding.run {
            tweetsRecyclerView.apply {
                /*adapter = tweetAdapter
                    .withLoadStateHeaderAndFooter(
                        FooterAdapter(tweetAdapter),
                        FooterAdapter(tweetAdapter)
                    )*/
                adapter = concatAdapter
            }
        }
    }

    override fun onClickTweet(view: View, tweetId: TweetId) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
        }
        val tweetDetailTransitionName = getString(R.string.tweet_card_detail_transition_name)
        val extras = FragmentNavigatorExtras(view to tweetDetailTransitionName)
//                    LogUtils.d("videoId=$videoId")
        val directions = TweetsFragmentDirections.actionTweetsToTweetDetail(tweetId)
        view.findNavController().navigate(directions, extras)
    }
}