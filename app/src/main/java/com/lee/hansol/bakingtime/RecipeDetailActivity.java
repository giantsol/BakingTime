package com.lee.hansol.bakingtime;

import android.app.FragmentTransaction;
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
    private boolean isReplacingFragment;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerRecyclerViewAdapter drawerAdapter;
    @Nullable private RecipeStepListFragment stepListFragment;
    @Nullable private RecipeStepDetailFragment stepDetailFragment;
    private FragmentTransaction fragmentTransaction;
    private ActionBar actionBar;

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
        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.content_description_drawer_open, R.string.content_description_drawer_close) {
            private boolean isDrawerDeterminedOpen = false;

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

                if (isReplacingFragment) {
                    replaceStepListFragment();
                    isReplacingFragment = false;
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

        };
        setupFragmentTransaction();
    }

    private void setupFragmentTransaction() {
        fragmentTransaction = getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit,
                        R.animator.fragment_slide_right_enter, R.animator.fragment_lisde_right_exit);
    }

    private void setActionBarTitleToCurrentRecipe() {
        Recipe currentRecipe = DataHelper.getInstance().getCurrentRecipeObject();
        if (actionBar != null && currentRecipe != null)
            actionBar.setTitle(currentRecipe.name);
    }

    private void setActionBarTitle(String title) {
        if (actionBar != null) actionBar.setTitle(title);
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
        drawerAdapter = new DrawerRecyclerViewAdapter(this);
        drawerView.setAdapter(drawerAdapter);
    }

    private void initializeFragmentContainers() {
        stepListFragment = new RecipeStepListFragment();
        if (isTablet) initializeAsTabletLayout();
        else initializeAsNonTabletLayout();
    }

    private void initializeAsTabletLayout() {
        fragmentTransaction.add(R.id.activity_recipe_detail_step_list_fragment_container, stepListFragment)
                .commit();
        setupFragmentTransaction();
    }

    private void initializeAsNonTabletLayout() {
        fragmentTransaction.add(R.id.activity_recipe_detail_fragment_container, stepListFragment)
                .commit();
        setupFragmentTransaction();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(drawerView))
            drawer.closeDrawer(drawerView);
        else if (isIngredientsSliderOpen())
            closeIngredientsSlider();
        else if (!isTablet && (getFragmentManager().getBackStackEntryCount() >= 1)) {
            getFragmentManager().popBackStack();
        } else
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

    @Override
    public void onDrawerItemClick(int recipeIndex) {
        DataHelper.getInstance().setCurrentRecipeIndex(recipeIndex);
        closeDrawerAndReplaceStepListFragment();
    }

    private void closeDrawerAndReplaceStepListFragment() {
        isReplacingFragment = true;
        drawer.closeDrawer(drawerView);
    }

    private void replaceStepListFragment() {
        stepListFragment = new RecipeStepListFragment();
        if (isTablet)
            fragmentTransaction
                    .replace(R.id.activity_recipe_detail_step_list_fragment_container, stepListFragment)
                    .commit();
        else
            fragmentTransaction
                    .replace(R.id.activity_recipe_detail_fragment_container, stepListFragment)
                    .commit();

        setupFragmentTransaction();
        clearStepDetailFragmentContainerIfTablet();
    }

    private void clearStepDetailFragmentContainerIfTablet() {
        if (isTablet && (stepDetailFragment != null)) {
            fragmentTransaction.remove(stepDetailFragment).commit();
            stepDetailFragment = null;
            setupFragmentTransaction();
        }
    }

    @Override
    public void onStepItemClick(int stepIndex) {
        DataHelper.getInstance().setCurrentStepIndex(stepIndex);
        replaceStepDetailFragment();
    }

    private void replaceStepDetailFragment() {
        stepDetailFragment = new RecipeStepDetailFragment();
        if (isTablet) {
            fragmentTransaction.replace(R.id.activity_recipe_detail_step_detail_fragment_container, stepDetailFragment)
                    .commit();
        } else {
            fragmentTransaction.replace(R.id.activity_recipe_detail_fragment_container, stepDetailFragment)
                    .addToBackStack(null)
                    .commit();
        }
        setupFragmentTransaction();
    }

    @Override
    public void onPrevButtonClicked() {
        if (DataHelper.getInstance().hasPreviousStep()) {
            DataHelper.getInstance().moveToPreviousStep();
            replaceStepDetailFragment();
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
            replaceStepDetailFragment();
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

}
