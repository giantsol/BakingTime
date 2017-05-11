package com.lee.hansol.bakingtime;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.lee.hansol.bakingtime.adapters.DrawerRecyclerViewAdapter;
import com.lee.hansol.bakingtime.adapters.StepsRecyclerViewAdapter;
import com.lee.hansol.bakingtime.fragments.RecipeStepDetailFragment;
import com.lee.hansol.bakingtime.fragments.RecipeStepListFragment;
import com.lee.hansol.bakingtime.helpers.DataHelper;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.utils.User;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeDetailActivity extends AppCompatActivity
        implements StepsRecyclerViewAdapter.OnStepItemClickListener,
        DrawerRecyclerViewAdapter.OnDrawerItemClickListener,
        RecipeStepDetailFragment.OnPrevNextButtonClickListener {
    private boolean isStepDetailFragmentVisible;
    private MyDrawerToggle drawerToggle;
    private ActionBar actionBar;
    @NonNull private RecipeStepListFragment stepListFragment = new RecipeStepListFragment();
    @NonNull private RecipeStepDetailFragment stepDetailFragment = new RecipeStepDetailFragment();
    private final String BUNDLE_DETAIL_FRAGMENT_VISIBLE_KEY = "step_detail_visible";

    @BindView(R.id.activity_recipe_detail_navigation_drawer) DrawerLayout drawer;
    @BindView(R.id.activity_recipe_detail_navigation_drawer_view) RecyclerView drawerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        initialize(savedInstanceState);
    }

    private void initialize(Bundle savedInstanceState) {
        initializeVariables();
        initializeActionBar();
        initializeDrawer();

        if (savedInstanceState != null)
            loadFromSavedInstanceState(savedInstanceState);
        else
            initializeFragmentContainers();
    }

    private void initializeVariables() {
        drawerToggle = new MyDrawerToggle(this, drawer, R.string.content_description_drawer_open,
                R.string.content_description_drawer_close);
        isStepDetailFragmentVisible = false;
        actionBar = getSupportActionBar();
    }

    private void initializeActionBar() {
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setActionBarTitleToCurrentRecipe();
    }

    private void setActionBarTitleToCurrentRecipe() {
        Recipe currentRecipe = DataHelper.getInstance().getCurrentRecipeObject();
        if (actionBar != null && currentRecipe != null)
            actionBar.setTitle(currentRecipe.name);
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

    private void loadFromSavedInstanceState(@NonNull Bundle savedInstanceState) {
        isStepDetailFragmentVisible = savedInstanceState.getBoolean(BUNDLE_DETAIL_FRAGMENT_VISIBLE_KEY,
                false);
        getFragmentsFromContainers();
    }

    private void getFragmentsFromContainers() {
        if (User.isTablet(this)) {
            stepListFragment = (RecipeStepListFragment) getFragmentManager()
                    .findFragmentById(R.id.activity_recipe_detail_step_list_fragment_container);
            if (stepListFragment == null) stepListFragment = new RecipeStepListFragment();
            stepDetailFragment = (RecipeStepDetailFragment) getFragmentManager()
                    .findFragmentById(R.id.activity_recipe_detail_step_detail_fragment_container);
            if (stepDetailFragment == null) stepDetailFragment = new RecipeStepDetailFragment();
        } else {
            Fragment fragment = getFragmentManager().findFragmentById(R.id.activity_recipe_detail_fragment_container);
            if (fragment instanceof RecipeStepListFragment) stepListFragment = (RecipeStepListFragment) fragment;
            else stepDetailFragment = (RecipeStepDetailFragment) fragment;
        }
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
        DataHelper.getInstance().setCurrentRecipeIndex(recipeIndex);
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
            setActionBarTitle(getString(R.string.text_choose_recipe));
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
        stepListFragment.fadeOutRenewFadeIn();
    }

    private void clearStepDetailFragmentContainerIfTablet() {
        if (User.isTablet(this) && isStepDetailFragmentVisible) {
            getFragmentManager().beginTransaction().
                    setCustomAnimations(0, R.animator.fragment_fade_out)
                    .remove(stepDetailFragment).commit();
            isStepDetailFragmentVisible = false;
        }
    }

    private void setActionBarTitle(String title) {
        if (actionBar != null) actionBar.setTitle(title);
    }

    @Override
    public void onStepItemClick(int stepIndex) {
        DataHelper.getInstance().setCurrentStepIndex(stepIndex);
        if (User.isTablet(this))
            showStepDetailFragmentInRightPanel();
        else
            replaceStepListFragmentWithStepDetailFragment();
    }

    private void showStepDetailFragmentInRightPanel() {
        if (isStepDetailFragmentVisible)
            stepDetailFragment.slideLeftRenewSlideRightEnter();
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
        if (DataHelper.getInstance().hasPreviousStep()) {
            DataHelper.getInstance().moveToPreviousStep();
            stepDetailFragment.slideRightRenewSlideRightEnter();
        } else {
            showTryingToGoPreviousAnimation();
        }
    }

    private void showTryingToGoPreviousAnimation() {

    }

    @Override
    public void onNextButtonClicked() {
        if (DataHelper.getInstance().hasNextStep()) {
            DataHelper.getInstance().moveToNextStep();
            stepDetailFragment.slideLeftRenewSlideLeftEnter();
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
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(BUNDLE_DETAIL_FRAGMENT_VISIBLE_KEY, isStepDetailFragmentVisible);
        super.onSaveInstanceState(outState);
    }
}
