package cn.sqh.creativeworld.ui.email

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.transition.Slide
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.databinding.FragmentComposeBinding
import com.google.android.material.transition.MaterialContainerTransform

class ComposeFragment : Fragment() {

    private lateinit var mDataBinding: FragmentComposeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_compose, container, false)
        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mDataBinding.run {
            enterTransition = MaterialContainerTransform().apply {
                // Manually add the Views to be shared since this is not a standard Fragment to
                // Fragment shared element transition.
                startView = requireActivity().findViewById(R.id.fab)
                endView = emailCardView
                duration = 300.toLong()
//                    scrimColor = Color.TRANSPARENT
                /*containerColor = requireContext().themeColor(R.attr.colorSurface)
                startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
                endContainerColor = requireContext().themeColor(R.attr.colorSurface)*/
            }
            returnTransition = Slide().apply {
                duration = 225.toLong()
                addTarget(R.id.email_card_view)
            }

        }
    }
}