package com.example.dinemate;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class RecommendActivity extends BaseDrawerActivity {
    private int userId;
    private int recipeId;
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
        getRecipe = new PrepareRecipe(userId);
        getRecipe.execute();
    }

    private void sendRating(float rating) {
        updateRating = new UpdateRating(userId, recipeId, (int) rating);
        updateRating.execute();
    }

    /* Asynchronized task used to download a new recipe */
    public class PrepareRecipe extends AsyncTask<Void, Void, Boolean> {

        private final int mUserId;

        PrepareRecipe(int userId) {
            mUserId = userId;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            /* TODO */
            /* Połącz się z bazą danych, dostań rekomendacje i ustaw recipeId, obrazek nazwe itd */

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
