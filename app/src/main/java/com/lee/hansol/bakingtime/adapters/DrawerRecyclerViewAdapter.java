package com.lee.hansol.bakingtime.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.models.Ingredient;
import com.lee.hansol.bakingtime.models.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DrawerRecyclerViewAdapter
        extends RecyclerView.Adapter<DrawerRecyclerViewAdapter.ItemViewHolder> {
    private Context context;
    @NonNull private final Recipe[] recipes;

    public DrawerRecyclerViewAdapter(Context context, @NonNull Recipe[] recipes) {
        this.context = context;
        this.recipes = recipes;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View holderView = LayoutInflater.from(context)
                .inflate(R.layout.drawer_list_item, parent, false);
        return new ItemViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Recipe recipe = recipes[position];
        holder.imageView.setImageResource(R.drawable.ic_assignment_ind_black_24dp);
        holder.textView.setText(recipe.name);
    }

    @Override
    public int getItemCount() {
        return recipes.length;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.drawer_list_item_imageview) ImageView imageView;
        @BindView(R.id.drawer_list_item_textview) TextView textView;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
