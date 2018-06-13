package com.example.adibella.bakinapp.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.adibella.bakinapp.MainActivity;
import com.example.adibella.bakinapp.R;
import com.example.adibella.bakinapp.adapter.StepAdapter;
import com.example.adibella.bakinapp.model.Recipe;
import com.example.adibella.bakinapp.model.Step;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Step>> {
    private static final int STEPS_LOADER_ID = 101;
    @BindView(R.id.rv_recipe_steps)
    RecyclerView rvRecipeSteps;
    @BindView(R.id.tv_error_message)
    TextView errorMessage;
    @BindView(R.id.pb_loading_indicator)
    ProgressBar progressBar;
    @BindView(R.id.tv_recipe_ingredients)
    TextView recipeIngredients;

    private Recipe recipe;
    private StepAdapter stepAdapter;
    private OnStepClickListener onStepClickListener;

    public interface OnStepClickListener {
        void onStepSelected(int position);
    }

    public RecipeFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelable(MainActivity.RECIPE_KEY);
        }
        View rootView = inflater.inflate(R.layout.recipe_fragment, container, false);

        ButterKnife.bind(this, rootView);

        if (recipe != null) {
            recipeIngredients.setText(recipe.getIngredientsString());
        }

        rvRecipeSteps.setHasFixedSize(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvRecipeSteps.setLayoutManager(layoutManager);
        stepAdapter = new StepAdapter(onStepClickListener);
        rvRecipeSteps.setAdapter(stepAdapter);

        getActivity().getSupportLoaderManager().initLoader(STEPS_LOADER_ID, null, this);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            onStepClickListener = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnStepClickListener");
        }
    }

    public void setRecipe(Recipe recipe){
        this.recipe = recipe;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(MainActivity.RECIPE_KEY, recipe);
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<List<Step>> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<List<Step>>(getContext()) {
            private List<Step> steps;

            @Nullable
            @Override
            public List<Step> loadInBackground() {
                return recipe.getSteps();
            }

            @Override
            protected void onStartLoading() {
                if (steps != null) {
                    deliverResult(steps);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Override
            public void deliverResult(@Nullable List<Step> data) {
                steps = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Step>> loader, List<Step> data) {
        progressBar.setVisibility(View.INVISIBLE);
        stepAdapter.setSteps(data);
        if (data == null) {
            showErrorMessage();
        } else {
            showSteps();
        }
    }

    private void showSteps() {
        rvRecipeSteps.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
    }

    private void showErrorMessage() {
        rvRecipeSteps.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Step>> loader) {

    }
}
