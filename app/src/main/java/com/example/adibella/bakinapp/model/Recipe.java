package com.example.adibella.bakinapp.model;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.adibella.bakinapp.R;
import com.example.adibella.bakinapp.RecipeActivity;
import com.example.adibella.bakinapp.util.JSONUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class Recipe implements Parcelable{
    @SerializedName(JSONUtils.ID_KEY)
    @Expose
    private int id;
    @SerializedName(JSONUtils.NAME_KEY)
    @Expose
    private String name;
    @SerializedName(JSONUtils.INGREDIENTS_KEY)
    @Expose
    private List<Ingredient> ingredients;
    @SerializedName(JSONUtils.STEPS_KEY)
    @Expose
    private List<Step> steps;
    @SerializedName(JSONUtils.SERVINGS_KEY)
    @Expose
    private int servings;
    @SerializedName(JSONUtils.IMAGE_KEY)
    @Expose
    private String image;

    public Recipe() {

    }

    protected Recipe(Parcel in) {
        id = in.readInt();
        name = in.readString();
        ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        steps = in.createTypedArrayList(Step.CREATOR);
        servings = in.readInt();
        image = in.readString();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeTypedList(ingredients);
        dest.writeTypedList(steps);
        dest.writeInt(servings);
        dest.writeString(image);
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ingredients=" + ingredients +
                ", steps=" + steps +
                ", servings=" + servings +
                ", image='" + image + '\'' +
                '}';
    }

    public String getIngredientsString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Ingredients: \n");
        for(Ingredient ingredient: this.getIngredients()) {
            String delimiter = " ";
            strBuilder.append(" - ")
                    .append(ingredient.getIngredient())
                    .append(delimiter)
                    .append(String.valueOf(ingredient.getQuantity()))
                    .append(delimiter)
                    .append(ingredient.getMeasure())
                    .append("\n");
        }
        return strBuilder.toString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getIngredientsGson() {
        Gson gson = new Gson();
        return gson.toJson(ingredients);
    }

    public void setIngredientsfromGson(String ingredientsGson) {
        Gson gson = new Gson();
        ArrayList<Ingredient> ingredients = gson.fromJson(ingredientsGson,
                new TypeToken<ArrayList<Ingredient>>(){}.getType());
        this.ingredients = ingredients;
        Timber.d(this.getIngredientsString());
    }
}
