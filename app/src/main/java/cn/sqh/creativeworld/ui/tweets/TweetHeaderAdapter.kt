package cn.sqh.creativeworld.ui.tweets

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import cn.sqh.creativeworld.CreativeWorldApplication
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.DataBoundViewHolder
import cn.sqh.creativeworld.databinding.HeaderTweetsLayoutBinding
import com.zhpan.bannerview.constants.PageStyle
import com.zhpan.indicator.enums.IndicatorSlideMode

class TweetHeaderAdapter() :
    RecyclerView.Adapter<DataBoundViewHolder<HeaderTweetsLayoutBinding>>() {

    val pics: MutableList<Int> = ArrayList()
    private val context = CreativeWorldApplication.context

    init {
        for (i in 0..3) {
            val drawable =
                context.resources.getIdentifier("banner_pic_$i", "drawable", context.packageName)
            pics.add(drawable)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DataBoundViewHolder<HeaderTweetsLayoutBinding> {
        return DataBoundViewHolder(
            HeaderTweetsLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).apply {
                bannerView
                    .setIndicatorSlideMode(IndicatorSlideMode.SCALE)
                    .setIndicatorSliderColor(
                        context.getColor(R.color.sqh_red_200),
                        context.getColor(R.color.sqh_red_400)
                    )
                    .setIndicatorSliderRadius(
                        context.getResources().getDimensionPixelOffset(R.dimen.grid_0_25),
                        context.getResources().getDimensionPixelOffset(R.dimen.grid_0_5)
                    )
                    .setOnPageClickListener { _, position ->
                        if (position != bannerView.getCurrentItem()) {
                            bannerView.setCurrentItem(position, true);
                        }
                    }
                    .setAdapter(HeaderBannerAdapter())
                    .setInterval(5000)
                    .setPageMargin(context.resources.getDimensionPixelOffset(R.dimen.banner_pic_page_margin))
                    .setRevealWidth(
                        context.resources.getDimensionPixelOffset(R.dimen.banner_pic_reveal_width_center)
                    )
                    .setPageStyle(PageStyle.MULTI_PAGE_OVERLAP)
                    .create(pics)
            }
        )
    }

    override fun onBindViewHolder(
        holder: DataBoundViewHolder<HeaderTweetsLayoutBinding>,
        position: Int
    ) {
        holder.binding.run {
            executePendingBindings()
        }
    }

    override fun getItemCount() = 1

}