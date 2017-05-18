package com.lee.hansol.bakingtime.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.utils.DataUtils;
import com.lee.hansol.bakingtime.utils.PrefUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lee.hansol.bakingtime.utils.LogUtils.log;
import static com.lee.hansol.bakingtime.utils.ToastUtils.toast;

public class RecipeAppWidgetConfigureActivity extends Activity {
    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @BindView(R.id.appwidget_configure_listview) ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appwidget_configure);
        ButterKnife.bind(this);
        setResult(RESULT_CANCELED);

        appWidgetId = getWidgetIdFromIntent();
        if (isWidgetIdValid(appWidgetId))
            initialize();
        else
            finish();
    }

    private int getWidgetIdFromIntent() {
        Bundle extras = getIntent().getExtras();
        int id = AppWidgetManager.INVALID_APPWIDGET_ID;
        if (extras != null) {
            id = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        return id;
    }

    private boolean isWidgetIdValid(int widgetId) {
        return widgetId != AppWidgetManager.INVALID_APPWIDGET_ID;
    }

    private void initialize() {
        Recipe[] recipes = DataUtils.loadRecipesFromDb(this);
        WidgetConfigureRecipesAdapter adapter = new WidgetConfigureRecipesAdapter(this, recipes);
        listView.setAdapter(adapter);
    }

    private class WidgetConfigureRecipesAdapter extends ArrayAdapter<Recipe> implements View.OnClickListener {

        private WidgetConfigureRecipesAdapter(Context context, Recipe[] recipes) {
            super(context, R.layout.appwidget_configure_list_item, recipes);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Recipe recipe = getItem(position);
            if (convertView == null) convertView = createNewConvertView(parent);
            ItemViewHolder holder = getItemViewHolderFromConvertView(convertView);
            if (recipe != null) {
                holder.textView.setText(recipe.name);
                holder.textView.setTag(recipe);
                holder.textView.setOnClickListener(this);
            }
            return convertView;
        }

        @NonNull
        private View createNewConvertView(@NonNull ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            return inflater.inflate(R.layout.appwidget_configure_list_item, parent, false);
        }

        private ItemViewHolder getItemViewHolderFromConvertView(@NonNull View convertView) {
            if (convertView.getTag() != null) {
                return (ItemViewHolder) convertView.getTag();
            } else {
                ItemViewHolder holder = createNewViewHolderWith(convertView);
                convertView.setTag(holder);
                return holder;
            }
        }

        @NonNull
        private ItemViewHolder createNewViewHolderWith(@NonNull View convertView) {
            ItemViewHolder holder = new ItemViewHolder();
            holder.textView = (TextView) convertView.findViewById(R.id.appwidget_configure_list_item_text);
            return holder;
        }

        @Override
        public void onClick(View v) {
            Recipe recipe = (Recipe) v.getTag();
            final Context context = getContext();
            PrefUtils.putInt(context, appWidgetId, recipe.recipeId);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RecipeAppWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId);

            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }

        private class ItemViewHolder {
            TextView textView;
        }
    }
}
