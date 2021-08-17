package cn.sqh.creativeworld.ui.bottomNav

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import cn.sqh.creativeworld.utils.normalize
import com.google.android.material.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlin.math.max

//实现CoordinatorLayout中的BottomSheet的Behavior接口，就能在CoordinatorLayout实现升降的效果
class BottomNavigationDrawerCallback : BottomSheetBehavior.BottomSheetCallback() {

    /**
     * 使用多监听器的设计模式在滑动和状态改变时回调监听函数
     */
    private val onSlideActions: MutableList<OnSlideAction> = mutableListOf()
    private val onStateChangedActions: MutableList<OnStateChangedAction> = mutableListOf()

    private var lastSlideOffset = -1.0F
    private var halfExpandedSlideOffset = Float.MAX_VALUE

    override fun onStateChanged(bottomSheet: View, newState: Int) {
//        Log.d("TAG", "onStateChanged: 状态改变了了")
        if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED) {
            halfExpandedSlideOffset = lastSlideOffset
//            Log.d("TAG", "onStateChanged: newState=${newState}")
            onSlide(bottomSheet, lastSlideOffset)//多执行一次滑动函数
        }

        //执行所有回调函数
        onStateChangedActions.forEach { it.onStateChanged(bottomSheet, newState) }
    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
//        Log.d("TAG", "onSlide: 滑动了")

        if (halfExpandedSlideOffset == Float.MAX_VALUE)
            calculateInitialHalfExpandedSlideOffset(bottomSheet)
//        Log.d("TAG", "onSlide: 现在的slideOffset=${slideOffset}")

        lastSlideOffset = slideOffset
        val trueOffset = if (slideOffset <= halfExpandedSlideOffset) {
            //也是怕偏差
            slideOffset.normalize(-1F, halfExpandedSlideOffset, -1F, 0F)
        } else {
            slideOffset.normalize(halfExpandedSlideOffset, 1F, 0F, 1F)
        }
//        Log.d("TAG", "onSlide: 现在的trueOffset=${trueOffset}")

        //执行所有回调函数
        onSlideActions.forEach { it.onSlide(bottomSheet, trueOffset) }
    }

    /**
     * 计算第一次展开需要展开多少
     */
    private fun calculateInitialHalfExpandedSlideOffset(sheet: View) {
        val parent = sheet.parent as CoordinatorLayout
        val behavior = BottomSheetBehavior.from(sheet)

        val halfExpandedOffset = parent.height * (1 - behavior.halfExpandedRatio)
        val peekHeightMin = parent.resources.getDimensionPixelSize(
            R.dimen.design_bottom_sheet_peek_height_min
        )
        val peek = max(peekHeightMin, parent.height - parent.width * 9 / 16)
        val collapsedOffset = max(
            parent.height - peek,
            max(0, parent.height - sheet.height)
        )
        halfExpandedSlideOffset =
            (collapsedOffset - halfExpandedOffset) / (parent.height - collapsedOffset)
        /*Log.d(
            "TAG",
            "parent.height=${parent.height}," +
                    "behavior.halfExpandedRatio=${behavior.halfExpandedRatio}," +
                    "collapsedOffset=${collapsedOffset}," +
                    "halfExpandedOffset=${halfExpandedOffset}," +
                    "halfExpandedSlideOffset=${halfExpandedSlideOffset}"
        )*/
    }

    fun addOnSlideAction(action: OnSlideAction): Boolean {
        return onSlideActions.add(action)
    }

    fun removeOnSlideAction(action: OnSlideAction): Boolean {
        return onSlideActions.remove(action)
    }

    fun addOnStateChangedAction(action: OnStateChangedAction): Boolean {
        return onStateChangedActions.add(action)
    }

    fun removeOnStateChangedAction(action: OnStateChangedAction): Boolean {
        return onStateChangedActions.remove(action)
    }
}