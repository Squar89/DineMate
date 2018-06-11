package com.example.dinemate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RecommendActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private int userId;
    private int recipeId;
    private String recipeName;
    private String recipeIngredients;
    private String recipeDirections;
    private String recipeImageUrl;
    private String recipePublisherUrl;
    private PrepareRecipe getRecipe = null;
    private RatingBar ratingBar;
    private UpdateRating updateRating = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "*TODO*", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

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
            finish();
            startActivity(authenticateAgain);
        }

        addListenerOnRatingBar();
        prepareRecipe();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.recommend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settingsIntent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /* TODO populate drawer navigation */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void addListenerOnRatingBar() {

        ratingBar = findViewById(R.id.rating);

        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
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
        Intent showDetailsIntent = new Intent(getApplicationContext(), RecipeDetails.class);
        showDetailsIntent.putExtra("userId", userId);
        showDetailsIntent.putExtra("recipeId", recipeId);
        // TODO put some more extras (whatever is necessary)
        startActivity(showDetailsIntent);
    }

    public void updateRecipe() {
        // TODO Update shown recipe and all dependencies
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
        PrepareRecipe() {}

        @Override
        protected Boolean doInBackground(Void... params) {
            try (Connection dbConnection = AppUtils.getConnection()) {
                Statement dbStatement = dbConnection.createStatement();
                String getDishSql = String.format("SELECT * FROM " +
                        "((SELECT * FROM dishes EXCEPT (SELECT dishes.* FROM ratings INNER JOIN dishes ON ratings.dish_id = dishes.dish_id WHERE ratings.user_id = %s)) " +
                        "UNION " +
                        "(SELECT dishes.* FROM ratings INNER JOIN dishes ON ratings.dish_id = dishes.dish_id WHERE ratings.user_id = %s AND ratings.rate IS NULL AND ratings.date < NOW() - INTERVAL '7 days')) AS dishes " +
                        "ORDER BY RANDOM() LIMIT 1", userId, userId);
                ResultSet getDishResult = dbStatement.executeQuery(getDishSql);
                if (getDishResult.next())
                {
                    recipeId = getDishResult.getInt("dish_id");
                    recipeName = getDishResult.getString("name");
                    recipeIngredients = getDishResult.getString("ingredients");
                    recipeDirections = getDishResult.getString("directions");
                    recipeImageUrl = getDishResult.getString("image_url");
                    recipePublisherUrl = getDishResult.getString("publisher_url");
                }
                else
                {
                    AppUtils.DisplayDialog(RecommendActivity.this, "Error",
                            "There are no unrated recipes right now. Maybe check out ones you already rated? :)");
                }
                return true;
            } catch (SQLException | URISyntaxException e) {
                Log.i("connection", e.toString());
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                updateRecipe();
            } else {
                AppUtils.DisplayDialog(RecommendActivity.this, "Error",
                        "Couldn't prepare new recipe, try again later");
            }
        }

        @Override
        protected void onCancelled() {
            AppUtils.DisplayDialog(RecommendActivity.this, "Error",
                    "Couldn't prepare new recipe, try again later");
        }
    }

    /* Asynchronized task used to upload rating to database */
    public class UpdateRating extends AsyncTask<Void, Void, Boolean> {

        private final int mUserId;
        private final int mRecipeId;
        private final int mRating;

        UpdateRating(int userId, int recipeId, int rating) {
            mUserId = userId;
            mRecipeId = recipeId;
            mRating = rating;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            /* TODO przeslij ocene do db (lub update)*/

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
        protected void onCancelled() {
            AppUtils.DisplayDialog(RecommendActivity.this, "Error",
                    "Couldn't upload rating, try again later");
        }
    }
}
