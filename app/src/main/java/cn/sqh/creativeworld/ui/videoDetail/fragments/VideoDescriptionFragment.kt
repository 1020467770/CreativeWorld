package cn.sqh.creativeworld.ui.videoDetail.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.databinding.FragmentVideoDescriptionBinding
import cn.sqh.creativeworld.ui.videoDetail.fragments.dummy.DummyContent
import com.blankj.utilcode.util.LogUtils

/**
 * A fragment representing a list of Items.
 */
class VideoDescriptionFragment : Fragment() {

    private var columnCount = 1

    private lateinit var mDataBinding: FragmentVideoDescriptionBinding

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

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            VideoDescriptionFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}