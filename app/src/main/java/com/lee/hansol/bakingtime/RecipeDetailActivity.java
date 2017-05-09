package com.lee.hansol.bakingtime;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lee.hansol.bakingtime.adapters.DrawerRecyclerViewAdapter;
import com.lee.hansol.bakingtime.adapters.RecipesRecyclerViewAdapter;
import com.lee.hansol.bakingtime.adapters.StepsRecyclerViewAdapter;
import com.lee.hansol.bakingtime.fragments.RecipeStepDetailFragment;
import com.lee.hansol.bakingtime.fragments.RecipeStepListFragment;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.models.Step;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lee.hansol.bakingtime.MainActivity.INTENT_EXTRA_ALL_RECIPES;
import static com.lee.hansol.bakingtime.MainActivity.INTENT_EXTRA_RECIPE_INDEX;
import static com.lee.hansol.bakingtime.utils.LogUtils.log;
import static com.lee.hansol.bakingtime.utils.ToastUtils.toast;

public class RecipeDetailActivity extends AppCompatActivity
        implements StepsRecyclerViewAdapter.OnStepItemClickListener {
    private Recipe[] recipes;
    private int recipeIndex;
    private boolean isTablet;
    private ActionBarDrawerToggle drawerToggle;
    private RecipeStepListFragment stepListFragment;
    private RecipeStepDetailFragment stepDetailFragment;
    private DrawerRecyclerViewAdapter drawerAdapter;

    @BindView(R.id.activity_recipe_detail_navigation_drawer) DrawerLayout drawer;
    @BindView(R.id.activity_recipe_detail_navigation_drawer_view) RecyclerView drawerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);
        initialize();

        if (savedInstanceState == null)
            addFragments();
    }

    private void initialize() {
        Object[] temp = getIntent().getParcelableArrayExtra(INTENT_EXTRA_ALL_RECIPES);
        recipes = Arrays.copyOf(temp, temp.length, Recipe[].class);
        recipeIndex = getIntent().getIntExtra(INTENT_EXTRA_RECIPE_INDEX, 0);
        getSupportActionBar().setTitle(recipes[recipeIndex].name);
        initializeDrawer();
    }

    private void initializeDrawer() {
        drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.content_description_drawer_open, R.string.content_description_drawer_close) {
            Recipe recipe = recipes[recipeIndex];
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(recipe.name);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(R.string.text_choose_recipe);
            }
        };
        drawer.addDrawerListener(drawerToggle);
        initializeDrawerView();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void initializeDrawerView() {
        drawerView.setHasFixedSize(true);
        drawerView.setLayoutManager(new LinearLayoutManager(this));
        drawerAdapter = new DrawerRecyclerViewAdapter(this, recipes, recipeIndex);
        drawerView.setAdapter(drawerAdapter);
    }

    private void addFragments() {
        isTablet = findViewById(R.id.activity_recipe_detail_step_list_fragment_container) != null;
        if (isTablet) setTabletLayout();
        else setNonTabletLayout();
    }

    private void setTabletLayout() {
        stepListFragment = RecipeStepListFragment.getInstance(recipes[recipeIndex]);
        stepDetailFragment = new RecipeStepDetailFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_recipe_detail_step_list_fragment_container, stepListFragment)
                .add(R.id.activity_recipe_detail_step_detail_fragment_container, stepDetailFragment)
                .commit();
    }

    private void setNonTabletLayout() {
        stepListFragment = RecipeStepListFragment.getInstance(recipes[recipeIndex]);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_recipe_detail_fragment_container, stepListFragment)
                .commit();
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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(drawerView))
            drawer.closeDrawer(drawerView);
        else if (isSliderOpenFromStepListFragment()) {
            closeSliderFromStepListFragment();
        } else {
            super.onBackPressed();
        }
    }

    private boolean isSliderOpenFromStepListFragment() {
        if (stepListFragment == null) stepListFragment = getStepListFragmentFromContainer();
        return stepListFragment != null && stepListFragment.isSliderOpen;
    }

    private RecipeStepListFragment getStepListFragmentFromContainer() {
        if (isTablet)
            return (RecipeStepListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.activity_recipe_detail_step_list_fragment_container);
        else
            return (RecipeStepListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.activity_recipe_detail_fragment_container);
    }

    private void closeSliderFromStepListFragment() {
        if (stepListFragment != null)
            stepListFragment.closeSlider();
    }

    @Override
    public void onStepItemClick(Step step) {
        toast(this, step.shortDescription);
    }
}
