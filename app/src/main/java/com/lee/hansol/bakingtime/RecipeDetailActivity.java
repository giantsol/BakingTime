package com.lee.hansol.bakingtime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lee.hansol.bakingtime.adapters.StepsRecyclerViewAdapter;
import com.lee.hansol.bakingtime.fragments.RecipeStepDetailFragment;
import com.lee.hansol.bakingtime.fragments.RecipeStepListFragment;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.models.Step;

import butterknife.ButterKnife;

import static com.lee.hansol.bakingtime.utils.ToastUtils.toast;

public class RecipeDetailActivity extends AppCompatActivity
        implements StepsRecyclerViewAdapter.OnStepItemClickListener {
    private Recipe recipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);

        if (savedInstanceState == null)
            initialize();
    }

    private void initialize() {
        boolean isTablet = findViewById(R.id.activity_recipe_detail_step_list_fragment_container) != null;
        recipe = getIntent().getParcelableExtra(MainActivity.INTENT_EXTRA_RECIPE_OBJECT);
        if (isTablet) setTabletLayout();
        else setNonTabletLayout();
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
