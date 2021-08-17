package cn.sqh.creativeworld.ui.home

import android.view.View
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import cn.sqh.creativeworld.utils.spring

private const val FINAL_ALPHA = 1F
private const val FINAL_TRANSLATION_Y = 0F

class CustomItemAnimator : DefaultItemAnimator() {

    private val pendingAdds = mutableListOf<RecyclerView.ViewHolder>()

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        holder.itemView.alpha = 0f
        holder.itemView.translationY = holder.itemView.bottom / 3f
        pendingAdds.add(holder)
        return true
    }

    override fun runPendingAnimations() {
        super.runPendingAnimations()
        if (pendingAdds.isNotEmpty()) {
            for (i in pendingAdds.indices.reversed()) {
                val holder = pendingAdds[i]

                val tySpring = holder.itemView.spring(
                    SpringAnimation.TRANSLATION_Y,
                    stiffness = 350f,
                    damping = 0.6f
                )
                val aSpring = holder.itemView.spring(
                    SpringAnimation.ALPHA,
                    stiffness = 100f,
                    damping = SpringForce.DAMPING_RATIO_NO_BOUNCY
                )

                listenForAllSpringAnimationEnd(
                    { cancelled ->
                        if (!cancelled) {
                            //结束一个item的动画
                            dispatchAddFinished(holder)
                            dispatchFinishedWhenDone()
                        } else {
                            //动画播放过程中中途结束了，直接让动画完成（硬设值）
                            clearAnimatedValues(holder.itemView)
                        }
                    },
                    tySpring, aSpring
                )
                //开始某个holder的动画
                dispatchAddStarting(holder)
                //使用finalPosition的动画，让spring自己去创建结束点
                aSpring.animateToFinalPosition(FINAL_ALPHA)
                tySpring.animateToFinalPosition(FINAL_TRANSLATION_Y)
                pendingAdds.removeAt(i)
            }
        }
    }

    //重载recyclerView需要的方法，当recyclerView去掉某一个item时会调用
    override fun endAnimation(holder: RecyclerView.ViewHolder) {
        holder.itemView.spring(SpringAnimation.TRANSLATION_Y).cancel()
        holder.itemView.spring(SpringAnimation.ALPHA).cancel()
        if (pendingAdds.remove(holder)) {
            dispatchAddFinished(holder)
            clearAnimatedValues(holder.itemView)
        }
        super.endAnimation(holder)
    }

    //重载recyclerView需要的方法，当recyclerView清空所有item时会调用
    override fun endAnimations() {
        for (i in pendingAdds.indices.reversed()) {
            val holder = pendingAdds[i]
            clearAnimatedValues(holder.itemView)
            dispatchAddFinished(holder)
            pendingAdds.removeAt(i)
        }
        super.endAnimations()
    }

    override fun isRunning(): Boolean {
        return pendingAdds.isNotEmpty() || super.isRunning()
    }

    /**
     * 所有item的动画都结束了
     */
    private fun dispatchFinishedWhenDone() {
        if (!isRunning) {
            dispatchAnimationsFinished()
        }
    }

    private fun clearAnimatedValues(view: View) {
        view.alpha = FINAL_ALPHA
        view.translationY = FINAL_TRANSLATION_Y
    }

}

fun listenForAllSpringAnimationEnd(
    onEnd: (Boolean) -> Unit,
    vararg springs: SpringAnimation
) = MultiSpringAnimationEndListener(onEnd, *springs)

/**
 * 因为DynamicAnimation.OnAnimationEndListener的监听器list获取不到
 * 所以我们要自己创建一个类模拟这个list，才能维护cancel的值
 */
class MultiSpringAnimationEndListener(
    onEnd: (Boolean) -> Unit,
    vararg springs: SpringAnimation
) {
    private val listeners = ArrayList<DynamicAnimation.OnAnimationEndListener>(springs.size)

    private var wasCancelled = false

    init {
        springs.forEach {
            val listener = object : DynamicAnimation.OnAnimationEndListener {
                override fun onAnimationEnd(
                    animation: DynamicAnimation<out DynamicAnimation<*>>?,
                    canceled: Boolean,
                    value: Float,
                    velocity: Float
                ) {
                    animation?.removeEndListener(this)
                    wasCancelled = wasCancelled or canceled
                    listeners.remove(this)
                    if (listeners.isEmpty()) {
                        onEnd(wasCancelled)
                    }
                }
            }
            it.addEndListener(listener)
            listeners.add(listener)
        }
    }
}
