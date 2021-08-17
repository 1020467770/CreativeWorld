package cn.sqh.creativeworld.core.data

import androidx.recyclerview.widget.DiffUtil

typealias FavoriteFolder = String

object FavoriteFolderDiff : DiffUtil.ItemCallback<FavoriteFolder>() {
    override fun areItemsTheSame(oldItem: FavoriteFolder, newItem: FavoriteFolder) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: FavoriteFolder, newItem: FavoriteFolder) =
        oldItem == newItem
}
