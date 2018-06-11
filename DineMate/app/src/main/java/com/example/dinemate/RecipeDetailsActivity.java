package com.example.dinemate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RecipeDetailsActivity extends AppCompatActivity {
    Bitmap icon = null;
    String recipeImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout toolbar_layout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        TextView description = (TextView)findViewById(R.id.description);
        TextView ingredients = (TextView)findViewById(R.id.ingredients);
        setSupportActionBar(toolbar);

        String dish_name = getIntent().getStringExtra("recipeName");
        String dish_description = getIntent().getStringExtra("recipeDirections");
        String dish_ingredients = getIntent().getStringExtra("recipeIngredients");
        recipeImageUrl = getIntent().getStringExtra("recipeImageUrl");
        GetImageFromUrl getImage = new GetImageFromUrl();
        getImage.execute();
        toolbar_layout.setTitle(dish_name);
        description.setText(dish_description);
        ingredients.setText(dish_ingredients);
    }

    public class GetImageFromUrl extends AsyncTask<Void, Void, Boolean> {
        private boolean everythingRated = false;
        GetImageFromUrl() {}

        @Override
        protected Boolean doInBackground(Void... params) {
            icon = null;
            try {

                InputStream inputStream = new URL(recipeImageUrl).openStream();
                icon = BitmapFactory.decodeStream(inputStream);
                return true;

            } catch ( IOException e) {
                Log.i("connection", e.toString());
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                AppBarLayout background = (AppBarLayout)findViewById(R.id.app_bar);
                Drawable image = new BitmapDrawable(getResources(), icon);
                background.setBackground(image);
                Log.i("penis","penis");
//                dishImage = findViewById(R.id.dish_image);
//                dishImage.setImageBitmap(icon);
            }
        }

        @Override
        protected void onCancelled() {}
    }
}
