package cn.sqh.creativeworld

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import cn.sqh.creativeworld.core.BaseActivity
import cn.sqh.creativeworld.databinding.ActivityMainBinding
import cn.sqh.creativeworld.ui.bottomNav.*
import cn.sqh.creativeworld.ui.bottomNav.sandwich.HalfCounterClockwiseRotateSlideAction
import cn.sqh.creativeworld.ui.email.ComposeFragmentDirections
import cn.sqh.creativeworld.utils.contentView
import com.google.android.material.bottomappbar.BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
import com.google.android.material.bottomappbar.BottomAppBar.FAB_ALIGNMENT_MODE_END
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialElevationScale

class MainActivity :
    BaseActivity(),
    NavigationAdapter.NavigationAdapterListener,
    NavController.OnDestinationChangedListener {

    private val mDataBinding: ActivityMainBinding by contentView(R.layout.activity_main)
    private val bottomNavDrawer: BottomNavDrawerFragment by lazy(LazyThreadSafetyMode.NONE) {
        supportFragmentManager.findFragmentById(R.id.bottom_nav_drawer) as BottomNavDrawerFragment
    }

    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialBottomNavigationAndFab()
    }

    private fun initialBottomNavigationAndFab() {
        mDataBinding.run {
            findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(
                this@MainActivity
            )
            /*
            如果使用FragmentContainerView而是使用fragment作为管理器，
            可以采用val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController获取控制器
             */
        }

        mDataBinding.fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            setOnClickListener {
                navigateToCompose()
            }
        }

        bottomNavDrawer.apply {
            //当打开drawer时角标向下转半圈
            addOnSlideAction(HalfClockwiseRotateSlideAction(mDataBinding.bottomAppBarChevron))
            //文字淡入淡出
            addOnSlideAction(AlphaSlideAction(mDataBinding.bottomAppBarTitle, true))
            //隐藏fab
            addOnStateChangedAction(ShowHideFabStateAction(mDataBinding.fab))
            //当打开drawer的夹层时角标向上转半圈
            addOnSandwichSlideAction(HalfCounterClockwiseRotateSlideAction(mDataBinding.bottomAppBarChevron))
            addNavigationListener(this@MainActivity)
        }


        mDataBinding.bottomAppBar.apply {
            /*setNavigationOnClickListener {
                bottomNavDrawer.toggle()
            }*/
        }

        //
        mDataBinding.bottomAppBarContentContainer.setOnClickListener {
            bottomNavDrawer.toggle()
        }
    }

    private fun navigateToCompose() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialElevationScale(false).apply {
                duration = 300.toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = 300.toLong()
            }
        }
        val direction = ComposeFragmentDirections.actionGlobalComposeFragment()
        findNavController(R.id.nav_host_fragment).navigate(direction)
    }

    override fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem) {
//
    }

    override fun onNavFavoriteItemClicked(item: NavigationModelItem.NavFavoriteFolder) {
//        TODO("Not yet implemented")
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.homeFragment -> {
                mDataBinding.run {
                    showBottomAppBar()
                }
            }

            R.id.videoDetailFragment -> {
                mDataBinding.run {
                    hideBottomAppBar(true)
                }
            }
        }
    }

    private fun showBottomAppBar() {
        mDataBinding.run {
            bottomAppBar.visibility = View.VISIBLE
            bottomAppBar.performShow()
            fab.setImageState(intArrayOf(-android.R.attr.state_activated), true)
            fab.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                override fun onHidden(fab: FloatingActionButton) {
                    super.onHidden(fab)
                    bottomAppBar.fabAlignmentMode = FAB_ALIGNMENT_MODE_CENTER
                    fab.show()
                }
            })
        }
    }

    private fun hideBottomAppBar(isBtnChangeToBack: Boolean) {
        mDataBinding.run {
            bottomAppBar.performHide()
            if (isBtnChangeToBack) {
                fab.setImageState(intArrayOf(android.R.attr.state_activated), true)
                fab.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                    override fun onHidden(fab: FloatingActionButton) {
                        super.onHidden(fab)
                        bottomAppBar.fabAlignmentMode = FAB_ALIGNMENT_MODE_END
                        fab.show()
                    }
                })
            } else {
                fab.hide()
            }
            bottomAppBar.animate().setListener(object : AnimatorListenerAdapter() {
                var isCanceled = false
                override fun onAnimationEnd(animation: Animator?) {
                    if (isCanceled) return
                    bottomAppBar.visibility = View.GONE
                    if (!isBtnChangeToBack) fab.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator?) {
                    isCanceled = true
                }
            })
        }
    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }
}