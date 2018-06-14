package com.example.adibella.bakinapp.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.adibella.bakinapp.MainActivity;
import com.example.adibella.bakinapp.R;
import com.example.adibella.bakinapp.adapter.RecipeListAdapter;
import com.example.adibella.bakinapp.model.Recipe;
import com.example.adibella.bakinapp.provider.RecipeContract;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class BakinAppWidgetConfigureActivity extends AppCompatActivity implements RecipeListAdapter.OnRecipeClickListener {
    private static final String PREFS_NAME = "com.example.bakinapp.BakinAppWidgetProvider";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private static final String[] MAIN_FAVORITE_MOVIES_PROJECTION = {
            RecipeContract.RecipeEntry.COLUMN_RECIPE_ID,
            RecipeContract.RecipeEntry.COLUMN_NAME,
            RecipeContract.RecipeEntry.COLUMN_INGREDIENTS,
            RecipeContract.RecipeEntry.COLUMN_IMAGE
    };

    private static final int INDEX_RECIPE_ID = 0;
    private static final int INDEX_NAME = 1;
    private static final int INDEX_INGREDIENTS = 2;
    private static final int INDEX_IMAGE = 3;

    private RecipeListAdapter recipeListAdapter;
    private List<Recipe> recipes;

    @BindView(R.id.widget_error_message)
    TextView errorMessage;

    @BindView(R.id.recipes_in_the_widget_label)
    TextView label;

    @BindView(R.id.recipes_list_view)
    RecyclerView recyclerView;

    public BakinAppWidgetConfigureActivity() {
        super();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.bakin_app_widget_provider_configure);
        ButterKnife.bind(this);

        loadLabel();

        GridLayoutManager layoutManager = new GridLayoutManager(this, MainActivity.numberOfColumns(this));
        recyclerView.setLayoutManager(layoutManager);
        recipeListAdapter = new RecipeListAdapter(R.layout.recipe_list_item,this, this, new ArrayList<Recipe>());
        recyclerView.setAdapter(recipeListAdapter);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        loadRecipes();
    }

    private void loadLabel() {
        errorMessage.setVisibility(View.INVISIBLE);
        label.setVisibility(View.VISIBLE);
    }

    private void loadRecipes() {
        if (recipes != null) {
            recipes.clear();
        } else {
            recipes = new ArrayList<>();
        }
        Cursor cursor = getContentResolver().query(RecipeContract.RecipeEntry.CONTENT_URI,
                MAIN_FAVORITE_MOVIES_PROJECTION,
                null,
                null,
                null);
        if (cursor != null) {
            Timber.d(DatabaseUtils.dumpCursorToString(cursor));
            if (cursor.getCount() > 0) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    Recipe recipe = new Recipe();
                    cursor.moveToPosition(i);
                    int id = cursor.getInt(INDEX_RECIPE_ID);
                    String name = cursor.getString(INDEX_NAME);
                    String ingredients = cursor.getString(INDEX_INGREDIENTS);
                    String image = cursor.getString(INDEX_IMAGE);
                    recipe.setId(id);
                    recipe.setName(name);
                    recipe.setIngredientsfromGson(ingredients);
                    recipe.setImage(image);
                    recipes.add(recipe);
                }
                recipeListAdapter.setRecipes(recipes);
            } else {
                loadErrorMessage();
            }
            cursor.close();
        }
        Timber.d("Cursor is null");
    }

    private void loadErrorMessage() {
        errorMessage.setVisibility(View.VISIBLE);
        label.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRecipeSelected(int position) {
        Recipe recipeSelected = recipes.get(position);
        final Context context = BakinAppWidgetConfigureActivity.this;

        String recipeWidgetText = recipeSelected.getName() + "\n" + recipeSelected.getIngredientsString();
        saveWidgetRecipe(context, widgetId, recipeWidgetText);
        
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        BakinAppWidgetProvider.updateAppWidget(context, appWidgetManager, widgetId);
        
        Intent result = new Intent();
        result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, result);
        finish();
    }

    public static void saveWidgetRecipe(Context context, int widgetId, String recipeWidget) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit();
        prefs.putString(PREF_PREFIX_KEY + widgetId, recipeWidget);
        prefs.apply();
    }

    public static String readWidgetRecipe(Context context, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String recipeText = prefs.getString(PREF_PREFIX_KEY + widgetId,
                context.getString(R.string.appwidget_text));
        return recipeText;
    }

    public static void deleteWidgetRecipe(Context context, int widgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        prefs.remove(PREF_PREFIX_KEY + widgetId);
        prefs.apply();
    }
}
