package cn.sqh.creativeworld

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.transition.Slide
import androidx.transition.TransitionManager
import cn.sqh.creativeworld.core.BaseActivity
import cn.sqh.creativeworld.core.interfaces.SendCommentActionListener
import cn.sqh.creativeworld.databinding.ActivityMainBinding
import cn.sqh.creativeworld.ui.bottomNav.*
import cn.sqh.creativeworld.ui.bottomNav.sandwich.HalfCounterClockwiseRotateSlideAction
import cn.sqh.creativeworld.ui.comment.CommentFragment
import cn.sqh.creativeworld.ui.home.HomeFragmentDirections
import cn.sqh.creativeworld.ui.upload.UploadVideoFragmentDirections
import cn.sqh.creativeworld.utils.contentView
import com.google.android.material.bottomappbar.BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
import com.google.android.material.bottomappbar.BottomAppBar.FAB_ALIGNMENT_MODE_END
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.manager.PictureCacheManager
import com.luck.picture.lib.permissions.PermissionChecker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity :
    BaseActivity(),
    NavigationAdapter.NavigationAdapterListener,
    NavController.OnDestinationChangedListener,
    CommentFragment.ShowCommentListener,
    BottomNavDrawerFragment.SandwichProfileClickListener {

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
        requestPower()
    }

    private fun initialBottomNavigationAndFab() {
        mDataBinding.run {
            findNavController(R.id.nav_host_fragment).addOnDestinationChangedListener(
                this@MainActivity
            )
            /*
            ????????????FragmentContainerView????????????fragment??????????????????
            ????????????val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            navHostFragment.navController???????????????
             */
        }

        mDataBinding.fab.apply {
            setShowMotionSpecResource(R.animator.fab_show)
            setHideMotionSpecResource(R.animator.fab_hide)
            /*setOnClickListener {
                navigateToCompose()
            }*/
        }

        bottomNavDrawer.apply {
            //????????????????????????????????????
            sandwichProfileClickListener = this@MainActivity
            //?????????drawer????????????????????????
            addOnSlideAction(HalfClockwiseRotateSlideAction(mDataBinding.bottomAppBarChevron))
            //??????????????????
            addOnSlideAction(AlphaSlideAction(mDataBinding.bottomAppBarTitle, true))
            //???????????????fab
            addOnStateChangedAction(ShowHideFabStateAction(mDataBinding.fab))
            //?????????drawer?????????????????????????????????
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

    private fun navigateToUploadVideo() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialElevationScale(false).apply {
                duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
            }
            reenterTransition = MaterialElevationScale(true).apply {
                duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
            }
        }
        val direction = UploadVideoFragmentDirections.actionGlobalUploadVideoFragment()
        findNavController(R.id.nav_host_fragment).navigate(direction)
    }

    override fun onNavMenuItemClicked(item: NavigationModelItem.NavMenuItem) {
        when (item.id) {
            NavigationStore.COMMUNITY -> navigateToCommunity(item.titleRes)
            NavigationStore.HOME_PAGE -> navigateToIndex(item.titleRes)
        }
    }

    private fun navigateToIndex(titleRes: Int) {
        mDataBinding.bottomAppBarTitle.text = getString(titleRes)
        currentNavigationFragment?.apply {
            exitTransition = MaterialFadeThrough().apply {
                duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
            }
        }
        val directions = HomeFragmentDirections.actionGlobalHomeFragment()
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    private fun navigateToCommunity(@StringRes titleRes: Int) {
        mDataBinding.bottomAppBarTitle.text = getString(titleRes)
        currentNavigationFragment?.apply {
            exitTransition = MaterialFadeThrough().apply {
                duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
            }
        }
        val directions = HomeFragmentDirections.actionGlobalTweetsFragment()
        findNavController(R.id.nav_host_fragment).navigate(directions)
    }

    private fun navigateToMainUserInterface() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialFadeThrough().apply {
                duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
            }
            reenterTransition = MaterialFadeThrough().apply {
                duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
            }
        }
        val directions = HomeFragmentDirections.actionGlobalUserInterfaceFragment()
        findNavController(R.id.nav_host_fragment).navigate(directions)
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
                    fab.setOnClickListener {
                        navigateToUploadVideo()
                    }
                    showBottomAppBar()
                    hideCommentCard()
                    fab.setImageResource(R.drawable.asl_upload_backup)
                }
            }

            R.id.uploadVideoFragment -> {
                mDataBinding.run {
                    hideBottomAppBar(true)
                }
            }

            R.id.tweetsFragment -> {
                mDataBinding.run {
                    fab.setOnClickListener { }
                    showBottomAppBar()
                    fab.setImageResource(R.drawable.asl_edit_backup)
                }
            }

            R.id.videoDetailFragment -> {
                mDataBinding.run {
                    hideBottomAppBar(true)
                }
            }

            R.id.tweetDetailFragment -> {
                mDataBinding.run {
                    hideBottomAppBar(true)
                }
            }

            R.id.userInterfaceFragment -> {
                mDataBinding.run {
                    hideBottomAppBar(true)
                }
            }
        }
    }

    private fun showBottomAppBar() {
        mDataBinding.run {
            fab.visibility = View.VISIBLE
            setHomeBottomAppBarView()
            bottomAppBar.performShow()
            bottomAppBar.hideOnScroll = true
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
                fab.setOnClickListener {
                    findNavController(R.id.nav_host_fragment).navigateUp()
                }
                fab.setImageState(intArrayOf(android.R.attr.state_activated), true)
                fab.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
                    override fun onHidden(fab: FloatingActionButton) {
                        super.onHidden(fab)
                        bottomAppBar.fabAlignmentMode = FAB_ALIGNMENT_MODE_END
                        fab.show()
                    }
                })
            } else {
//                fab.visibility = View.GONE
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

    private fun clearPictureCache() {
        // ?????????????????????????????????????????????????????? ??????:????????????????????????????????? ?????????????????????
        if (PermissionChecker.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            //PictureCacheManager.deleteCacheDirFile(this, PictureMimeType.ofImage());
            PictureCacheManager.deleteAllCacheDirRefreshFile(this)
        } else {
            PermissionChecker.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PictureConfig.APPLY_STORAGE_PERMISSIONS_CODE
            )
        }
    }


    fun requestPower() { //??????????????????????????????
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //????????????????????????????????????????????????????????????????????????????????? true???
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) { //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????.??????????????????"????????????"??????????????????false
            } else {
                //????????????????????????????????????????????????????????????????????????1??????????????????????????????????????????onRequestPermissionsResult????????????????????????
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }

    private fun setHomeBottomAppBarView() {
        mDataBinding.run {
            bottomAppBarContentContainer.visibility = View.VISIBLE
            btnComment.visibility = View.GONE
            bottomAppBar.visibility = View.VISIBLE
        }
    }

    private fun setCommentBottomAppBarView() {
        mDataBinding.run {
            bottomAppBarContentContainer.visibility = View.GONE
            btnComment.visibility = View.VISIBLE
            bottomAppBar.visibility = View.VISIBLE
        }
    }


    private fun toggleCommentCard(
        targetId: Long,
        sendCommentActionListener: SendCommentActionListener?
    ) {
        val commentCard = mDataBinding.commentCard
        if (commentCard.visibility == View.VISIBLE) {
            hideCommentCard()
        } else {
            showCommentCard(targetId, sendCommentActionListener)
        }
    }

    private fun hideCommentCard() {
        mDataBinding.run {
            if (commentCard.visibility == View.VISIBLE) {
                val transform = Slide().apply {
                    duration = resources.getInteger(R.integer.sqh_motion_duration_large).toLong()
                    addTarget(commentCard)
                }
                TransitionManager.beginDelayedTransition(coordinatorLayout, transform)
                commentCard.visibility = View.GONE
                btnSendComment.setOnClickListener(null)
            }
        }
    }

    private fun showCommentCard(
        targetId: Long,
        sendCommentActionListener: SendCommentActionListener?
    ) {
        mDataBinding.run {
//            commentCard.visibility
            val transform = MaterialContainerTransform().apply {
                startView = btnComment
                endView = commentCard
                scrimColor = Color.TRANSPARENT
                endElevation = resources.getDimension(R.dimen.elevation_3)
                addTarget(commentCard)
            }
            TransitionManager.beginDelayedTransition(coordinatorLayout, transform)
            commentCard.visibility = View.VISIBLE
            btnSendComment.setOnClickListener {
                sendCommentActionListener?.sendComment(
                    targetId,
                    commentEditText.text.toString().trim()
                ) {
                    hideCommentCard()
                }
            }
        }
    }

    override fun onCommentFragmentShow(
        targetId: Long,
        sendCommentActionListener: SendCommentActionListener?
    ) {
        mDataBinding.run {
            setCommentBottomAppBarView()
            bottomAppBar.performShow()
            bottomAppBar.hideOnScroll = false
            btnComment.setOnClickListener {
                toggleCommentCard(targetId, sendCommentActionListener)
            }
        }
    }


    override fun onCommentFragmentHide() {
        hideBottomAppBar(true)
        hideCommentCard()
    }


    override fun onSandwichProfileClick() {
        navigateToMainUserInterface()
    }


    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, MainActivity::class.java))
        }
    }


}

