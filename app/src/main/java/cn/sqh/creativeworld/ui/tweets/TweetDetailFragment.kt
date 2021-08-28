package cn.sqh.creativeworld.ui.tweets

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.databinding.FragmentTweetDetailBinding
import cn.sqh.creativeworld.ui.videoDetail.VideoDetailFragmentArgs
import cn.sqh.creativeworld.utils.themeColor
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class TweetDetailFragment : Fragment() {

    private val args: TweetDetailFragmentArgs by navArgs()
    private val tweetId: Long by lazy(LazyThreadSafetyMode.NONE) { args.tweetId }

    private val mViewModel: TweetDetailViewModel by viewModels()
    private lateinit var mBinding: FragmentTweetDetailBinding

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentTweetDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            viewModel = mViewModel.apply {
                getTweetDetail(tweetId).observe(viewLifecycleOwner) {

                }
            }
        }
        mViewModel.failure.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        })
    }


}