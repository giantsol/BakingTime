package com.lee.hansol.bakingtime;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.lee.hansol.bakingtime.adapters.DrawerRecyclerViewAdapter;
import com.lee.hansol.bakingtime.adapters.StepsRecyclerViewAdapter;
import com.lee.hansol.bakingtime.fragments.RecipeStepDetailFragment;
import com.lee.hansol.bakingtime.fragments.RecipeStepListFragment;
import com.lee.hansol.bakingtime.helpers.ActionBarHelper;
import com.lee.hansol.bakingtime.helpers.AnimationHelper;
import com.lee.hansol.bakingtime.helpers.DataStorage;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.utils.User;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lee.hansol.bakingtime.utils.LogUtils.log;

public class RecipeDetailActivity extends AppCompatActivity
        implements StepsRecyclerViewAdapter.OnStepItemClickListener,
        DrawerRecyclerViewAdapter.OnDrawerItemClickListener,
        RecipeStepDetailFragment.OnPrevNextButtonClickListener {
    private boolean isStepDetailFragmentVisible;
    private MyDrawerToggle drawerToggle;
    private ActionBarHelper actionBarHelper;
    private AnimationHelper animationHelper;
    @NonNull private RecipeStepListFragment stepListFragment = new RecipeStepListFragment();
    @NonNull private RecipeStepDetailFragment stepDetailFragment = new RecipeStepDetailFragment();

    @BindView(R.id.activity_recipe_detail_navigation_drawer) DrawerLayout drawer;
    @BindView(R.id.activity_recipe_detail_navigation_drawer_view) RecyclerView drawerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        initialize();
    }

    private void initialize() {
        initializeVariables();
        initializeActionBar();
        initializeDrawer();
        initializeFragmentContainers();
    }

    private void initializeVariables() {
        drawerToggle = new MyDrawerToggle(this, drawer, R.string.content_description_drawer_open,
                R.string.content_description_drawer_close);
        isStepDetailFragmentVisible = false;
        actionBarHelper = new ActionBarHelper(getSupportActionBar());
        animationHelper = new AnimationHelper(this);
    }

    private void initializeActionBar() {
        actionBarHelper.setHomeButtonEnabled();
        setActionBarTitleToCurrentRecipe();
    }

    private void setActionBarTitleToCurrentRecipe() {
        Recipe currentRecipe = DataStorage.getInstance().getCurrentRecipeObject();
        actionBarHelper.setTitleToRecipeName(currentRecipe);
    }

    private void initializeDrawer() {
        drawer.addDrawerListener(drawerToggle);
        initializeDrawerView();
    }

    private void initializeDrawerView() {
        drawerView.setHasFixedSize(true);
        drawerView.setLayoutManager(new LinearLayoutManager(this));
        drawerView.setAdapter(new DrawerRecyclerViewAdapter(this));
    }

    private void initializeFragmentContainers() {
        if (User.isTablet(this)) initializeAsTabletLayout();
        else initializeAsNonTabletLayout();
    }

    private void initializeAsTabletLayout() {
        getFragmentManager().beginTransaction()
                .add(R.id.activity_recipe_detail_step_list_fragment_container, stepListFragment)
                .commit();
    }

    private void initializeAsNonTabletLayout() {
        getFragmentManager().beginTransaction()
                .add(R.id.activity_recipe_detail_fragment_container, stepListFragment)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDrawerAvailability();
    }

    private void setDrawerAvailability() {
        if (!User.isTablet(this) && isStepDetailFragmentVisible)
            disableDrawer();
        else
            enableDrawer();
    }

    private void disableDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerToggle.setDrawerIndicatorEnabled(false);
    }

    private void enableDrawer() {
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        drawerToggle.setDrawerIndicatorEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(drawerView))
            drawer.closeDrawer(drawerView);
        else if (isIngredientsSliderOpen())
            closeIngredientsSlider();
        else if (isStepDetailFragmentVisible && stepDetailFragment.isFullMode)
            stepDetailFragment.exitFullMode();
        else if (!User.isTablet(this) && isStepDetailFragmentVisible)
            replaceStepDetailFragmentWithStepListFragment();
        else
            super.onBackPressed();
    }

    private boolean isIngredientsSliderOpen() {
        return stepListFragment.isVisible() && stepListFragment.isSliderOpen;
    }

    private void closeIngredientsSlider() {
        stepListFragment.closeSlider();
    }

    private void replaceStepDetailFragmentWithStepListFragment() {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit)
                .replace(R.id.activity_recipe_detail_fragment_container, stepListFragment)
                .commit();
        isStepDetailFragmentVisible = false;
        setDrawerAvailability();
    }

    @Override
    public void onDrawerItemClick(int recipeIndex) {
        changeRecipe(recipeIndex);
    }

    private void changeRecipe(int recipeIndex) {
        DataStorage.getInstance().setCurrentRecipeIndex(recipeIndex);
        closeDrawerAndShowNewRecipeDetail();
    }

    private void closeDrawerAndShowNewRecipeDetail() {
        drawerToggle.notifyNewRecipeAhead();
        drawer.closeDrawer(drawerView);
    }

    private class MyDrawerToggle extends ActionBarDrawerToggle {

        private MyDrawerToggle(Activity activity, DrawerLayout drawerLayout,
                               int openDrawerContentDescRes, int closeDrawerContentDescRes) {
            super(activity, drawerLayout, openDrawerContentDescRes, closeDrawerContentDescRes);
        }

        private boolean isDrawerDeterminedOpen = false;
        private boolean isChangingRecipe = false;

        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            super.onDrawerSlide(drawerView, slideOffset);
            if (isDrawerDeterminedOpen)
                watchForClosing(slideOffset);
            else
                watchForOpening(slideOffset);
        }

        private void watchForClosing(float slideOffset) {
            if (slideOffset < 0.3) {
                determineClose();
            }
        }

        private void determineClose() {
            isDrawerDeterminedOpen = false;
            setActionBarTitleToCurrentRecipe();

            if (isChangingRecipe) {
                showNewRecipeDetail();
                isChangingRecipe = false;
            }
        }

        private void watchForOpening(float slideOffset) {
            if (slideOffset > 0.7) {
                determineOpen();
            }
        }

        private void determineOpen() {
            isDrawerDeterminedOpen = true;
            actionBarHelper.setTitle(getString(R.string.text_choose_recipe));
        }

        private void notifyNewRecipeAhead() {
            isChangingRecipe = true;
        }
    }

    private void showNewRecipeDetail() {
        renewStepListFragment();
        clearStepDetailFragmentContainerIfTablet();
    }

    private void renewStepListFragment() {
        animationHelper.fadeOutRenewFadeIn(stepListFragment);
    }

    private void clearStepDetailFragmentContainerIfTablet() {
        if (User.isTablet(this) && isStepDetailFragmentVisible) {
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(0, R.animator.fragment_fade_out)
                    .remove(stepDetailFragment).commit();
            isStepDetailFragmentVisible = false;
        }
    }

    @Override
    public void onStepItemClick(int stepIndex) {
        DataStorage.getInstance().setCurrentStepIndex(stepIndex);
        if (User.isTablet(this)) {
            showStepDetailFragmentInRightPanel();
            stepListFragment.renew();
        }
        else
            replaceStepListFragmentWithStepDetailFragment();
    }

    private void showStepDetailFragmentInRightPanel() {
        if (isStepDetailFragmentVisible)
            animationHelper.slideLeftExitRenewSlideRightEnter(stepDetailFragment);
        else
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.fragment_slide_right_enter, 0)
                    .add(R.id.activity_recipe_detail_step_detail_fragment_container, stepDetailFragment)
                    .commit();
        isStepDetailFragmentVisible = true;
    }

    private void replaceStepListFragmentWithStepDetailFragment() {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit)
                .replace(R.id.activity_recipe_detail_fragment_container, stepDetailFragment)
                .commit();
        isStepDetailFragmentVisible = true;
        setDrawerAvailability();
    }

    @Override
    public void onPrevButtonClicked() {
        if (DataStorage.getInstance().hasPreviousStep()) {
            DataStorage.getInstance().moveToPreviousStep();
            animationHelper.slideRightExitRenewSlideRightEnter(stepDetailFragment);
            if (User.isTablet(this))
                stepListFragment.renew();
        } else {
            showTryingToGoPreviousAnimation();
        }
    }

    private void showTryingToGoPreviousAnimation() {

    }

    @Override
    public void onNextButtonClicked() {
        if (DataStorage.getInstance().hasNextStep()) {
            DataStorage.getInstance().moveToNextStep();
            animationHelper.slideLeftExitRenewSlideLeftEnter(stepDetailFragment);
            if (User.isTablet(this))
                stepListFragment.renew();
        } else {
            showTryingToGoNextAnimation();
        }
    }

    private void showTryingToGoNextAnimation() {

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!drawerToggle.isDrawerIndicatorEnabled()) {
                replaceStepDetailFragmentWithStepListFragment();
                return true;
            } else if (isStepDetailFragmentVisible && stepDetailFragment.isFullMode) {
                stepDetailFragment.exitFullMode();
                return true;
            }
        }
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (isStepDetailFragmentVisible && (stepDetailFragment.gestureDetector != null))
//            stepDetailFragment.gestureDetector.onTouchEvent(ev);
//        return super.dispatchTouchEvent(ev);
//    }
}
