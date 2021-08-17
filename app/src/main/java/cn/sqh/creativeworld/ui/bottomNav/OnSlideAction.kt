package cn.sqh.creativeworld.ui.bottomNav

import android.view.View
import android.widget.ImageView
import androidx.annotation.FloatRange
import androidx.core.view.marginTop
import androidx.core.view.updatePadding
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.utils.normalize
import com.google.android.material.shape.MaterialShapeDrawable

interface OnSlideAction {

    /**
     * 因为这个滑动的接口和BottomSheetCallback需要的值相关，
     * 只能在-1~1之间，所以使用@FloatRange限定，可以在编译时发现
     */
    fun onSlide(
        sheet: View,
        @FloatRange(
            from = -1.0,
            fromInclusive = true,
            to = 1.0,
            toInclusive = true
        ) slideOffset: Float
    )
}

/**
 * AppBar上的角标转半圈
 */
class HalfClockwiseRotateSlideAction(
    private val view: View
) : OnSlideAction {

    override fun onSlide(sheet: View, slideOffset: Float) {
        //因为CoordinatorLayout的BottomSheet的滑动值可能不能正好到inputMax的值，所以用插值限定，下面同理
        view.rotation = slideOffset.normalize(
            -1F,
            0F,
            0F,
            180F
        )
    }
}

/**
 * 前景在滑动时发生的动画效果
 * 包括圆角化和头像出现
 */
class ForegroundSheetTransformSlideAction(
    private val foregroundView: View,
    private val foregroundShapeDrawable: MaterialShapeDrawable,
    private val profileImageView: ImageView
) : OnSlideAction {

    private val foregroundMarginTop = foregroundView.marginTop
    private var systemTopInset: Int = 0
    private val foregroundZ = foregroundView.z
    private val profileImageOriginalZ = profileImageView.z

    private fun getPaddingTop(): Int {
        if (systemTopInset == 0) {
            //获取之前存的系统工具栏顶部高度
            systemTopInset = foregroundView.getTag(R.id.tag_system_window_inset_top) as? Int? ?: 0
        }
        return systemTopInset
    }

    /**
     * 参考了其他drawer的计算方法
     */
    override fun onSlide(sheet: View, slideOffset: Float) {
        val progress = slideOffset.normalize(0F, 0.25F, 1F, 0F)
        /**
         * 头像大小的变化
         */
        profileImageView.scaleX = progress
        profileImageView.scaleY = progress
        foregroundShapeDrawable.interpolation = progress

        foregroundView.translationY = -(1 - progress) * foregroundMarginTop
        val topPaddingProgress = slideOffset.normalize(0F, 0.9F, 0F, 1F)
        foregroundView.updatePadding(top = (getPaddingTop() * topPaddingProgress).toInt())

        if (slideOffset > 0 && foregroundZ <= profileImageView.z) {
            profileImageView.z = profileImageOriginalZ
        } else if (slideOffset <= 0 && foregroundZ >= profileImageView.z) {
            profileImageView.z = foregroundZ + 1
        }
    }
}

/**
 * 透明度随着滑动值的变化
 */
class AlphaSlideAction(
    private val view: View,
    private val reverse: Boolean = false
) : OnSlideAction {

    override fun onSlide(sheet: View, slideOffset: Float) {
        val alpha = slideOffset.normalize(-1F, 0F, 0F, 1F)
        view.alpha = if (!reverse) alpha else 1F - alpha
    }
}
