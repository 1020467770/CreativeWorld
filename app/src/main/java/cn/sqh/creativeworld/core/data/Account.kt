package cn.sqh.creativeworld.core.data

import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.DiffUtil
import cn.sqh.creativeworld.R

/**
 * 同一个用户可以拥有多个账户
 */
data class Account(
    val id: Long,//区别同一用户的不同账户所用id
    val uid: Long,//服务器存储uid
    val username: String,//用户名
    val email: String,//邮箱
    @DrawableRes val profile: Int,//用户头像
    var isCurrentAccount: Boolean = false
) {
    @DrawableRes
    val checkedIcon: Int = if (isCurrentAccount) R.drawable.ic_done else 0
}

object AccountDiffCallback : DiffUtil.ItemCallback<Account>() {
    override fun areItemsTheSame(oldItem: Account, newItem: Account) =
        oldItem.username == newItem.username

    override fun areContentsTheSame(oldItem: Account, newItem: Account) =
        oldItem.email == newItem.email
                && oldItem.profile == newItem.profile
                && oldItem.isCurrentAccount == newItem.isCurrentAccount
}