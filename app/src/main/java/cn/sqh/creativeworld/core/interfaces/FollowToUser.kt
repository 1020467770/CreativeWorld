package cn.sqh.creativeworld.core.interfaces

import android.view.View
import cn.sqh.creativeworld.core.data.UserId

/**
 * 因为之后关注按钮可能出现在多个不同地方，所以可以把这个接口从ui层中分离出来
 * 在需要的关注ui处实现该接口
 */
interface FollowToUserListener {
    fun onFollowAction(view: View, userId: UserId)
}