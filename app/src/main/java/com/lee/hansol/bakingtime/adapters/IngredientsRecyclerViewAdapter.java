package com.lee.hansol.bakingtime.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.helpers.DataStorage;
import com.lee.hansol.bakingtime.models.Ingredient;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientsRecyclerViewAdapter
        extends RecyclerView.Adapter<IngredientsRecyclerViewAdapter.IngredientViewHolder> {
    private Context context;

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View holderView = LayoutInflater.from(context)
                .inflate(R.layout.ingredient_list_item, parent, false);
        return new IngredientViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        Ingredient ingredient = DataStorage.getInstance().getIngredientObjectAt(position);
        if (ingredient != null) {
            String text = String.format(Locale.getDefault(),
                    context.getString(R.string.text_ingredient_placeholder),
                    position + 1, ingredient.name, ingredient.quantity, ingredient.measureUnit);
            holder.textView.setText(text);
        }
    }

    @Override
    public int getItemCount() {
        return DataStorage.getInstance().getAllIngredients().length;
    }

    class IngredientViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ingredient_list_item_text) TextView textView;

        IngredientViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
