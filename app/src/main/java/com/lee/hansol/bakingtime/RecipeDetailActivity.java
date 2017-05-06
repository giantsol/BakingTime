package com.lee.hansol.bakingtime;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.lee.hansol.bakingtime.fragments.RecipeStepDetailFragment;
import com.lee.hansol.bakingtime.fragments.RecipeStepListFragment;

import butterknife.ButterKnife;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        ButterKnife.bind(this);

        if (savedInstanceState != null) return;

        if (findViewById(R.id.activity_recipe_detail_fragment_container) != null) {
            //small screen
            RecipeStepListFragment stepListFragment = new RecipeStepListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_recipe_detail_fragment_container, stepListFragment)
                    .commit();
        } else {
            //tablet screen
            RecipeStepListFragment stepListFragment = new RecipeStepListFragment();
            RecipeStepDetailFragment stepDetailFragment = new RecipeStepDetailFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_recipe_detail_step_list_fragment_container, stepListFragment)
                    .add(R.id.activity_recipe_detail_step_detail_fragment_container, stepDetailFragment)
                    .commit();
        }
    }
}
