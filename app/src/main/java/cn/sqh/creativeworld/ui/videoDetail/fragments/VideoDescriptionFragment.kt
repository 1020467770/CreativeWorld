package cn.sqh.creativeworld.ui.videoDetail.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.data.VideoId
import cn.sqh.creativeworld.databinding.FragmentVideoDescriptionBinding
import cn.sqh.creativeworld.ui.videoDetail.VideoDetailViewModel
import cn.sqh.creativeworld.ui.videoDetail.fragments.dummy.DummyContent
import com.blankj.utilcode.util.LogUtils


class VideoDescriptionFragment private constructor() : Fragment() {

    private var columnCount = 1

    private lateinit var mDataBinding: FragmentVideoDescriptionBinding

    //和VideoDetailFragment共享一个ViewModel
    private val videoViewModel: VideoDetailViewModel by activityViewModels()
    private val videoId: VideoId by lazy { requireArguments().getLong(ARG_VIDEO_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mDataBinding = FragmentVideoDescriptionBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
//            videoViewModel.setVideoId(videoId)
//            video = videoViewModel.video

            otherVideoList.apply {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = OtherVideoRecyclerViewAdapter(DummyContent.ITEMS)
            }
        }

        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDataBinding.run {
            viewModel = videoViewModel.apply {
                getVideoDetail(videoId).observe(viewLifecycleOwner) {}
            }
        }
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"
        const val ARG_VIDEO_ID = "video-id"

        @JvmStatic
        fun newInstance(columnCount: Int, videoId: VideoId) =
            VideoDescriptionFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_VIDEO_ID, videoId)
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}