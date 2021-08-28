package cn.sqh.creativeworld.ui.common

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import cn.sqh.creativeworld.R
import com.bumptech.glide.Glide
import com.google.android.material.transition.MaterialFadeThrough


class PictureDialogFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val path = arguments?.getString(ARG_PATH)
        val path = R.drawable.avatar_0//todo 用户设置的背景图

        val dialog = Dialog(requireActivity())
        dialog.setContentView(R.layout.fragment_picture_dialog)
        val imageView = dialog.findViewById(R.id.img_show) as ImageView
        Glide.with(requireContext())
            .load(path)
            .into(imageView)
        imageView.setOnClickListener {
            dialog.dismiss()
        }
        return dialog
    }

    companion object {

        private const val ARG_PATH = "path"

        fun newInstance(path: String): PictureDialogFragment {
            val args = Bundle().apply {
                putSerializable(ARG_PATH, path)
            }
            return PictureDialogFragment().apply {
                arguments = args
            }
        }
    }


}