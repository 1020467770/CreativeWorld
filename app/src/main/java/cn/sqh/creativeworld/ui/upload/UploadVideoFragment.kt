package cn.sqh.creativeworld.ui.upload

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.databinding.FragmentUploadViedoBinding
import cn.sqh.creativeworld.utils.themeColor
import com.blankj.utilcode.util.LogUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.transition.MaterialContainerTransform
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File

@AndroidEntryPoint
class UploadVideoFragment : Fragment(), ActionUploadListener, ChooseVideoListener {


    private val mViewModel: UploadVideoViewModel by viewModels()

    private lateinit var mBinding: FragmentUploadViedoBinding
//    private val mUploadingVideo = UploadingVideo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentUploadViedoBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            selectVideoHandler = this@UploadVideoFragment
            uploadedHandler = this@UploadVideoFragment
            viewModel = mViewModel

//            uploadingVideoPreviewImg.setOnClickListener {
            /*PictureSelector.create(this@UploadVideoFragment)
                .openGallery(PictureMimeType.ofVideo())
                .isPreviewVideo(true)
                .isCompress(true)//????????????
                .maxSelectNum(1)//?????????????????????????????????????????????????????????????????????
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        val localVideo = result?.get(0)
                        val path = localVideo?.path
                        mViewModel.setCoverPath(path)
                        mViewModel.apply {
                            setVideoFile(localVideo)
                            setCoverFile(path)
                        }

                    }

                    override fun onCancel() {
//                         TODO("Not yet implemented")
                    }
                })*/
//            }
            tvChangeCoverFile.setOnClickListener {
                PictureSelector.create(this@UploadVideoFragment)
                    .openGallery(PictureMimeType.ofImage())
                    .isPreviewImage(true)
                    .isCompress(true)//????????????
                    .maxSelectNum(1)//?????????????????????
                    .forResult(object : OnResultCallbackListener<LocalMedia> {
                        override fun onResult(result: MutableList<LocalMedia>?) {
                            val imgFile = result?.get(0)
                            imgFile?.let { file ->
                                mViewModel.setCoverPath(file.path)
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {//???IO????????????????????????
                                        val localFile = File(file.realPath)
                                        mViewModel.uploadingVideo.coverFile = localFile.readBytes()
                                    }
                                }

                            }
                        }

                        override fun onCancel() {
//                            TODO("Not yet implemented")
                        }
                    })
            }
        }
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.run {
            enterTransition = MaterialContainerTransform().apply {
                startView = requireActivity().findViewById(R.id.fab)
                endView = uploadVideoCard
                duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
                scrimColor = Color.TRANSPARENT
                containerColor = requireContext().themeColor(R.attr.colorSurface)
                startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
                endContainerColor = requireContext().themeColor(R.attr.colorSurface)
            }
            returnTransition = Slide().apply {
                duration = resources.getInteger(R.integer.sqh_motion_duration_medium).toLong()
                //??????CardView????????????????????????????????????
                addTarget(R.id.upload_video_card)
            }
        }
    }


    private fun setCoverFile(path: String?) {
        var result: ByteArray? = null
        if (!path.isNullOrEmpty()) {
            val bos = ByteArrayOutputStream()
            Glide.with(requireContext())
                .asBitmap()
                .load(path)
                .into(object : CustomTarget<Bitmap>(
                    Target.SIZE_ORIGINAL,
                    Target.SIZE_ORIGINAL
                ) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                        result = bos.toByteArray()
//                        LogUtils.d("??????result=$result")
                        mViewModel.uploadingVideo.coverFile = result
                        bos.close()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        bos.close()
                    }
                })
        }
    }

    @InternalCoroutinesApi
    override fun onActionUploadVideo() {
        mBinding.apply {
            btnSend.isEnabled = false
            loading.visibility = View.VISIBLE
        }
        mViewModel.uploadVideo() {
            mBinding.loading.visibility = View.GONE
            findNavController().navigateUp()
        }
    }

    override fun onActionChooseVideo() {
        PictureSelector.create(this@UploadVideoFragment)
            .openGallery(PictureMimeType.ofVideo())
            .isPreviewVideo(true)
            .isCompress(true)//????????????
            .maxSelectNum(1)//?????????????????????????????????????????????????????????????????????
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: MutableList<LocalMedia>?) {
                    val localVideo = result?.get(0)
                    val path = localVideo?.path
//                    val realPath = localVideo?.realPath
                    LogUtils.d("path=${path}")
                    LogUtils.d("realPath=${localVideo?.realPath}")
                    mViewModel.setCoverPath(path)
                    mViewModel.apply {
                        setVideoFile(localVideo)
                        setCoverFile(path)
                    }

                }

                override fun onCancel() {
//                         TODO("Not yet implemented")
                }
            })
    }
}

interface ChooseVideoListener {
    fun onActionChooseVideo()
}

interface ActionUploadListener {
    fun onActionUploadVideo()
}