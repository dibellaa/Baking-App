package com.example.adibella.bakinapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.example.adibella.bakinapp.fragment.ExoPlayerFragment;
import com.example.adibella.bakinapp.fragment.RecipeFragment;
import com.example.adibella.bakinapp.fragment.StepFragment;
import com.example.adibella.bakinapp.model.Recipe;
import com.example.adibella.bakinapp.model.Step;
import com.example.adibella.bakinapp.provider.RecipeContract;
import com.example.adibella.bakinapp.widget.BakinAppWidgetProvider;

import java.util.List;

import timber.log.Timber;

public class RecipeActivity extends AppCompatActivity implements RecipeFragment.OnStepClickListener{
    public static final String STEP_SELECTED_KEY = "step_selected";

    private boolean twoPane;
    private Recipe recipe;
    private List<Step> steps;
    private boolean addedToWidget;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            recipe = savedInstanceState.getParcelable(MainActivity.RECIPE_KEY);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recipe = extras.getParcelable(MainActivity.RECIPE_KEY);
            if (recipe != null && !TextUtils.isEmpty(recipe.getName())) {
                getSupportActionBar().setTitle(recipe.getName());
            }
        }

        if (savedInstanceState == null) {
            RecipeFragment recipeFragment = new RecipeFragment();
            recipeFragment.setRecipe(recipe);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fl_recipe_activity, recipeFragment)
                    .commit();

        }

        twoPane = false;
        if(findViewById(R.id.fl_video_player) != null) {
            twoPane = true;
            if (savedInstanceState == null) {
                ExoPlayerFragment exoPlayerFragment = new ExoPlayerFragment();
                exoPlayerFragment.setMediaUrl(recipe.getSteps().get(0).getVideoURL());
                exoPlayerFragment.setThumbnailUrl(recipe.getSteps().get(0).getThumbnailURL());

                FragmentManager fragmentManager = getSupportFragmentManager();

                fragmentManager.beginTransaction()
                        .add(R.id.fl_video_player, exoPlayerFragment)
                        .commit();

                StepFragment stepFragment = new StepFragment();
                stepFragment.setStepDescription(recipe.getSteps().get(0).getDescription());

                fragmentManager.beginTransaction()
                        .add(R.id.step_instructions, stepFragment)
                        .commit();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.RECIPE_KEY, recipe);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if (item.getItemId() == R.id.menu_widget) {
            if (addedToWidget) {
                getContentResolver().delete(RecipeContract.RecipeEntry.CONTENT_URI,
                        RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + " = ? ",
                        new String[] {String.valueOf(recipe.getId())});
                item.setTitle(R.string.add_to_widget);
                addedToWidget = false;
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(RecipeContract.RecipeEntry.COLUMN_RECIPE_ID, recipe.getId());
                contentValues.put(RecipeContract.RecipeEntry.COLUMN_NAME, recipe.getName());
                contentValues.put(RecipeContract.RecipeEntry.COLUMN_INGREDIENTS, recipe.getIngredientsGson());
                contentValues.put(RecipeContract.RecipeEntry.COLUMN_IMAGE, recipe.getImage());
                getContentResolver().insert(RecipeContract.RecipeEntry.CONTENT_URI, contentValues);
                addedToWidget = true;
                item.setTitle(R.string.remove_from_widget);
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.widget_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_widget);
        Cursor cursor = getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI,
                null,
                RecipeContract.RecipeEntry.COLUMN_RECIPE_ID + " = ? ",
                new String[] {String.valueOf(recipe.getId())},
                null);
        if (cursor != null) {
            Timber.d(DatabaseUtils.dumpCursorToString(cursor));
            if (cursor.getCount() == 0) {
                addedToWidget = false;
                menuItem.setTitle(R.string.add_to_widget);
            } else {
                addedToWidget = true;
                menuItem.setTitle(R.string.remove_from_widget);
            }
            cursor.close();
        }
        return true;
    }

    @Override
    public void onStepSelected(int position) {
        steps = recipe.getSteps();
        Step step = steps.get(position);

        if (twoPane) {
            ExoPlayerFragment exoPlayerFragment = new ExoPlayerFragment();
            exoPlayerFragment.setMediaUrl(step.getVideoURL());
            exoPlayerFragment.setThumbnailUrl(step.getThumbnailURL());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_video_player, exoPlayerFragment)
                    .commit();

            StepFragment stepFragment = new StepFragment();
            stepFragment.setStepDescription(step.getDescription());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_instructions, stepFragment)
                    .commit();
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable(MainActivity.RECIPE_KEY, recipe);
            bundle.putInt(STEP_SELECTED_KEY, position);
            Intent intent = new Intent(this, StepDescriptionActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }
}
