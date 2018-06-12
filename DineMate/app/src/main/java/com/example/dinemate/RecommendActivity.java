package com.example.dinemate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.TimeUnit;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RecommendActivity extends BaseDrawerActivity {

    private int userId;
    private String recipeId;
    private String recipeName;
    private String recipeIngredients;
    private String recipeDirections;
    private String recipeImageUrl;
    private String recipePublisherUrl;
    private PrepareRecipe getRecipe = null;
    private RatingBar ratingBar;
    private UpdateRating updateRating = null;
    private TextView dishName;
    private ImageView dishImage;
    private boolean prepareRecipeSucces = true;
    private Bitmap icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* Authenticate User */
        userId = getIntent().getIntExtra("userId", -1);
        /* User was not authenticated, go back to login activity */
        if (userId == -1) {
            Intent authenticateAgain = new Intent(getApplicationContext(), LoginActivity.class);
            Log.i("ouch", "Why are u running?");
            startActivity(authenticateAgain);
            finish();
        }

        addListenerOnRatingBar();
        prepareRecipe();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base_drawer, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_saved) {
            //Intent savedIntent = new Intent(getApplicationContext(), RatedActivity.class);
            //putextra
            //startActivity(savedIntent);
        } else if (id == R.id.nav_mates) {
            Intent matesIntent = new Intent(getApplicationContext(), PeopleActivity.class);
            matesIntent.putExtra("userId", userId);
            startActivity(matesIntent);
        } else if (id == R.id.nav_profile) {
            Intent profileIntent = new Intent(getApplicationContext(), UserInfoActivity.class);
            profileIntent.putExtra("userId", userId);
            startActivity(profileIntent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addListenerOnRatingBar() {

        ratingBar = findViewById(R.id.rating);

        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                if(rating != 0)
                    sendRating(rating);
            }
        });
    }

    public void skipAction(View view) {
        prepareRecipe();
    }

    public void saveAction(View view) {
        sendRating(0);
    }

    public void showRecipe(View view) {
        Intent showDetailsIntent = new Intent(getApplicationContext(), RecipeDetailsActivity.class);
        showDetailsIntent.putExtra("userId", userId);
        showDetailsIntent.putExtra("recipeId", recipeId);
        showDetailsIntent.putExtra("recipeName", recipeName);
        showDetailsIntent.putExtra("recipeIngredients", recipeIngredients);
        showDetailsIntent.putExtra("recipeDirections", recipeDirections);
        showDetailsIntent.putExtra("recipeImageUrl", recipeImageUrl);
        showDetailsIntent.putExtra("recipePublisherUrl", recipePublisherUrl);
        startActivity(showDetailsIntent);
    }

    public void updateRecipe() {
        try {
            dishName = findViewById(R.id.dish_name);
            dishName.setText(recipeName);


            Log.i("OBRAZEK:",recipeImageUrl);
            dishImage = findViewById(R.id.dish_image);
            dishImage.setImageBitmap(icon);

            Log.i("OBRAZEK2:",recipeImageUrl);
            ratingBar.setRating(0);

        } catch ( Exception e) {

            Log.i("wyjatek",e.toString());
            // TODO blad
        }

    }

    public void prepareRecipe() {
        getRecipe = new PrepareRecipe();
        getRecipe.execute();
    }

    private void sendRating(float rating) {
        updateRating = new UpdateRating(userId, recipeId, (int) rating);
        updateRating.execute();
    }

    /* Asynchronized task used to download a new recipe */
    public class PrepareRecipe extends AsyncTask<Void, Void, Boolean> {
        private boolean everythingRated = false;
        PrepareRecipe() {}

        @Override
        protected Boolean doInBackground(Void... params) {
            prepareRecipeSucces = false;
            icon = null;
            try (Connection dbConnection = AppUtils.getConnection()) {
                Statement dbStatement = dbConnection.createStatement();
                String getDishSql = String.format("SELECT * FROM " +
                        "((SELECT * FROM dishes EXCEPT (SELECT dishes.* FROM ratings INNER JOIN dishes ON ratings.dish_id = dishes.dish_id WHERE ratings.user_id = %s)) " +
                        "UNION " +
                        "(SELECT dishes.* FROM ratings INNER JOIN dishes ON ratings.dish_id = dishes.dish_id WHERE ratings.user_id = %s AND ratings.rate = 0 AND ratings.date < NOW() - INTERVAL '7 days')) AS dishes " +
                        "ORDER BY RANDOM() LIMIT 1", userId, userId);
                ResultSet getDishResult = dbStatement.executeQuery(getDishSql);

                if (getDishResult.next()) {
                    recipeId = getDishResult.getString("dish_id");
                    recipeName = getDishResult.getString("name");
                    recipeIngredients = getDishResult.getString("ingredients");
                    recipeDirections = getDishResult.getString("directions");
                    recipeImageUrl = getDishResult.getString("image_url");
                    recipePublisherUrl = getDishResult.getString("publisher_url");
                    prepareRecipeSucces = true;
                    InputStream inputStream = new URL(recipeImageUrl).openStream();
                    icon = BitmapFactory.decodeStream(inputStream);
                    return true;
                }
                else {
                    everythingRated = true;
                    return false;
                }

            } catch (SQLException | URISyntaxException | IOException e) {
                Log.i("connection", e.toString());
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                updateRecipe();
            } else if (everythingRated) {
                AppUtils.DisplayDialog(RecommendActivity.this, "Error",
                        "There are no unrated recipes right now. Maybe check out ones you already rated? :)");
            } else {
                AppUtils.DisplayDialog(RecommendActivity.this, "Error",
                        "Couldn't prepare new recipe, try again later");
            }
        }

        @Override
        protected void onCancelled() {}
    }

    /* Asynchronized task used to upload rating to database */
    public class UpdateRating extends AsyncTask<Void, Void, Boolean> {

        private final int mUserId;
        private final String mRecipeId;
        private final int mRating;

        UpdateRating(int userId, String recipeId, int rating) {
            mUserId = userId;
            mRecipeId = recipeId;
            mRating = rating;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try (Connection dbConnection = AppUtils.getConnection()) {
                Statement dbStatement = dbConnection.createStatement();
                String upsertRatingSql = String.format("INSERT INTO ratings (user_id, dish_id, rate) " +
                        "VALUES (%d, '%s', %d) " +
                        "ON CONFLICT (user_id, dish_id) DO UPDATE " +
                        "  SET rate = excluded.rate, " +
                        "      date = NOW()", mUserId, mRecipeId, mRating);
                dbStatement.executeUpdate(upsertRatingSql);
                return true;
            } catch (SQLException | URISyntaxException e) {
                Log.i("connection", e.toString());
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                prepareRecipe();
            } else {
                AppUtils.DisplayDialog(RecommendActivity.this, "Error",
                        "Couldn't upload rating, try again later");
            }
        }

        @Override
        protected void onCancelled() {}
    }
}
