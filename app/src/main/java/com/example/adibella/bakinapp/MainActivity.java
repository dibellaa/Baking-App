package com.example.adibella.bakinapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

import com.example.adibella.bakinapp.adapter.RecipeListAdapter;
import com.example.adibella.bakinapp.model.Recipe;
import com.example.adibella.bakinapp.service.RecipeService;
import com.example.adibella.bakinapp.util.JSONUtils;
import com.example.adibella.bakinapp.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RecipeListAdapter.OnRecipeClickListener {
    @BindView(R.id.rv_recipes) RecyclerView recyclerView;
    public static final String RECIPE_KEY = "recipe";
    private RecipeListAdapter recipeAdapter;
    private RecipeService recipeService;
    private List<Recipe> recipes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recipeService = NetworkUtils.getRecipeService();

        ButterKnife.bind(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns(this));
        recyclerView.setLayoutManager(layoutManager);
        recipeAdapter = new RecipeListAdapter( R.layout.recipe_grid_item,this,this, new ArrayList<Recipe>());
        recyclerView.setAdapter(recipeAdapter);

        loadRecipes();
    }

    public static int numberOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int nColumns = (int)(dpWidth / scalingFactor);
        int defNbColumns = context.getResources().getInteger(R.integer.number_columns);
        if (nColumns < defNbColumns) return defNbColumns;
        return nColumns;
    }

    private void loadRecipes() {
        recipeService.getRecipes().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    recipes = response.body();
                    recipeAdapter.setRecipes(recipes);
                    Timber.d("Recipes from network: %s", recipes.toString());
                } else {
                    recipes = JSONUtils.getAllRecipesFromJsonFile(MainActivity.this);
                    recipeAdapter.setRecipes(recipes);
                    Timber.d("ERROR Recipes from JSON file: %s", recipes.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Timber.d("ERROR onFailure(): %s", t.getMessage());
                recipes = JSONUtils.getAllRecipesFromJsonFile(MainActivity.this);
                recipeAdapter.setRecipes(recipes);
            }
        });
    }

    @Override
    public void onRecipeSelected(int position) {
        Timber.d("selected Recipe: " + position);
        Recipe selectedRecipe = recipes.get(position);
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(RECIPE_KEY, selectedRecipe);
        startActivity(intent);
    }

}
