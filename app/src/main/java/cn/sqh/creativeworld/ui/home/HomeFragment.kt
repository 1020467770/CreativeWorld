package cn.sqh.creativeworld.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.doOnNextLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.data.VideoId
//import cn.sqh.creativeworld.core.data.videos
import cn.sqh.creativeworld.databinding.FragmentHomeBinding
import cn.sqh.creativeworld.ui.bottomNav.NavigationStore
import cn.sqh.creativeworld.ui.common.MenuBottomSheetDialogFragment
import cn.sqh.creativeworld.ui.common.pagingFooter.FooterAdapter
import cn.sqh.creativeworld.ui.home.data.VideoPreviewModel
import com.blankj.utilcode.util.LogUtils
import com.google.android.material.navigation.NavigationView
import com.google.android.material.transition.MaterialElevationScale
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import java.util.concurrent.TimeUnit

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HomeFragment : Fragment(), VideoPreviewAdapter2.VideoViewClickListener {

    private lateinit var mDataBinding: FragmentHomeBinding

    private val homeViewModel: HomeViewModel2 by viewModels()

    private val onBackCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            NavigationStore.setNavigationMenuItemChecked(NavigationStore.HOME_PAGE)
        }
    }

    private val previewAdapter = VideoPreviewAdapter2(this@HomeFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        LogUtils.d("onCreateView....,此时的ViewModel=${homeViewModel}")
//        homeViewModel.getVideos()
        mDataBinding = FragmentHomeBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            homeRecyclerView.apply {
                itemAnimator = CustomItemAnimator()
                adapter = previewAdapter.apply {
                    /*homeViewModel.getVideos().observe(viewLifecycleOwner) {
                        this.submitData(lifecycle, it)
//                        startPostponedEnterTransition()
                    }*/
                    /*homeViewModel.videos.observe(viewLifecycleOwner) { result ->
                        when (result.status) {
                            Resource.Status.SUCCESS -> {
                                result.data?.let {
                                    val tmpList = currentList.toMutableList()
                                    tmpList.addAll(it)
                                    submitList(tmpList)
                                }
                                startPostponedEnterTransition()
                            }
                        }
                    }*/
                    doOnNextLayout {
                        startPostponedEnterTransition()
                    }
                }.withLoadStateFooter(FooterAdapter(previewAdapter))

            }
        }
        postponeEnterTransition(1000L, TimeUnit.MILLISECONDS)
        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackCallback
        )
        lifecycleScope.launchWhenCreated {
            homeViewModel.videosFlow.collectLatest {
                previewAdapter.submitData(it)
            }
        }
        /*mDataBinding.homeRecyclerView.apply {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = recyclerView.layoutManager as StaggeredGridLayoutManager
                    val (leftLastPosition, rightLastPosition) = layoutManager.findLastVisibleItemPositions(
                        intArrayOf(0, 0)
                    )
                    if (maxOf(leftLastPosition, rightLastPosition)
                        == this@apply.adapter?.itemCount!! - 1
                    ) {
                        homeViewModel.getNextPageVideos()
//                        ToastUtils.showShort("到底部了！")
                    }
                }

            })
        }*/

//        postponeEnterTransition()
//        view.doOnPreDraw { startPostponedEnterTransition() }
    }

    override fun onDetach() {
        super.onDetach()
        LogUtils.d("onDetach....")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LogUtils.d("onDestroyView....")
    }

    override fun onClick(view: View, videoId: VideoId) {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
        }
        val videoDetailTransitionName = getString(R.string.video_detail_transition_name)
        val extras = FragmentNavigatorExtras(view to videoDetailTransitionName)
//                    LogUtils.d("videoId=$videoId")
        val directions = HomeFragmentDirections.actionHomeToVideoDetail(videoId)
//        VideoDetailActivity.start(requireActivity(), videoId)
        view.findNavController().navigate(directions, extras)
    }

    override fun onLongPressed(video: VideoPreviewModel): Boolean {
        MenuBottomSheetDialogFragment
            .newInstance(R.menu.sheet_menu_bottom_navigation).apply {
                navigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener {
                    Toast.makeText(requireContext(), "点击了", Toast.LENGTH_SHORT).show()
                    dismiss()
                    true
                }
            }
            .show(parentFragmentManager, null)
        return true
    }
}