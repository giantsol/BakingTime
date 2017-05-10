package com.lee.hansol.bakingtime;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity
        implements StepsRecyclerViewAdapter.OnStepItemClickListener,
        DrawerRecyclerViewAdapter.OnDrawerItemClickListener,
        RecipeStepDetailFragment.OnPrevNextButtonClickListener {
    private boolean isTablet;
    private boolean isStepDetailShowingInNonTablet;
    private MyDrawerToggle drawerToggle;
    private ActionBar actionBar;
    @Nullable private RecipeStepListFragment stepListFragment;
    @Nullable private RecipeStepDetailFragment stepDetailFragment;

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
        setActionBarTitleToCurrentRecipe();
        initializeDrawer();

        if (savedInstanceState == null)
            initializeFragmentContainers();
    }

    private void initializeVariables() {
        actionBar = getSupportActionBar();
        isTablet = findViewById(R.id.activity_recipe_detail_step_list_fragment_container) != null;
        drawerToggle = new MyDrawerToggle(this, drawer, R.string.content_description_drawer_open,
                R.string.content_description_drawer_close);
    }

    private void setActionBarTitleToCurrentRecipe() {
        Recipe currentRecipe = DataHelper.getInstance().getCurrentRecipeObject();
        if (actionBar != null && currentRecipe != null)
            actionBar.setTitle(currentRecipe.name);
    }

    private void initializeDrawer() {
        setupActionBar();
        drawer.addDrawerListener(drawerToggle);
        initializeDrawerView();
    }

    private void setupActionBar() {
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    private void initializeDrawerView() {
        drawerView.setHasFixedSize(true);
        drawerView.setLayoutManager(new LinearLayoutManager(this));
        drawerView.setAdapter(new DrawerRecyclerViewAdapter(this));
    }

    private void initializeFragmentContainers() {
        stepListFragment = new RecipeStepListFragment();
        if (isTablet) initializeAsTabletLayout();
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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(drawerView))
            drawer.closeDrawer(drawerView);
        else if (isIngredientsSliderOpen())
            closeIngredientsSlider();
        else if (isStepDetailShowingInNonTablet)
            replaceStepDetailFragmentWithStepListFragment();
        else
            super.onBackPressed();
    }

    private boolean isIngredientsSliderOpen() {
        return stepListFragment != null && stepListFragment.isSliderOpen;
    }

    private void closeIngredientsSlider() {
        if (stepListFragment != null) {
            stepListFragment.closeSlider();
        }
    }

    private void replaceStepDetailFragmentWithStepListFragment() {
        stepListFragment = new RecipeStepListFragment();
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit)
                .replace(R.id.activity_recipe_detail_fragment_container, stepListFragment)
                .commit();
        isStepDetailShowingInNonTablet = false;
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        drawerToggle.setDrawerIndicatorEnabled(true);
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
        stepListFragment = new RecipeStepListFragment();
        if (isTablet)
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.fragment_fade_in, R.animator.fragment_fade_out)
                    .replace(R.id.activity_recipe_detail_step_list_fragment_container, stepListFragment)
                    .commit();
        else
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.fragment_fade_in, R.animator.fragment_fade_out)
                    .replace(R.id.activity_recipe_detail_fragment_container, stepListFragment)
                    .commit();
    }

    private void clearStepDetailFragmentContainerIfTablet() {
        if (isTablet && (stepDetailFragment != null)) {
            getFragmentManager().beginTransaction().
                    setCustomAnimations(0, R.animator.fragment_fade_out)
                    .remove(stepDetailFragment).commit();
            stepDetailFragment = null;
        }
    }

    private void setActionBarTitle(String title) {
        if (actionBar != null) actionBar.setTitle(title);
    }

    @Override
    public void onStepItemClick(int stepIndex) {
        DataHelper.getInstance().setCurrentStepIndex(stepIndex);
        if (isTablet)
            showStepDetailFragmentInRightPanel();
        else
            replaceStepListFragmentWithStepDetailFragment();
    }

    private void showStepDetailFragmentInRightPanel() {
        stepDetailFragment = new RecipeStepDetailFragment();
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_right_enter, R.animator.fragment_slide_left_exit)
                .replace(R.id.activity_recipe_detail_step_detail_fragment_container, stepDetailFragment)
                .commit();
    }

    private void replaceStepListFragmentWithStepDetailFragment() {
        stepDetailFragment = new RecipeStepDetailFragment();
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit)
                .replace(R.id.activity_recipe_detail_fragment_container, stepDetailFragment)
                .commit();
        isStepDetailShowingInNonTablet = true;
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawerToggle.setDrawerIndicatorEnabled(false);
    }

    @Override
    public void onPrevButtonClicked() {
        if (DataHelper.getInstance().hasPreviousStep()) {
            DataHelper.getInstance().moveToPreviousStep();
            renewStepDetailFragmentFadeInLeft();
        } else {
            showTryingToGoPreviousAnimation();
        }
    }

    private void renewStepDetailFragmentFadeInLeft() {
        stepDetailFragment = new RecipeStepDetailFragment();
        if (isTablet)
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit)
                    .replace(R.id.activity_recipe_detail_step_detail_fragment_container, stepDetailFragment)
                    .commit();
        else
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.fragment_slide_right_enter, R.animator.fragment_slide_right_exit)
                    .replace(R.id.activity_recipe_detail_fragment_container, stepDetailFragment)
                    .commit();
    }

    private void showTryingToGoPreviousAnimation() {

    }

    @Override
    public void onNextButtonClicked() {
        if (DataHelper.getInstance().hasNextStep()) {
            DataHelper.getInstance().moveToNextStep();
            renewStepDetailFragmentFadeInRight();
        } else {
            showTryingToGoNextAnimation();
        }
    }

    private void renewStepDetailFragmentFadeInRight() {
        stepDetailFragment = new RecipeStepDetailFragment();
        if (isTablet)
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit)
                    .replace(R.id.activity_recipe_detail_step_detail_fragment_container, stepDetailFragment)
                    .commit();
        else
            getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit)
                    .replace(R.id.activity_recipe_detail_fragment_container, stepDetailFragment)
                    .commit();
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

}
