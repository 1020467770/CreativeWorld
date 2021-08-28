package cn.sqh.creativeworld.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView


class RoundCornerImageView(context: Context, attrs: AttributeSet?) :
    AppCompatImageView(context, attrs) {
    private var width = 0f
    private var height: Float = 0f

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        width = getWidth().toFloat()
        height = getHeight().toFloat()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        /**
         * 通过裁减画布实现圆角效果，还没把圆角值参数化
         */
        if (width > 12 && height > 12) {
            val path = Path().apply {
                moveTo(12F, 0F)
                lineTo(width - 12, 0F)
                quadTo(width, 0F, width, 12F)
                lineTo(width, height - 12)
                quadTo(width, height, width - 12, height)
                lineTo(12F, height)
                quadTo(0F, height, 0F, height - 12)
                lineTo(0F, 12F)
                quadTo(0F, 0F, 12F, 0F)
            }
            canvas.clipPath(path)
        }
        super.onDraw(canvas)
    }
}