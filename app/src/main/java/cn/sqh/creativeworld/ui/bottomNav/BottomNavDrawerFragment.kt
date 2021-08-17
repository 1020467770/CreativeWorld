package cn.sqh.creativeworld.ui.bottomNav

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import cn.sqh.creativeworld.R
import cn.sqh.creativeworld.core.data.Account
import cn.sqh.creativeworld.core.data.AccountStore
import cn.sqh.creativeworld.databinding.FragmentBottomNavDrawerBinding
import cn.sqh.creativeworld.ui.bottomNav.sandwich.AccountAdapter
import cn.sqh.creativeworld.ui.bottomNav.sandwich.OnSandwichStateChangedAction
import cn.sqh.creativeworld.ui.bottomNav.sandwich.OnSandwichSlideAction
import cn.sqh.creativeworld.utils.lerp
import cn.sqh.creativeworld.utils.themeColor
import cn.sqh.creativeworld.utils.themeInterpolator
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable
import kotlin.math.abs


class BottomNavDrawerFragment :
    Fragment(),
    NavigationAdapter.NavigationAdapterListener,
    AccountAdapter.AccountAdapterListener {

    private lateinit var mDataBinding: FragmentBottomNavDrawerBinding

    /**
     * 设置CoordinatorLayout需要的Behavior，
     * 根据Behavior的状态实现在CoordinatorLayout中DrawerFragment的动画变换
     */
    private val behavior: BottomSheetBehavior<FrameLayout> by lazy(LazyThreadSafetyMode.NONE) {
        BottomSheetBehavior.from(mDataBinding.backgroundContainer)
    }

    private val bottomSheetCallback = BottomNavigationDrawerCallback()


    private val navigationListeners: MutableList<NavigationAdapter.NavigationAdapterListener> =
        mutableListOf()

    /**
     * 夹层的状态enum，这里的夹层指的是头像展开后的layout
     */
    enum class SandwichState {
        //夹层关闭
        CLOSED,

        //夹层打开
        OPEN,

        //夹层正在转换
        SETTLING

    }

    private val backgroundShapeDrawable: MaterialShapeDrawable by lazy(LazyThreadSafetyMode.NONE) {
        val backgroundContext = mDataBinding.backgroundContainer.context
        MaterialShapeDrawable(
            backgroundContext,
            null,
            R.attr.bottomSheetStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                //获取副主题颜色
                backgroundContext.themeColor(R.attr.colorPrimarySurfaceVariant)
            )
            elevation = resources.getDimension(R.dimen.elevation_8)//materialDrawable的elevation高度，背景的elevation较低
            initializeElevationOverlay(requireContext())//刷新drawable以便重新计算阴影等值
        }
    }

    private val foregroundShapeDrawable: MaterialShapeDrawable by lazy(LazyThreadSafetyMode.NONE) {
        val foregroundContext = mDataBinding.foregroundContainer.context
        MaterialShapeDrawable(
            foregroundContext,
            null,
            R.attr.bottomSheetStyle,
            0
        ).apply {
            fillColor = ColorStateList.valueOf(
                foregroundContext.themeColor(R.attr.colorPrimarySurface)
            )
            elevation = resources.getDimension(R.dimen.elevation_16)//前景的elevation较低较高
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_NEVER//无阴影
            initializeElevationOverlay(requireContext())
            shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                .setTopEdge(
                    //使用谷歌开发人员写的圆角边界以及锐化效果的EdgeTreatment
                    SemiCircleEdgeCutoutTreatment(
                        resources.getDimension(R.dimen.grid_1),//这里必须要用Dimension，才能根据手机屏幕进行适配
                        resources.getDimension(R.dimen.grid_3),
                        0F,
                        resources.getDimension(R.dimen.navigation_drawer_profile_image_size_padded)
                    )
                )
                .build()
        }
    }

    //当按下系统的后退键时要关闭drawer
    private val closeDrawerOnBackPressed = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            close()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //注册回退监听
        requireActivity().onBackPressedDispatcher.addCallback(this, closeDrawerOnBackPressed)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mDataBinding = FragmentBottomNavDrawerBinding.inflate(inflater, container, false)
        mDataBinding.foregroundContainer.setOnApplyWindowInsetsListener { view, windowInsets ->
            //临时存放系统工具栏的高度，以便之后展开fragment不会超过它
            view.setTag(
                R.id.tag_system_window_inset_top,
                windowInsets.systemWindowInsetTop//API30才废弃的，API30之后可以用getInsets(WindowInsets.Type.systemBar())
            )
            windowInsets
        }
        return mDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDataBinding.run {
            backgroundContainer.background = backgroundShapeDrawable
            foregroundContainer.background = foregroundShapeDrawable

            //点击遮罩区就直接关闭drawer
            scrimView.setOnClickListener { close() }

            bottomSheetCallback.apply {
                //遮罩淡入淡出
                addOnSlideAction(AlphaSlideAction(scrimView))
                //让遮罩可见
                addOnStateChangedAction(VisibilityStateAction(scrimView))
                //使前景layout在滑动时更自然
                addOnSlideAction(
                    ForegroundSheetTransformSlideAction(
                        mDataBinding.foregroundContainer,
                        foregroundShapeDrawable,
                        mDataBinding.profileImageView
                    )
                )

                //drawer的状态改变就回到第一个元素的位置
                addOnStateChangedAction(ScrollToTopStateAction(navRecyclerView))

                //drawer发生任何状态改变都要把夹层关闭
                addOnStateChangedAction(object : OnStateChangedAction {
                    override fun onStateChanged(sheet: View, newState: Int) {
                        sandwichAnim?.cancel()
                        sandwichProgress = 0F
                    }
                })

                //当drawer不为open时，按下系统的返回键应该关闭drawer
                addOnStateChangedAction(object : OnStateChangedAction {
                    override fun onStateChanged(sheet: View, newState: Int) {
                        closeDrawerOnBackPressed.isEnabled =
                            newState != BottomSheetBehavior.STATE_HIDDEN
                    }
                })
            }

            profileImageView.setOnLongClickListener {
                toggleSandwich()
                true
            }

//            profileImageView.setOnClickListener { toggleSandwich() }

            behavior.addBottomSheetCallback(bottomSheetCallback)
            behavior.state = BottomSheetBehavior.STATE_HIDDEN

            val adapter = NavigationAdapter(this@BottomNavDrawerFragment)

            navRecyclerView.adapter = adapter
            NavigationStore.navigationList.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }
            NavigationStore.setNavigationMenuItemChecked(0)

            val accountAdapter = AccountAdapter(this@BottomNavDrawerFragment)
            accountRecyclerView.adapter = accountAdapter
            AccountStore.userAccounts.observe(viewLifecycleOwner) {
                accountAdapter.submitList(it)
                currentUserAccount = it.first { account -> account.isCurrentAccount }
            }

        }
    }

    fun toggle() {
        when {
            sandwichState == SandwichState.OPEN -> toggleSandwich()
            behavior.state == BottomSheetBehavior.STATE_HIDDEN -> open()
            behavior.state == BottomSheetBehavior.STATE_HIDDEN
                    || behavior.state == BottomSheetBehavior.STATE_HALF_EXPANDED
                    || behavior.state == BottomSheetBehavior.STATE_EXPANDED
                    || behavior.state == BottomSheetBehavior.STATE_COLLAPSED -> close()
        }
    }

    private fun open() {
        sandwichStateChangedActions.remove(closeSandwichThenCloseDrawerListener)
        behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
    }

    private val closeSandwichThenCloseDrawerListener = object : OnSandwichStateChangedAction {
        override fun onStateChanged(state: SandwichState) {
            if (state == SandwichState.CLOSED) {
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

    }

    private fun close() {
        if (sandwichState != SandwichState.CLOSED) {
            sandwichStateChangedActions.add(closeSandwichThenCloseDrawerListener)
            toggleSandwich()
        } else {
            behavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    fun addOnSlideAction(action: OnSlideAction) {
        bottomSheetCallback.addOnSlideAction(action)
    }

    fun addOnStateChangedAction(action: OnStateChangedAction) {
        bottomSheetCallback.addOnStateChangedAction(action)
    }

    fun addNavigationListener(listener: NavigationAdapter.NavigationAdapterListener) {
        navigationListeners.add(listener)
    }


    override fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem) {
        NavigationStore.setNavigationMenuItemChecked(item.id)
        close()
        navigationListeners.forEach { it.onNavMenuItemClicked(item) }
    }

    override fun onNavFavoriteItemClicked(item: NavigationModelItem.NavFavoriteFolder) {
        navigationListeners.forEach { it.onNavFavoriteItemClicked(item) }
    }


/////////////////////////////////////////////////////////////////////
    /**
     * 夹层相关的内容
     */

    private var sandwichState: SandwichState = SandwichState.CLOSED
    private var sandwichAnim: ValueAnimator? = null

    //夹层的动画变换使用motionInterpolatorPersistent线性插值器
    private val sandwichInterpolator by lazy(LazyThreadSafetyMode.NONE) {
        requireActivity().themeInterpolator(R.attr.motionInterpolatorPersistent)
    }


    private val sandwichSlideActions = mutableListOf<OnSandwichSlideAction>()
    private val sandwichStateChangedActions = mutableListOf<OnSandwichStateChangedAction>()


    private var sandwichProgress: Float = 0F
        set(value) {
            if (field != value) {
                onSandwichProgressChanged(value)
                val newState = when (value) {
                    0F -> SandwichState.CLOSED
                    1F -> SandwichState.OPEN
                    else -> SandwichState.SETTLING
                }
//                Log.d("TAG", "改变后的状态是$newState")
                if (sandwichState != newState) onSandwichStateChanged(newState)
                sandwichState = newState
                field = value
            }
        }


    private fun toggleSandwich() {
        val initialProgress = sandwichProgress
        val newProgress = when (sandwichState) {
            SandwichState.CLOSED -> {
                //存原本的drawer高度值以便恢复
                mDataBinding.backgroundContainer.setTag(
                    R.id.tag_view_top_snapshot,
                    mDataBinding.backgroundContainer.top
                )
                1F
            }
            SandwichState.OPEN -> 0F
            SandwichState.SETTLING -> return
        }
        sandwichAnim?.cancel()
        //使用数值动画来模拟进度的变化，使用快出慢进的插值器
        sandwichAnim = ValueAnimator.ofFloat(initialProgress, newProgress).apply {
            addUpdateListener {
                sandwichProgress = animatedValue as Float
            }
            interpolator = sandwichInterpolator
            duration = (abs(newProgress - initialProgress) *
                    resources.getInteger(R.integer.sqh_motion_duration_medium)).toLong()
        }
        sandwichAnim?.start()
    }

    private fun onSandwichProgressChanged(progress: Float) {
        mDataBinding.run {
            //分成前景淡出和背景淡入两部分动画结合进行
            val navProgress = lerp(0F, 1F, 0F, 0.5F, progress)
            val accProgress = lerp(0F, 1F, 0.5F, 1F, progress)

            foregroundContainer.translationY =
                (mDataBinding.foregroundContainer.height * 0.15F) * navProgress
            //头像随进度变大或变小
            profileImageView.scaleX = 1F - navProgress
            profileImageView.scaleY = 1F - navProgress
            //各view的透明度随进度变化
            profileImageView.alpha = 1F - navProgress
            foregroundContainer.alpha = 1F - navProgress
            accountRecyclerView.alpha = accProgress

            foregroundShapeDrawable.interpolation = 1F - navProgress

            //背景layout移动到合适的位置
            backgroundContainer.translationY = progress *
                    ((scrimView.bottom - accountRecyclerView.height
                            //最低值
                            - resources.getDimension(R.dimen.bottom_app_bar_height)) -
                            //获取原本drawer的高度值
                            (backgroundContainer.getTag(R.id.tag_view_top_snapshot) as Int))
        }

        //回调监听函数
        sandwichSlideActions.forEach { it.onSlide(progress) }
    }

    private fun onSandwichStateChanged(state: SandwichState) {
        //在夹层打开和关闭时维护前景的可见性
        when (state) {
            SandwichState.OPEN -> {
                mDataBinding.run {

                    foregroundContainer.visibility = View.GONE
                    profileImageView.isClickable = false
                }
            }
            else -> {
                mDataBinding.run {
                    foregroundContainer.visibility = View.VISIBLE
                    profileImageView.isClickable = true
                }
            }
        }

        sandwichStateChangedActions.forEach { it.onStateChanged(state) }
    }

    fun addOnSandwichSlideAction(action: OnSandwichSlideAction) {
        sandwichSlideActions.add(action)
    }

    override fun onAccountClicked(account: Account) {
        AccountStore.setCurrentUserAccount(account.id)
        toggleSandwich()
    }

}