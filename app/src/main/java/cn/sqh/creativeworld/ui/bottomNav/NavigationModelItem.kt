package cn.sqh.creativeworld.ui.bottomNav

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DiffUtil
import cn.sqh.creativeworld.core.data.FavoriteFolder
import cn.sqh.creativeworld.core.data.FavoriteFolderDiff

sealed class NavigationModelItem {

    data class NavMenuItem(
        val id: Int,
        @DrawableRes val icon: Int,
        @StringRes val titleRes: Int,
        var checked: Boolean
    ) : NavigationModelItem()

    data class NavDivider(val title: String) : NavigationModelItem()

    data class NavFavoriteFolder(val favoriteFolder: FavoriteFolder) : NavigationModelItem()

    object NavModelItemDiff : DiffUtil.ItemCallback<NavigationModelItem>() {
        override fun areItemsTheSame(
            oldItem: NavigationModelItem,
            newItem: NavigationModelItem
        ): Boolean {
            return when {
                oldItem is NavMenuItem && newItem is NavMenuItem ->
                    oldItem.id == newItem.id
                oldItem is NavFavoriteFolder && newItem is NavFavoriteFolder ->
                    FavoriteFolderDiff.areItemsTheSame(
                        oldItem.favoriteFolder,
                        newItem.favoriteFolder
                    )
                else -> oldItem == newItem
            }
        }

        override fun areContentsTheSame(
            oldItem: NavigationModelItem,
            newItem: NavigationModelItem
        ): Boolean {
            return when {
                oldItem is NavMenuItem && newItem is NavMenuItem ->
                    oldItem.icon == newItem.icon &&
                            oldItem.titleRes == newItem.titleRes &&
                            oldItem.checked == newItem.checked
                oldItem is NavFavoriteFolder && newItem is NavFavoriteFolder ->
                    FavoriteFolderDiff.areContentsTheSame(
                        oldItem.favoriteFolder,
                        newItem.favoriteFolder
                    )
                else -> false
            }
        }
    }
}