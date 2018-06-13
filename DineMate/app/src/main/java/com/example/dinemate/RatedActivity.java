package com.example.dinemate;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ListView;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

class Dish{
    String name;
    Integer rating;
    String description;
    String ingredients;
    String image_url;
    Dish(String name,Integer rating, String description,
         String ingredients, String image_url){
        this.name = name;
        this.rating = rating;
        this.description = description;
        this.ingredients = ingredients;
        this.image_url = image_url;
    }
}

class MyDishListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Dish> list;
    private Context context;

    MyDishListAdapter(ArrayList<Dish> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
//        return list.get(pos).getId();
        //just return 0 if your list items do not have an Id variable.\
        return 0;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.content_rateddish_itemlist, null);
        }

        //Handle TextView and display string from your list
        final Button user_name= view.findViewById(R.id.name);
        user_name.setText(list.get(position).name);

        TextView rating = view.findViewById(R.id.rate);
        if(list.get(position).rating > 0)
            rating.setText(list.get(position).rating.toString());


        user_name.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent dishIntent = new Intent(context, RecipeDetailsActivity.class);
                dishIntent.putExtra("recipeName", list.get(position).name);
                dishIntent.putExtra("recipeDirections", list.get(position).description);
                dishIntent.putExtra("recipeIngredients", list.get(position).ingredients);
                dishIntent.putExtra("recipeImageUrl", list.get(position).image_url);

                context.startActivity(dishIntent);
            }
        });

        return view;
    }
}


public class RatedActivity extends AppCompatActivity {

    String my_name;
    String my_description;
    String my_url;
    String my_ingredients;
    int my_rating;
    int userId;
    ArrayList<Dish> mArrData = new ArrayList<>();
    int dishesCounter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rated);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userId = getIntent().getIntExtra("userId",0);

        GetDishes getDishes = new GetDishes();
        getDishes.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }

        return true;
    }

    public class GetDishes extends AsyncTask<Void, Void, Boolean> {
        GetDishes() {}

        @Override
        protected Boolean doInBackground(Void... params) {
            try (Connection dbConnection = AppUtils.getConnection()) {
                String getDishSql = "SELECT * FROM ratings r JOIN dishes d ON d.dish_id = " +
                        "r.dish_id where user_id = (?) ";
                PreparedStatement dbStatement = dbConnection.prepareStatement(getDishSql);

                dbStatement.setInt(1, userId);

                ResultSet getDishResult = dbStatement.executeQuery();
                while (getDishResult.next()) {
                    dishesCounter++;
                    my_name = getDishResult.getString("name");
                    my_rating = getDishResult.getInt("rate");
                    my_ingredients = getDishResult.getString("ingredients");
                    my_description = getDishResult.getString("directions");
                    my_url = getDishResult.getString("image_url");

                    mArrData.add(new Dish(my_name,my_rating,my_description,my_ingredients,my_url));
                }
                return (dishesCounter > 0);
            } catch (SQLException | URISyntaxException e) {
                Log.e("DB exception", e.toString());
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if( success) {
                ListView listView = findViewById(R.id.lista);
                // Set some data to array list

                // Initialize adapter and set adapter to list view
                MyDishListAdapter adapter = new MyDishListAdapter(mArrData, RatedActivity.this);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {}
    }
}
