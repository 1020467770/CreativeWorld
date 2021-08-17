package cn.sqh.creativeworld.ui.bottomNav

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cn.sqh.creativeworld.databinding.ItemDividerNavMenuLayoutBinding
import cn.sqh.creativeworld.databinding.ItemNavFavoriteFolderLayoutBinding
import cn.sqh.creativeworld.databinding.ItemNavMenuLayoutBinding

sealed class NavigationViewHolder<T : NavigationModelItem>(view: View) :
    RecyclerView.ViewHolder(view) {

    abstract fun bind(navItem: T)

    class NavMenuItemViewHolder(
        private val binding: ItemNavMenuLayoutBinding,
        private val listener: NavigationAdapter.NavigationAdapterListener
    ) : NavigationViewHolder<NavigationModelItem.NavMenuItem>(binding.root) {
        override fun bind(navItem: NavigationModelItem.NavMenuItem) {
            binding.run {
                navMenuItem = navItem
                navListener = listener
                executePendingBindings()
            }
        }
    }

    class NavDividerViewHolder(
        private val binding: ItemDividerNavMenuLayoutBinding
    ) : NavigationViewHolder<NavigationModelItem.NavDivider>(binding.root) {
        override fun bind(navItem: NavigationModelItem.NavDivider) {
            binding.navDivider = navItem
            binding.executePendingBindings()
        }
    }

    class NavFavoriteFolderViewHolder(
        private val binding: ItemNavFavoriteFolderLayoutBinding,
        private val listener: NavigationAdapter.NavigationAdapterListener
    ) : NavigationViewHolder<NavigationModelItem.NavFavoriteFolder>(binding.root) {
        override fun bind(navItem: NavigationModelItem.NavFavoriteFolder) {
            binding.run {
                navFavoriteFolder = navItem
                navListener = listener
                executePendingBindings()
            }
        }
    }


}