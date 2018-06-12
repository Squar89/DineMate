package com.example.dinemate;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

class Dish{
    String name;
    Integer rating;
    String dishId;
    String description;
    String ingredients;
    String image_url;
    Dish(String name,Integer rating, String dishId, String description,
         String ingredients, String image_url){
        this.name = name;
        this.rating = rating;
        this.dishId = dishId;
        this.description = description;
        this.ingredients = ingredients;
        this.image_url = image_url;
    }
}

class MyDishListAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Dish> list;
    private Context context;

    public MyDishListAdapter(ArrayList<Dish> list, Context context) {
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
        user_name.setImeOptions(position);

        TextView rating = view.findViewById(R.id.rate);
        rating.setText(list.get(position).rating.toString());

        /*
        user_name.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Integer userId = user_name.getImeOptions();
                Intent dishIntent = new Intent(context, UserInfoActivity.class);
                dishIntent.putExtra("dishId", userId);
                context.startActivity(profileIntent);
            }
        });
        */

        return view;
    }
}


public class RatedActivity extends AppCompatActivity {

    String my_name;
    String my_description;
    String my_url;
    String my_ingredients;
    int my_rating;
    String my_id;
    int userId;
    ArrayList<Dish> mArrData = new ArrayList<>();
    int dishesCounter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rated);

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
                Statement dbStatement = dbConnection.createStatement();
                String getDishSql = String.format("SELECT * FROM ratings r JOIN dishes d ON d.dish_id = " +
                        "r.dish_id where user_id = (%s) ", userId);
                ResultSet getDishResult = dbStatement.executeQuery(getDishSql);

                while (getDishResult.next()) {
                    dishesCounter++;
                    my_name = getDishResult.getString("name");
                    my_id = getDishResult.getString("dish_id");
                    my_rating = getDishResult.getInt("rate");
                    my_ingredients = getDishResult.getString("ingredients");
                    my_description = getDishResult.getString("directions");
                    my_url = getDishResult.getString("image_url");

                    mArrData.add(new Dish(my_name,my_rating,my_id,my_description,my_ingredients,my_url));
                }
                if(dishesCounter > 0 )
                    return true;
                return false;
            } catch (SQLException | URISyntaxException e) {
                Log.i("connection", e.toString());
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
