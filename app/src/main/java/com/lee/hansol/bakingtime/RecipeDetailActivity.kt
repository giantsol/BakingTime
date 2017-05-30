package com.lee.hansol.bakingtime

import android.app.Activity
import android.app.Fragment
import android.app.FragmentManager
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.View

import com.lee.hansol.bakingtime.adapters.DrawerRecyclerViewAdapter
import com.lee.hansol.bakingtime.adapters.StepsRecyclerViewAdapter
import com.lee.hansol.bakingtime.fragments.RecipeStepDetailFragment
import com.lee.hansol.bakingtime.fragments.RecipeStepListFragment
import com.lee.hansol.bakingtime.helpers.ActionBarHelper
import com.lee.hansol.bakingtime.helpers.AnimationHelper
import com.lee.hansol.bakingtime.helpers.DataStorage
import com.lee.hansol.bakingtime.models.Recipe
import com.lee.hansol.bakingtime.utils.User
import kotlinx.android.synthetic.main.activity_recipe_detail.*
import kotlinx.android.synthetic.main.activity_recipe_detail.view.*

class RecipeDetailActivity : AppCompatActivity(),
        StepsRecyclerViewAdapter.OnStepItemClickListener,
        DrawerRecyclerViewAdapter.OnDrawerItemClickListener,
        RecipeStepDetailFragment.OnPrevNextButtonClickListener {
    private var isStepDetailFragmentVisible: Boolean = false
    private lateinit var drawerToggle: MyDrawerToggle
    private lateinit var actionBarHelper: ActionBarHelper
    private lateinit var animationHelper: AnimationHelper
    private var stepListFragment = RecipeStepListFragment()
    private var stepDetailFragment = RecipeStepDetailFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)
        initialize(savedInstanceState)
    }

    private fun initialize(savedInstanceState: Bundle?) {
        initializeVariables()
        initializeActionBar()
        initializeDrawer()
        if (savedInstanceState != null)
            getFragmentsFromContainers()
        else
            initializeFragmentContainers()
    }

    private fun initializeVariables() {
        drawerToggle = MyDrawerToggle(this, activity_recipe_detail_navigation_drawer,
                R.string.content_description_drawer_open,
                R.string.content_description_drawer_close)
        isStepDetailFragmentVisible = false
        actionBarHelper = ActionBarHelper(supportActionBar)
        animationHelper = AnimationHelper(this)
    }

    private fun initializeActionBar() {
        actionBarHelper.setHomeButtonEnabled()
        setActionBarTitleToCurrentRecipe()
    }

    private fun setActionBarTitleToCurrentRecipe() {
        val currentRecipe = DataStorage.getInstance().currentRecipeObject
        actionBarHelper.setTitleToRecipeName(currentRecipe)
    }

    private fun initializeDrawer() {
        activity_recipe_detail_navigation_drawer.addDrawerListener(drawerToggle)
        initializeDrawerView()
    }

    private fun initializeDrawerView() {
        activity_recipe_detail_navigation_drawer_view.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@RecipeDetailActivity)
            adapter = DrawerRecyclerViewAdapter(this@RecipeDetailActivity)
        }
    }

    private fun getFragmentsFromContainers() {
        if (User.isTablet(this))
            getFragmentsFromTabletLayout()
        else
            getFragmentsFromNonTabletLayout()
    }

    private fun getFragmentsFromTabletLayout() {
        val stepListFragContainer: Fragment? = fragmentManager.findFragmentById(R.id.activity_recipe_detail_step_list_fragment_container)
        val stepDetailFragContainer: Fragment? = fragmentManager.findFragmentById(R.id.activity_recipe_detail_step_detail_fragment_container)
        stepListFragment = if (stepListFragContainer == null) RecipeStepListFragment()
                else stepListFragContainer as RecipeStepListFragment
        if (stepDetailFragContainer != null) {
            stepDetailFragment = stepDetailFragContainer as RecipeStepDetailFragment
            isStepDetailFragmentVisible = true
        } else {
            stepDetailFragment = RecipeStepDetailFragment()
            isStepDetailFragmentVisible = false
        }
    }

    private fun getFragmentsFromNonTabletLayout() {
        val fragment: Fragment = fragmentManager.findFragmentById(R.id.activity_recipe_detail_fragment_container)
        if (fragment is RecipeStepListFragment) {
            stepListFragment = fragment
            stepDetailFragment = RecipeStepDetailFragment()
            isStepDetailFragmentVisible = false
        } else {
            stepListFragment = RecipeStepListFragment()
            stepDetailFragment = fragment as RecipeStepDetailFragment
            isStepDetailFragmentVisible = true
        }
    }

    private fun initializeFragmentContainers() {
        if (User.isTablet(this))
            initializeAsTabletLayout()
        else
            initializeAsNonTabletLayout()
    }

    private fun initializeAsTabletLayout() {
        fragmentManager.beginTransaction()
                .add(R.id.activity_recipe_detail_step_list_fragment_container, stepListFragment)
                .commit()
    }

    private fun initializeAsNonTabletLayout() {
        fragmentManager.beginTransaction()
                .add(R.id.activity_recipe_detail_fragment_container, stepListFragment)
                .commit()
    }

    override fun onResume() {
        super.onResume()
        setDrawerAvailability()
    }

    private fun setDrawerAvailability() {
        if (!User.isTablet(this) && isStepDetailFragmentVisible)
            disableDrawer()
        else
            enableDrawer()
    }

    private fun disableDrawer() {
        activity_recipe_detail_navigation_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        drawerToggle.isDrawerIndicatorEnabled = false
    }

    private fun enableDrawer() {
        activity_recipe_detail_navigation_drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        drawerToggle.isDrawerIndicatorEnabled = true
    }

    override fun onBackPressed() {
        if (activity_recipe_detail_navigation_drawer.isDrawerOpen(activity_recipe_detail_navigation_drawer_view))
            activity_recipe_detail_navigation_drawer.closeDrawer(activity_recipe_detail_navigation_drawer_view)
        else if (isIngredientsSliderOpen)
            closeIngredientsSlider()
        else if (isStepDetailFragmentVisible && stepDetailFragment.isFullMode)
            stepDetailFragment.exitFullMode()
        else if (!User.isTablet(this) && isStepDetailFragmentVisible)
            replaceStepDetailFragmentWithStepListFragment()
        else
            super.onBackPressed()
    }

    private val isIngredientsSliderOpen: Boolean
         = stepListFragment.isVisible && stepListFragment.isSliderOpen

    private fun closeIngredientsSlider() {
        stepListFragment.closeSlider()
    }

    private fun replaceStepDetailFragmentWithStepListFragment() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit)
                .replace(R.id.activity_recipe_detail_fragment_container, stepListFragment)
                .commit()
        isStepDetailFragmentVisible = false
        setDrawerAvailability()
    }

    override fun onDrawerItemClick(recipeIndex: Int) {
        changeRecipe(recipeIndex)
    }

    private fun changeRecipe(recipeIndex: Int) {
        DataStorage.getInstance().currentRecipeIndex = recipeIndex //TODO
        closeDrawerAndShowNewRecipeDetail()
    }

    private fun closeDrawerAndShowNewRecipeDetail() {
        drawerToggle.notifyNewRecipeAhead()
        activity_recipe_detail_navigation_drawer.closeDrawer(activity_recipe_detail_navigation_drawer_view)
    }

    private inner class MyDrawerToggle(activity: Activity, drawerLayout: DrawerLayout,
                                       openDrawerContentDescRes: Int, closeDrawerContentDescRes: Int):
            ActionBarDrawerToggle(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes) {

        private var isDrawerDeterminedOpen = false
        private var isChangingRecipe = false

        override fun onDrawerSlide(drawerView: View?, slideOffset: Float) {
            super.onDrawerSlide(drawerView, slideOffset)
            if (isDrawerDeterminedOpen)
                watchForClosing(slideOffset)
            else
                watchForOpening(slideOffset)
        }

        private fun watchForClosing(slideOffset: Float) {
            if (slideOffset < 0.3) {
                determineClose()
            }
        }

        private fun determineClose() {
            isDrawerDeterminedOpen = false
            setActionBarTitleToCurrentRecipe()

            if (isChangingRecipe) {
                showNewRecipeDetail()
                isChangingRecipe = false
            }
        }

        private fun watchForOpening(slideOffset: Float) {
            if (slideOffset > 0.7) {
                determineOpen()
            }
        }

        private fun determineOpen() {
            isDrawerDeterminedOpen = true
            actionBarHelper.setTitle(getString(R.string.text_choose_recipe))
        }

        internal fun notifyNewRecipeAhead() {
            isChangingRecipe = true
        }
    }

    private fun showNewRecipeDetail() {
        renewStepListFragment()
        clearStepDetailFragmentContainerIfTablet()
    }

    private fun renewStepListFragment() {
        animationHelper.fadeOutRenewFadeIn(stepListFragment)
    }

    private fun clearStepDetailFragmentContainerIfTablet() {
        if (User.isTablet(this) && isStepDetailFragmentVisible) {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(0, R.animator.fragment_fade_out)
                    .remove(stepDetailFragment).commit()
            isStepDetailFragmentVisible = false
        }
    }

    override fun onStepItemClick(stepIndex: Int) {
        DataStorage.getInstance().currentStepIndex = stepIndex //TODO
        if (User.isTablet(this)) {
            showStepDetailFragmentInRightPanel()
            stepListFragment.renew()
        } else
            replaceStepListFragmentWithStepDetailFragment()
    }

    private fun showStepDetailFragmentInRightPanel() {
        if (isStepDetailFragmentVisible)
            animationHelper.slideLeftExitRenewSlideRightEnter(stepDetailFragment)
        else
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.fragment_slide_right_enter, 0)
                    .add(R.id.activity_recipe_detail_step_detail_fragment_container, stepDetailFragment)
                    .commit()
        isStepDetailFragmentVisible = true
    }

    private fun replaceStepListFragmentWithStepDetailFragment() {
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit)
                .replace(R.id.activity_recipe_detail_fragment_container, stepDetailFragment)
                .commit()
        isStepDetailFragmentVisible = true
        setDrawerAvailability()
    }

    override fun onPrevButtonClicked() {
        if (DataStorage.getInstance().hasPreviousStep()) {
            DataStorage.getInstance().moveToPreviousStep()
            animationHelper.slideRightExitRenewSlideRightEnter(stepDetailFragment)
            if (User.isTablet(this))
                stepListFragment.renew()
        } else {
            showTryingToGoPreviousAnimation()
        }
    }

    private fun showTryingToGoPreviousAnimation() {

    }

    override fun onNextButtonClicked() {
        if (DataStorage.getInstance().hasNextStep()) {
            DataStorage.getInstance().moveToNextStep()
            animationHelper.slideLeftExitRenewSlideLeftEnter(stepDetailFragment)
            if (User.isTablet(this))
                stepListFragment.renew()
        } else {
            showTryingToGoNextAnimation()
        }
    }

    private fun showTryingToGoNextAnimation() {

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            if (!drawerToggle.isDrawerIndicatorEnabled) {
                replaceStepDetailFragmentWithStepListFragment()
                return true
            } else if (isStepDetailFragmentVisible && stepDetailFragment.isFullMode) {
                stepDetailFragment.exitFullMode()
                return true
            }
        }
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

}
