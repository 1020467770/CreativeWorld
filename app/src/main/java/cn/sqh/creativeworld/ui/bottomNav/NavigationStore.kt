package cn.sqh.creativeworld.ui.bottomNav

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import cn.sqh.creativeworld.R

object NavigationStore {
    const val HOME_PAGE = 0
    const val FAVORITE = 1
    const val UPLOAD_VIDEOS = 2
    const val COMMUNITY = 3

    private var navigationMenuItems = mutableListOf(
        NavigationModelItem.NavMenuItem(
            id = HOME_PAGE,
            icon = R.drawable.ic_twotone_inbox,
            titleRes = R.string.navigation_home_page,
            checked = false
        ),
        NavigationModelItem.NavMenuItem(
            id = FAVORITE,
            icon = R.drawable.ic_twotone_stars,
            titleRes = R.string.navigation_favorite,
            checked = false
        ),
        NavigationModelItem.NavMenuItem(
            id = UPLOAD_VIDEOS,
            icon = R.drawable.ic_twotone_send,
            titleRes = R.string.navigation_upload_videos,
            checked = false
        ),
        NavigationModelItem.NavMenuItem(
            id = COMMUNITY,
            icon = R.drawable.ic_twotone_delete,
            titleRes = R.string.navigation_community,
            checked = false
        )
    )

    private val _navigationList: MutableLiveData<List<NavigationModelItem>> = MutableLiveData()
    val navigationList: LiveData<List<NavigationModelItem>>
        get() = _navigationList

    init {
        postListUpdate()
    }

    fun setNavigationMenuItemChecked(id: Int): Boolean {
        var updated = false
        navigationMenuItems.forEachIndexed { index, item ->
            val shouldCheck = item.id == id
            if (item.checked != shouldCheck) {
                navigationMenuItems[index] = item.copy(checked = shouldCheck)
                updated = true
            }
        }
        if (updated) postListUpdate()
        return updated
    }

    private fun postListUpdate() {
        val newList = navigationMenuItems +
                (NavigationModelItem.NavDivider("Folders"))
        _navigationList.value = newList
    }

}