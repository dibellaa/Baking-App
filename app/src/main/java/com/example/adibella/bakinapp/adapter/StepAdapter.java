package com.example.adibella.bakinapp.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adibella.bakinapp.R;
import com.example.adibella.bakinapp.fragment.RecipeFragment;
import com.example.adibella.bakinapp.model.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {
    private List<Step> steps;
    private final RecipeFragment.OnStepClickListener onStepClickListener;

    public StepAdapter(RecipeFragment.OnStepClickListener clickListener) {
        this.onStepClickListener = clickListener;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.step_list_item, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        Step step = steps.get(position);
        holder.stepShortDescription.setText(step.getShortDescription());
    }

    @Override
    public int getItemCount() {
        if (steps != null) {
            return steps.size();
        }
        return 0;
    }

    class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_step_list_item)
        TextView stepShortDescription;

        public StepViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            onStepClickListener.onStepSelected(position);
        }
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
        notifyDataSetChanged();
    }
}
