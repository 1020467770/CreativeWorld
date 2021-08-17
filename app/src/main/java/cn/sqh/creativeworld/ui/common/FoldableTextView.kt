package cn.sqh.creativeworld.ui.common

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.LayoutInflaterCompat
import androidx.databinding.DataBindingUtil
import cn.sqh.creativeworld.R
import com.blankj.utilcode.util.LogUtils


@SuppressLint("InflateParams", "Recycle")
class FoldableTextView(context: Context, attrs: AttributeSet) :
    LinearLayout(context, attrs), View.OnClickListener {

    private lateinit var mFoldableTextViewHolder: FoldableTextViewHolder

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val lines = msg.obj as Int
            when (msg.what) {
                STATE_TEXT_OPEN -> mFoldableTextViewHolder.tvFold.setMaxLines(lines)
                STATE_TEXT_CLOSE -> mFoldableTextViewHolder.tvFold.setMaxLines(lines)
            }
        }
    }

    private val text: String?

    private var isFirstPaint = true

    private var defaultMaxLineCounts = 3

    private var realLineCounts = 0

    private var realHeight = 0

    private var foldHeight = 0

    private var isExpand = false

    /**
     * Icon状态 展开动画
     */
    private val expandIconAnimation: RotateAnimation

    /**
     * Icon状态 折叠动画
     */
    private val foldIconAnimation: RotateAnimation

    init {
        LayoutInflater.from(context).inflate(R.layout.view_foldable_textview, this)
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.FoldableTextView)
        text = typedArray.getString(R.styleable.FoldableTextView_ftv_text)
        defaultMaxLineCounts =
            typedArray.getInt(R.styleable.FoldableTextView_defaultMaxLineCounts, 3)

        //设置小箭头展开时的旋转动画
        expandIconAnimation = RotateAnimation(
            0f,
            180f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
            fillAfter = true
        }


        //设置小箭头关闭时的旋转动画
        foldIconAnimation = RotateAnimation(
            180f,
            0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
            fillAfter = true
        }
        mFoldableTextViewHolder = FoldableTextViewHolder(this)

        setText(text)

    }

    private fun setText(text: String?) {
        mFoldableTextViewHolder.tvFold.text = text
        val vto = mFoldableTextViewHolder.tvFold.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mFoldableTextViewHolder.tvFold.viewTreeObserver.removeOnGlobalLayoutListener(this)

                //获取真实的行数和高度
                realLineCounts = mFoldableTextViewHolder.tvFold.lineCount
                realHeight = mFoldableTextViewHolder.tvFold.measuredHeight

                LogUtils.d("realLineCounts=$realLineCounts")
                LogUtils.d("realHeight=$realHeight")

                //如果真实行数大于默认的最大显示行数，则进行折叠  isExpand设为false
                if (realLineCounts > defaultMaxLineCounts) {
                    mFoldableTextViewHolder.tvFold.maxLines = defaultMaxLineCounts
                    mFoldableTextViewHolder.tvFold.measure(0, 0)
                    foldHeight = mFoldableTextViewHolder.tvFold.measuredHeight
                    mFoldableTextViewHolder.imgFold.visibility = View.VISIBLE

                    mFoldableTextViewHolder.tvFold.setOnClickListener(this@FoldableTextView)
                    mFoldableTextViewHolder.imgFold.setOnClickListener(this@FoldableTextView)
                    isExpand = false
                } else {
                    mFoldableTextViewHolder.imgFold.visibility = View.GONE
                    isExpand = true
                }
            }
        })
    }

    private inner class FoldableTextViewHolder(
        view: View
    ) {
        var tvFold: TextView
        var imgFold: ImageView

        init {
            tvFold = view.findViewById(R.id.tv_fold)
            imgFold = view.findViewById(R.id.iv_fold)
        }

    }

    override fun onClick(v: View?) {
        if (isExpand) {
            mFoldableTextViewHolder.imgFold.animation = foldIconAnimation
            foldIconAnimation.startNow()

            Thread {
                var endcount = realLineCounts
                while (endcount-- > defaultMaxLineCounts) {
                    val msg = Message.obtain()
                    msg.what = STATE_TEXT_CLOSE
                    msg.obj = endcount
                    handler.sendMessage(msg)
                    Thread.sleep(20)
                }
            }.start()


            isExpand = false
        } else {
            mFoldableTextViewHolder.imgFold.animation = expandIconAnimation
            expandIconAnimation.startNow()

            Thread {
                var startcount = defaultMaxLineCounts
                while (startcount++ < realLineCounts) {
                    val msg = Message.obtain()
                    msg.what = STATE_TEXT_OPEN;
                    msg.obj = startcount;
                    handler.sendMessage(msg);
                    Thread.sleep(20)
                }
            }.start()

            isExpand = true
        }
    }

    companion object {

        private const val STATE_TEXT_OPEN = 1

        private const val STATE_TEXT_CLOSE = 2
    }

}