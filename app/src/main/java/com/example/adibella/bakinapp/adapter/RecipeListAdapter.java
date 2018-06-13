package com.example.adibella.bakinapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.adibella.bakinapp.R;
import com.example.adibella.bakinapp.model.Recipe;
import com.example.adibella.bakinapp.util.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.Utils;

public class RecipeListAdapter extends RecyclerView.Adapter<RecipeListAdapter.ViewHolder> {
    private final int layoutResourceId;
    private final Context context;
    private final OnRecipeClickListener clickListener;

    private List<Recipe> recipes;

    public RecipeListAdapter(int layoutResourceId, Context context, OnRecipeClickListener clickListener, List<Recipe> recipes) {
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.clickListener = clickListener;
        this.recipes = recipes;
    }

    public interface OnRecipeClickListener {
        void onRecipeSelected(int position);
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecipeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View rootView = layoutInflater.inflate(layoutResourceId, parent, false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecipeListAdapter.ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.recipeName.setText(recipe.getName());
        if (!TextUtils.isEmpty(recipe.getImage())) {
            Picasso.with(context)
                    .load(recipe.getImage())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.default_image)
                    .into(holder.recipeImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            NetworkUtils.checkConnection(holder.recipeImage.getContext());
                        }
                    });
        } else {
            Picasso.with(context)
                    .load(R.drawable.default_image)
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.default_image)
                    .into(holder.recipeImage);
        }
    }

    @Override
    public int getItemCount() {
        if (recipes != null) {
            return recipes.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_recipe_image)
        ImageView recipeImage;
        @BindView(R.id.tv_recipe_name)
        TextView recipeName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            recipeImage.setOnClickListener(this);
            recipeName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            clickListener.onRecipeSelected(position);
        }
    }
}
