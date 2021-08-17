package cn.sqh.creativeworld.ui.bottomNav

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import cn.sqh.creativeworld.databinding.ItemDividerNavMenuLayoutBinding
import cn.sqh.creativeworld.databinding.ItemNavFavoriteFolderLayoutBinding
import cn.sqh.creativeworld.databinding.ItemNavMenuLayoutBinding

private const val VIEW_TYPE_NAV_MENU_ITEM = 4
private const val VIEW_TYPE_NAV_DIVIDER = 5
private const val VIEW_TYPE_NAV_FAVORITE_FOLDER_ITEM = 6

class NavigationAdapter(
    private val listener: NavigationAdapterListener
) : ListAdapter<NavigationModelItem, NavigationViewHolder<NavigationModelItem>>(
    NavigationModelItem.NavModelItemDiff
) {


    interface NavigationAdapterListener {
        fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem)
        fun onNavFavoriteItemClicked(item: NavigationModelItem.NavFavoriteFolder)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is NavigationModelItem.NavMenuItem -> VIEW_TYPE_NAV_MENU_ITEM
            is NavigationModelItem.NavDivider -> VIEW_TYPE_NAV_DIVIDER
            is NavigationModelItem.NavFavoriteFolder -> VIEW_TYPE_NAV_FAVORITE_FOLDER_ITEM
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NavigationViewHolder<NavigationModelItem> {
        return when (viewType) {
            VIEW_TYPE_NAV_MENU_ITEM -> NavigationViewHolder.NavMenuItemViewHolder(
                ItemNavMenuLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            VIEW_TYPE_NAV_DIVIDER -> NavigationViewHolder.NavDividerViewHolder(
                ItemDividerNavMenuLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            VIEW_TYPE_NAV_FAVORITE_FOLDER_ITEM -> NavigationViewHolder.NavFavoriteFolderViewHolder(
                ItemNavFavoriteFolderLayoutBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                ),
                listener
            )
            else -> throw RuntimeException("Unsupported view holder type")
        } as NavigationViewHolder<NavigationModelItem>
    }

    override fun onBindViewHolder(
        holder: NavigationViewHolder<NavigationModelItem>,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

}