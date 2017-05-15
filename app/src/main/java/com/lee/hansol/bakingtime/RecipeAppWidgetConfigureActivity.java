package com.lee.hansol.bakingtime;

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

import com.lee.hansol.bakingtime.helpers.DataHelper;
import com.lee.hansol.bakingtime.models.Recipe;
import com.lee.hansol.bakingtime.utils.DataUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.lee.hansol.bakingtime.utils.DataUtils.loadRecipesFromDb;
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
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        Recipe[] recipes = DataUtils.loadRecipesFromDb(this);
        WidgetConfigureRecipesAdapter adapter = new WidgetConfigureRecipesAdapter(this, recipes);
        listView.setAdapter(adapter);
    }

    private class WidgetConfigureRecipesAdapter extends ArrayAdapter<Recipe> {
        private WidgetConfigureRecipesAdapter(Context context, Recipe[] recipes) {
            super(context, R.layout.appwidget_configure_list_item, recipes);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Recipe recipe = getItem(position);
            ItemViewHolder holder;
            if (convertView == null) {
                holder = new ItemViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.appwidget_configure_list_item, parent, false);
                holder.textView = (TextView)convertView.findViewById(R.id.appwidget_configure_list_item_text);
                convertView.setTag(holder);
            } else {
                holder = (ItemViewHolder) convertView.getTag();
            }
            holder.textView.setText(recipe.name);
            holder.textView.setTag(recipe);
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Context context = RecipeAppWidgetConfigureActivity.this;
                    Recipe recipe = (Recipe) v.getTag();
                    toast(context, recipe.name);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                    RecipeAppWidgetProvider.updateAppWidget(context, appWidgetManager,
                            appWidgetId, recipe);

                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }
            });
            return convertView;
        }

        private class ItemViewHolder {
            TextView textView;
        }
    }
}
