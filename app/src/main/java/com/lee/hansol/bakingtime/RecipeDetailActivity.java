package com.lee.hansol.bakingtime;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.lee.hansol.bakingtime.adapters.StepsRecyclerViewAdapter;
import com.lee.hansol.bakingtime.fragments.RecipeStepDetailFragment;
import com.lee.hansol.bakingtime.fragments.RecipeStepListFragment;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.models.Step;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.id.toggle;
import static com.lee.hansol.bakingtime.utils.ToastUtils.toast;

public class RecipeDetailActivity extends AppCompatActivity
        implements StepsRecyclerViewAdapter.OnStepItemClickListener {
    private Recipe recipe;

    @BindView(R.id.activity_recipe_detail_navigation_drawer) DrawerLayout navigationDrawer;
    @BindView(R.id.activity_recipe_detail_navigation_drawer_view) ListView navigationDrawerView;

    private ActionBarDrawerToggle toggle;

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
        recipe = getIntent().getParcelableExtra(MainActivity.INTENT_EXTRA_RECIPE_OBJECT);

        navigationDrawerView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new String[]{"hello", "there"}));
        navigationDrawerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toast(RecipeDetailActivity.this, (String) parent.getItemAtPosition(position));
            }
        });

        toggle = new ActionBarDrawerToggle(this, navigationDrawer,
                R.string.content_description_drawer_open,
                R.string.content_description_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                getSupportActionBar().setTitle(recipe.name);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Choose recipe");
            }
        };
        navigationDrawer.addDrawerListener(toggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void addFragments() {
        boolean isTablet = findViewById(R.id.activity_recipe_detail_step_list_fragment_container) != null;
        if (isTablet) setTabletLayout();
        else setNonTabletLayout();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
          return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setTabletLayout() {
        RecipeStepListFragment stepListFragment = RecipeStepListFragment.getInstance(recipe);
        RecipeStepDetailFragment stepDetailFragment = new RecipeStepDetailFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_recipe_detail_step_list_fragment_container, stepListFragment)
                .add(R.id.activity_recipe_detail_step_detail_fragment_container, stepDetailFragment)
                .commit();
    }

    private void setNonTabletLayout() {
        RecipeStepListFragment stepListFragment = RecipeStepListFragment.getInstance(recipe);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_recipe_detail_fragment_container, stepListFragment)
                .commit();
    }

    @Override
    public void onStepItemClick(Step step) {
        toast(this, step.shortDescription);
    }
}
