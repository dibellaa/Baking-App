package com.example.adibella.bakinapp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.adibella.bakinapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepFragment extends Fragment {
    private static final String STEP_DESCRIPTION_KEY = "stepDescription";

    @BindView(R.id.step_instructions)
    TextView tvStepDescription;

    private String stepDescription;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            stepDescription = savedInstanceState.getString(STEP_DESCRIPTION_KEY);
        }
        View rootView = inflater.inflate(R.layout.step_description_layout, container, false);
        ButterKnife.bind(this, rootView);
        tvStepDescription.setText(stepDescription);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(STEP_DESCRIPTION_KEY, stepDescription);
    }

    public String getStepDescription() {
        return stepDescription;
    }

    public void setStepDescription(String stepDescription) {
        this.stepDescription = stepDescription;
    }
}
