package com.lee.hansol.bakingtime.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.models.Step;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeStepDetailFragment extends Fragment {
    private Unbinder unbinder;
    private Step step;

    public static RecipeStepDetailFragment getInstance(Step step) {
        RecipeStepDetailFragment fragment = new RecipeStepDetailFragment();
        fragment.step = step;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_step_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
