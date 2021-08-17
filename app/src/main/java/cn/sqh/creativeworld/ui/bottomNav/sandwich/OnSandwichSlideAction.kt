package cn.sqh.creativeworld.ui.bottomNav.sandwich

import android.view.View
import androidx.annotation.FloatRange
import cn.sqh.creativeworld.utils.normalize

/**
 * 夹层滑动时的事件接口
 */
interface OnSandwichSlideAction {
    fun onSlide(
        @FloatRange(
            from = 0.0,
            fromInclusive = true,
            to = 1.0,
            toInclusive = true
        ) slideOffset: Float
    )
}

/**
 * 当打开夹层时把某个View向上旋转半圈（这里指navigation的角标）
 */
class HalfCounterClockwiseRotateSlideAction(
    private val view: View
) : OnSandwichSlideAction {
    override fun onSlide(slideOffset: Float) {
        view.rotation = slideOffset.normalize(
            0F,
            1F,
            180F,
            0F
        )
    }
}