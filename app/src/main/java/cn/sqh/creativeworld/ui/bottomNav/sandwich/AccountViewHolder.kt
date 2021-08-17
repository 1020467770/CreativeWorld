package cn.sqh.creativeworld.ui.bottomNav.sandwich

import androidx.recyclerview.widget.RecyclerView
import cn.sqh.creativeworld.core.data.Account
import cn.sqh.creativeworld.databinding.ItemAccountLayoutBinding

class AccountViewHolder(
    val binding: ItemAccountLayoutBinding,
    val listener: AccountAdapter.AccountAdapterListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(account: Account) {
        binding.run {
            this.account = account
            accountListener = listener
            executePendingBindings()
        }
    }
}