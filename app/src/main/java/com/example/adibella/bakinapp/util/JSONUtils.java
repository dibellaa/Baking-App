package com.example.adibella.bakinapp.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.Uri;
import android.widget.Toast;

import com.example.adibella.bakinapp.R;
import com.example.adibella.bakinapp.model.Ingredient;
import com.example.adibella.bakinapp.model.Recipe;
import com.example.adibella.bakinapp.model.Step;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSourceInputStream;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JSONUtils {
    public final static String ID_KEY = "id";
    public final static String SHORT_DESCRIPTION_KEY = "shortDescription";
    public final static String DESCRIPTION_KEY = "description";
    public final static String VIDEO_URL_KEY = "videoURL";
    public final static String THUMBNAIL_URL_KEY = "thumbnailURL";
    public final static String QUANTITY_KEY = "quantity";
    public final static String MEASURE_KEY = "measure";
    public final static String INGREDIENT_KEY = "ingredient";
    public final static String NAME_KEY = "name";
    public final static String INGREDIENTS_KEY = "ingredients";
    public final static String STEPS_KEY = "steps";
    public final static String SERVINGS_KEY = "servings";
    public final static String IMAGE_KEY = "image";

    public static ArrayList<Step> getStepsFromJsonArray(JSONArray stepsJsonArray) {
        ArrayList<Step> steps = new ArrayList<>();
        if (stepsJsonArray != null) {
            for (int i = 0; i < stepsJsonArray.length(); ++i) {
                Step step = null;
                try {
                    step = getStepFromJson(stepsJsonArray.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                steps.add(step);
            }
        }
        return steps;
    }

    private static Step getStepFromJson(JSONObject recipeJson) {
        Step step = new Step();
        step.setId(recipeJson.optInt(ID_KEY));
        step.setShortDescription(recipeJson.optString(SHORT_DESCRIPTION_KEY));
        step.setDescription(recipeJson.optString(DESCRIPTION_KEY));
        step.setVideoURL(recipeJson.optString(VIDEO_URL_KEY));
        step.setThumbnailURL(recipeJson.optString(THUMBNAIL_URL_KEY));
        return step;
    }

    public static ArrayList<Ingredient> getListIngredientsFromJsonArray(JSONArray ingredientsJsonArray) {
        ArrayList<Ingredient> ingredients = new ArrayList<>();
        if (ingredientsJsonArray != null) {
            for (int i = 0; i < ingredientsJsonArray.length(); ++i) {
                Ingredient ingredient = null;
                try {
                    ingredient = getIngredientFromJson(ingredientsJsonArray.getJSONObject(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ingredients.add(ingredient);
            }
        }
        return ingredients;
    }

    private static Ingredient getIngredientFromJson(JSONObject recipeJson) {
        Ingredient ingredient = new Ingredient();
        ingredient.setQuantity(recipeJson.optDouble(QUANTITY_KEY));
        ingredient.setMeasure(recipeJson.optString(MEASURE_KEY));
        ingredient.setIngredient(recipeJson.optString(INGREDIENT_KEY));
        return ingredient;
    }

    public static ArrayList<Recipe> getAllRecipesFromJsonFile(Context context){
        String json;
        ArrayList<Recipe> recipes = new ArrayList<>();
        try {
            json = readJSONFile(context);
            JSONArray recipesArray = new JSONArray(json);
            for (int i = 0; i < recipesArray.length(); i++) {
                Recipe recipe = getRecipeFromJson(recipesArray.getJSONObject(i));
                recipes.add(recipe);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recipes;
    }

    private static Recipe getRecipeFromJson(JSONObject recipeJsonObject) throws JSONException {
        Recipe recipe = new Recipe();
        recipe.setId(recipeJsonObject.optInt(ID_KEY));
        recipe.setName(recipeJsonObject.optString(NAME_KEY));
        recipe.setServings(recipeJsonObject.optInt(SERVINGS_KEY));
        recipe.setImage(recipeJsonObject.optString(IMAGE_KEY));
        recipe.setIngredients(getListIngredientsFromJsonArray(recipeJsonObject.getJSONArray(INGREDIENTS_KEY)));
        recipe.setSteps(getStepsFromJsonArray(recipeJsonObject.getJSONArray(STEPS_KEY)));

        return recipe;
    }

    private static String readJSONFile(Context context) throws IOException{
        AssetManager assetManager = context.getAssets();
        String uri = null;
        try {
            for (String asset : assetManager.list("")) {
                if(asset.equalsIgnoreCase("baking.json")) {
                    uri = "asset:///" + asset;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, R.string.recipe_list_load_failed, Toast.LENGTH_SHORT).show();
        }

        String userAgent = Util.getUserAgent(context, "BakinApp");
        DataSource dataSource = new DefaultDataSource(context, null, userAgent, false);
        DataSpec dataSpec = new DataSpec(Uri.parse(uri));
        InputStream inputStream = new DataSourceInputStream(dataSource, dataSpec);

        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String string;

        while ((string = bufferedReader.readLine()) != null) {
            stringBuilder.append(string);
        }
        bufferedReader.close();

        return stringBuilder.toString();
    }
}
