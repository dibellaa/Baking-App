package com.example.adibella.bakinapp.adapter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;

import com.example.adibella.bakinapp.RecipeActivity;
import com.example.adibella.bakinapp.fragment.StepperFragment;
import com.stepstone.stepper.Step;
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter;

public class StepDescriptionAdapter extends AbstractFragmentStepAdapter {
    private final int stepsNb;

    public StepDescriptionAdapter(@NonNull FragmentManager fragmentManager, @NonNull Context context, int stepsNb) {
        super(fragmentManager, context);
        this.stepsNb = stepsNb;
    }

    @Override
    public Step createStep(int position) {
        final StepperFragment stepperFragment = new StepperFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(RecipeActivity.STEP_SELECTED_KEY, position);
        stepperFragment.setArguments(bundle);
        return stepperFragment;
    }

    @Override
    public int getCount() {
        return stepsNb;
    }
}
