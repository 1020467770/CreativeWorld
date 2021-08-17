package cn.sqh.creativeworld.utils

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import cn.sqh.creativeworld.R


/**
 * 设置textAppearance的工具类
 */
@Suppress("DEPRECATION")
fun TextView.setTextAppearanceCompat(context: Context, resId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        setTextAppearance(resId)
    } else {
        setTextAppearance(context, resId)
    }
}

/**
 * 给View设置spring动画通用方法
 */
fun View.spring(
    property: DynamicAnimation.ViewProperty,
    stiffness: Float = 200f,
    damping: Float = 0.3f,
    startVelocity: Float? = null
): SpringAnimation {
    val key = getKey(property)
    var springAnim = getTag(key) as? SpringAnimation?
    if (springAnim == null) {
        springAnim = SpringAnimation(this, property).apply {
            spring = SpringForce().apply {
                this.dampingRatio = damping
                this.stiffness = stiffness
                startVelocity?.let { setStartVelocity(it) }
            }
        }
        setTag(key, springAnim)
    }
    return springAnim
}

@IdRes
private fun getKey(property: DynamicAnimation.ViewProperty): Int {
    return when (property) {
        SpringAnimation.TRANSLATION_X -> R.id.sqh_translation_x
        SpringAnimation.TRANSLATION_Y -> R.id.sqh_translation_y
        SpringAnimation.TRANSLATION_Z -> R.id.sqh_translation_z
        SpringAnimation.SCALE_X -> R.id.sqh_scale_x
        SpringAnimation.SCALE_Y -> R.id.sqh_scale_y
        SpringAnimation.ROTATION -> R.id.sqh_rotation
        SpringAnimation.ROTATION_X -> R.id.sqh_rotation_x
        SpringAnimation.ROTATION_Y -> R.id.sqh_rotation_y
        SpringAnimation.X -> R.id.sqh_x
        SpringAnimation.Y -> R.id.sqh_y
        SpringAnimation.Z -> R.id.sqh_z
        SpringAnimation.ALPHA -> R.id.sqh_alpha
        SpringAnimation.SCROLL_X -> R.id.sqh_scroll_x
        SpringAnimation.SCROLL_Y -> R.id.sqh_scroll_y
        else -> throw IllegalAccessException("Unknown ViewProperty: $property")
    }
}