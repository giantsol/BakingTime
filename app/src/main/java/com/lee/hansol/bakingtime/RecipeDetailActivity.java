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
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.models.Step;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lee.hansol.bakingtime.MainActivity.INTENT_EXTRA_ALL_RECIPES;
import static com.lee.hansol.bakingtime.MainActivity.INTENT_EXTRA_RECIPE_INDEX;
import static com.lee.hansol.bakingtime.utils.ToastUtils.toast;

public class RecipeDetailActivity extends AppCompatActivity
        implements StepsRecyclerViewAdapter.OnStepItemClickListener,
        DrawerRecyclerViewAdapter.OnDrawerItemClickListener{
    private Recipe[] recipes;
    private int recipeIndex;
    private Recipe currentRecipe;
    private boolean isTablet;
    private boolean isReplacingFragment;
    private ActionBarDrawerToggle drawerToggle;
    private DrawerRecyclerViewAdapter drawerAdapter;
    private RecipeStepListFragment stepListFragment;
    private RecipeStepDetailFragment stepDetailFragment;
    private FragmentTransaction fragmentReplaceTransaction;
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
        setActionBarTitle(currentRecipe.name);
        initializeDrawer();

        if (savedInstanceState == null)
            initializeFragmentContainers();
    }

    private void initializeVariables() {
        recipes = getRecipesFromCallingIntent();
        recipeIndex = getRecipeIndexFromCallingIntent();
        currentRecipe = recipes[recipeIndex];
        actionBar = getSupportActionBar();
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
                if (slideOffset < 0.5) {
                    determineClose();
                }
            }

            private void determineClose() {
                isDrawerDeterminedOpen = false;
                setActionBarTitle(currentRecipe.name);

                if (isReplacingFragment) {
                    replaceStepListFragment();
                    isReplacingFragment = false;
                }
            }

            private void watchForOpening(float slideOffset) {
                if (slideOffset > 0.5) {
                    determineOpen();
                }
            }

            private void determineOpen() {
                isDrawerDeterminedOpen = true;
                setActionBarTitle(getString(R.string.text_choose_recipe));
            }
        };
        fragmentReplaceTransaction = getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit);
    }

    private Recipe[] getRecipesFromCallingIntent() {
        Object[] temp = getIntent().getParcelableArrayExtra(INTENT_EXTRA_ALL_RECIPES);
        return Arrays.copyOf(temp, temp.length, Recipe[].class);
    }

    private int getRecipeIndexFromCallingIntent() {
        return getIntent().getIntExtra(INTENT_EXTRA_RECIPE_INDEX, 0);
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
        drawerAdapter = new DrawerRecyclerViewAdapter(recipes, recipeIndex, this);
        drawerView.setAdapter(drawerAdapter);
    }

    private void initializeFragmentContainers() {
        isTablet = findViewById(R.id.activity_recipe_detail_step_list_fragment_container) != null;
        if (isTablet) initializeWithTabletLayout();
        else initializeWithNonTabletLayout();
    }

    private void initializeWithTabletLayout() {
        stepListFragment = RecipeStepListFragment.getInstance(currentRecipe);
        stepDetailFragment = new RecipeStepDetailFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.activity_recipe_detail_step_list_fragment_container, stepListFragment)
                .add(R.id.activity_recipe_detail_step_detail_fragment_container, stepDetailFragment)
                .commit();
    }

    private void initializeWithNonTabletLayout() {
        stepListFragment = RecipeStepListFragment.getInstance(currentRecipe);
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
        else
            super.onBackPressed();
    }

    private boolean isIngredientsSliderOpen() {
        if (stepListFragment == null) stepListFragment = getStepListFragmentFromContainer();
        return stepListFragment != null && stepListFragment.isSliderOpen;
    }

    private RecipeStepListFragment getStepListFragmentFromContainer() {
        if (isTablet)
            return (RecipeStepListFragment) getFragmentManager()
                    .findFragmentById(R.id.activity_recipe_detail_step_list_fragment_container);
        else
            return (RecipeStepListFragment) getFragmentManager()
                    .findFragmentById(R.id.activity_recipe_detail_fragment_container);
    }

    private void closeIngredientsSlider() {
        if (stepListFragment != null)
            stepListFragment.closeSlider();
    }

    @Override
    public void onDrawerItemClick(int recipeIndex) {
        this.recipeIndex = recipeIndex;
        currentRecipe = recipes[recipeIndex];
        closeDrawerAndReplaceStepListFragment();
    }

    private void closeDrawerAndReplaceStepListFragment() {
        isReplacingFragment = true;
        drawer.closeDrawer(drawerView);
    }

    private void replaceStepListFragment() {
        stepListFragment = RecipeStepListFragment.getInstance(currentRecipe);
        if (isTablet)
            fragmentReplaceTransaction
                    .replace(R.id.activity_recipe_detail_step_list_fragment_container, stepListFragment)
                    .commit();
        else
            fragmentReplaceTransaction
                    .replace(R.id.activity_recipe_detail_fragment_container, stepListFragment)
                    .commit();

        reloadFragmentReplaceTransactionObject();
    }

    private void reloadFragmentReplaceTransactionObject() {
        fragmentReplaceTransaction = getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_left_exit);
    }

    @Override
    public void onStepItemClick(Step step) {
        toast(this, step.shortDescription);
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
