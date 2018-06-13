package com.example.adibella.bakinapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.adibella.bakinapp.adapter.StepDescriptionAdapter;
import com.example.adibella.bakinapp.fragment.ExoPlayerFragment;
import com.example.adibella.bakinapp.fragment.StepFragment;
import com.example.adibella.bakinapp.model.Recipe;
import com.example.adibella.bakinapp.model.Step;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import java.util.List;

public class StepDescriptionActivity extends AppCompatActivity implements StepperLayout.StepperListener {
    private String mediaUrl;
    private String thumbnailUrl;
    private String description;
    private Recipe recipe;
    private List<Step> steps;
    private int stepIdx;
    private boolean twoPane;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_description);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recipe = extras.getParcelable(MainActivity.RECIPE_KEY);
            stepIdx = extras.getInt(RecipeActivity.STEP_SELECTED_KEY);
            steps = recipe.getSteps();
            Step step = steps.get(stepIdx);
            mediaUrl = step.getVideoURL();
            thumbnailUrl = step.getThumbnailURL();
            description = step.getDescription();
            getSupportActionBar().setTitle(recipe.getName());
        }

        if (savedInstanceState == null) {
            ExoPlayerFragment exoPlayerFragment = new ExoPlayerFragment();
            exoPlayerFragment.setMediaUrl(mediaUrl);
            exoPlayerFragment.setThumbnailUrl(thumbnailUrl);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fl_video_player, exoPlayerFragment)
                    .commit();
        }

        twoPane = false;
        if (findViewById(R.id.step_instructions) != null) {
            twoPane = true;
            if (savedInstanceState == null) {
                StepFragment stepFragment = new StepFragment();
                stepFragment.setStepDescription(description);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .add(R.id.step_instructions, stepFragment)
                        .commit();
            }
            StepperLayout stepperLayout = findViewById(R.id.stepper_layout);
            StepDescriptionAdapter stepDescriptionAdapter = new StepDescriptionAdapter(getSupportFragmentManager(), this, recipe.getSteps().size());
            stepperLayout.setAdapter(stepDescriptionAdapter);
            stepperLayout.setListener(this);
            stepperLayout.setCurrentStepPosition(stepIdx);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCompleted(View completeButton) {
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onError(VerificationError verificationError) {

    }

    @Override
    public void onStepSelected(int newStepPosition) {
        if (newStepPosition != stepIdx) {
            stepIdx = newStepPosition;
            steps = recipe.getSteps();
            Step step = steps.get(stepIdx);
            mediaUrl = step.getVideoURL();
            thumbnailUrl = step.getThumbnailURL();
            description = step.getDescription();

            ExoPlayerFragment exoPlayerFragment = new ExoPlayerFragment();
            exoPlayerFragment.setMediaUrl(step.getVideoURL());
            exoPlayerFragment.setThumbnailUrl(step.getThumbnailURL());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_video_player, exoPlayerFragment)
                    .commit();

            if (twoPane) {
                StepFragment stepFragment = new StepFragment();
                stepFragment.setStepDescription(step.getDescription());

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.step_instructions, stepFragment)
                        .commit();
            }
        }
    }

    @Override
    public void onReturn() {

    }
}
