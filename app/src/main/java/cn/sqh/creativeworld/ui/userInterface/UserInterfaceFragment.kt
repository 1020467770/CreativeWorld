package cn.sqh.creativeworld.ui.userInterface

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.databinding.FragmentUserInterfaceBinding
import cn.sqh.creativeworld.ui.common.PictureDialogFragment
import com.google.android.material.transition.MaterialFadeThrough

class UserInterfaceFragment : Fragment() {


    private lateinit var viewModel: UserInterfaceViewModel
    private lateinit var mBinding: FragmentUserInterfaceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
        }
        returnTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentUserInterfaceBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            toolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            userBackground.setOnClickListener {
                val pictureDialogFragment = PictureDialogFragment.newInstance("用户背景图路径url")
                pictureDialogFragment.show(requireActivity().supportFragmentManager, "a")
            }
        }
        return mBinding.root
    }


}