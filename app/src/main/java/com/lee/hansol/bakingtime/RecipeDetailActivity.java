package com.lee.hansol.bakingtime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lee.hansol.bakingtime.fragments.RecipeStepDetailFragment;
import com.lee.hansol.bakingtime.fragments.RecipeStepListFragment;
import com.lee.hansol.bakingtime.models.Recipe;

import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity {
    private Recipe recipe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);

        if (savedInstanceState != null) return;

        recipe = getIntent().getParcelableExtra(MainActivity.INTENT_EXTRA_RECIPE_OBJECT);

        if (findViewById(R.id.activity_recipe_detail_fragment_container) != null) {
            //small screen
            RecipeStepListFragment stepListFragment = RecipeStepListFragment.getInstance(recipe);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_recipe_detail_fragment_container, stepListFragment)
                    .commit();
        } else {
            //tablet screen
            RecipeStepListFragment stepListFragment = RecipeStepListFragment.getInstance(recipe);
            RecipeStepDetailFragment stepDetailFragment = new RecipeStepDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_recipe_detail_step_list_fragment_container, stepListFragment)
                    .add(R.id.activity_recipe_detail_step_detail_fragment_container, stepDetailFragment)
                    .commit();
        }
    }
}
