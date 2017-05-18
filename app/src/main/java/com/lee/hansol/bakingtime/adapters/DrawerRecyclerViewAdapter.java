package com.lee.hansol.bakingtime.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.helpers.DataStorage;
import com.lee.hansol.bakingtime.models.Recipe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DrawerRecyclerViewAdapter
        extends RecyclerView.Adapter<DrawerRecyclerViewAdapter.ItemViewHolder> {
    private Context context;
    private final OnDrawerItemClickListener drawerItemClickListener;

    public interface OnDrawerItemClickListener {
        void onDrawerItemClick(int recipeIndex);
    }

    public DrawerRecyclerViewAdapter(OnDrawerItemClickListener listener) {
        this.drawerItemClickListener = listener;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View holderView = LayoutInflater.from(context)
                .inflate(R.layout.drawer_list_item, parent, false);
        return new ItemViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Recipe recipe = DataStorage.getInstance().getRecipeObjectAt(position);
        if (DataStorage.getInstance().getCurrentRecipeObject() == recipe) {
            setCurrentRecipeView(holder, recipe);
        } else {
            setOtherRecipeView(holder, recipe);
        }
    }

    private void setCurrentRecipeView(ItemViewHolder holder, Recipe recipe) {
        holder.imageView.setImageResource(R.drawable.ic_assignment_ind_black_24dp);
        holder.textView.setText(recipe.name);
        holder.parent.setBackgroundResource(android.R.color.darker_gray);
        holder.parent.setClickable(false);
    }

    private void setOtherRecipeView(ItemViewHolder holder, Recipe recipe) {
        holder.imageView.setImageResource(R.drawable.ic_assignment_ind_black_24dp);
        holder.textView.setText(recipe.name);
        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = context.obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        holder.parent.setBackgroundResource(backgroundResource);
        typedArray.recycle();
        holder.parent.setClickable(true);
    }

    @Override
    public int getItemCount() {
        return DataStorage.getInstance().getAllRecipes().length;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.drawer_list_item_parent) ViewGroup parent;
        @BindView(R.id.drawer_list_item_imageview) ImageView imageView;
        @BindView(R.id.drawer_list_item_textview) TextView textView;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.drawer_list_item_parent)
        void onClick() {
            drawerItemClickListener.onDrawerItemClick(getAdapterPosition());
            notifyDataSetChanged();
        }
    }
}
