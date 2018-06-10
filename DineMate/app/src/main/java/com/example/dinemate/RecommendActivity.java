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
import android.content.Context;

public class RecommendActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private int userId;
    private int recipeId;
    private PrepareRecipe getRecipe = null;
    private AppUtils utils = new AppUtils();

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /* TODO */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void yayAction(View view) {
        // TODO Do something when yay button is clicked
    }

    public void nayAction(View view) {
        prepareRecipe();
    }

    public void saveAction(View view) {
        // TODO Do something when save button is clicked
    }

    public void showRecipe(View view) {
        // TODO Show detailed recipe when image is clicked
    }

    public void updateRecipe() {
        // TODO Update shown recipe and all dependencies
    }

    public void prepareRecipe() {
        getRecipe = new PrepareRecipe(userId);
        getRecipe.execute();
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
                utils.DisplayDialog(getApplicationContext(), "Error", "Couldn't prepare new recipe, try again later");
                /* TODO */
            }
        }

        @Override
        protected void onCancelled() {
            utils.DisplayDialog(getApplicationContext(), "Error", "Couldn't prepare new recipe, try again later");
            /* TODO */
        }
    }
}
