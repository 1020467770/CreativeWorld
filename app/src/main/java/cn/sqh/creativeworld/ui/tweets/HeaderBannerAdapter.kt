package cn.sqh.creativeworld.ui.tweets

import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.databinding.DataBindingUtil
import cn.sqh.creativeworld.CreativeWorldApplication
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.databinding.ItemBannerImgBinding
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder

class HeaderBannerAdapter : BaseBannerAdapter<Int>() {
    override fun bindData(
        holder: BaseViewHolder<Int>,
        @DrawableRes data: Int,
        position: Int,
        pageSize: Int
    ) {
        val binding = DataBindingUtil.bind<ItemBannerImgBinding>(holder.itemView)
        binding?.imgBanner?.setImageResource(data)
    }

    override fun getLayoutId(viewType: Int) = R.layout.item_banner_img
}