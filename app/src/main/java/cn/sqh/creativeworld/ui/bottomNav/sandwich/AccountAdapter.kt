package cn.sqh.creativeworld.ui.bottomNav.sandwich

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import cn.sqh.creativeworld.core.data.Account
import cn.sqh.creativeworld.core.data.AccountDiffCallback
import cn.sqh.creativeworld.databinding.ItemAccountLayoutBinding

class AccountAdapter(
    private val listener: AccountAdapterListener
) : ListAdapter<Account, AccountViewHolder>(AccountDiffCallback) {

    interface AccountAdapterListener {
        fun onAccountClicked(account: Account)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        return AccountViewHolder(
            ItemAccountLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            listener
        )
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}