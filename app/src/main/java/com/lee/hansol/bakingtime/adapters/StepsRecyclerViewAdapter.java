package com.lee.hansol.bakingtime.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lee.hansol.bakingtime.R;
import com.lee.hansol.bakingtime.helpers.DataHelper;
import com.lee.hansol.bakingtime.models.Step;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.lee.hansol.bakingtime.utils.LogUtils.log;

public class StepsRecyclerViewAdapter
        extends RecyclerView.Adapter<StepsRecyclerViewAdapter.StepViewHolder> {
    private Context context;

    private final OnStepItemClickListener stepItemClickListener;

    public interface OnStepItemClickListener {
        void onStepItemClick(int stepIndex);
    }

    public StepsRecyclerViewAdapter(OnStepItemClickListener stepItemClickListener) {
        this.stepItemClickListener = stepItemClickListener;
    }

    @Override
    public StepViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View holderView = LayoutInflater.from(context).inflate(R.layout.step_list_item, parent, false);
        return new StepViewHolder(holderView);
    }

    @Override
    public void onBindViewHolder(StepViewHolder holder, int position) {
        Step step = DataHelper.getInstance().getStepObjectAt(position);
        if (step != null) {
            String text = String.format(Locale.getDefault(),
                    context.getString(R.string.text_step_placeholder),
                    step.stepOrder, step.shortDescription);
            holder.textView.setText(text);
        }
    }

    @Override
    public int getItemCount() {
        return DataHelper.getInstance().getAllSteps().length;
    }

    class StepViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.step_list_item_text) TextView textView;

        StepViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.step_list_item_text)
        void onClick() {
            stepItemClickListener.onStepItemClick(getAdapterPosition());
        }
    }
}
