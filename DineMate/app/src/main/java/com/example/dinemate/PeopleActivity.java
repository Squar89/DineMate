package com.example.dinemate;

import android.content.Context;
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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

class Person{
    String name;
    String sex;
    int age;
    Person(String name,String sex, int age){
        this.name = name;
        this.sex = sex;
        this.age = age;
    }
}

class MyCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Person> list;
    private Context context;

    public MyCustomAdapter(ArrayList<Person> list, Context context) {
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
            view = inflater.inflate(R.layout.content_itemlist, null);
        }

        //Handle TextView and display string from your list
        Button user_name= view.findViewById(R.id.name);

        user_name.setText(list.get(position).name);

        TextView user_sex= view.findViewById(R.id.sex);
        user_sex.setText(list.get(position).sex);

        TextView user_age= view.findViewById(R.id.age);


        user_age.setText( new Integer (list.get(position).age).toString() );

        //Handle buttons and add onClickListeners
//        Button callbtn= (Button)view.findViewById(R.id.btn);
//
//        callbtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v) {
//                do something
//
//            }
//        });

        return view;
    }
}


public class PeopleActivity extends AppCompatActivity {

    String my_name;
    String my_sex;
    int my_age;
    ArrayList<Person> mArrData = new ArrayList<>();
    int dineMatesCounter=0;

    ListView simpleList;
    int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        userId = getIntent().getIntExtra("Id", 0);

        GetDineMates getDineMates = new GetDineMates();
        getDineMates.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }

        return true;
    }

    public class GetDineMates extends AsyncTask<Void, Void, Boolean> {
        GetDineMates() {}

        @Override
        protected Boolean doInBackground(Void... params) {
            try (Connection dbConnection = AppUtils.getConnection()) {
                Statement dbStatement = dbConnection.createStatement();
                String getDishSql = String.format("SELECT * FROM daj_ziomkow(%s)", userId);
                Log.i("ziomek",getDishSql);
                ResultSet getDishResult = dbStatement.executeQuery(getDishSql);

                if (getDishResult.next()) {
                    dineMatesCounter++;
                    my_name = getDishResult.getString("nick");
                    my_age = getDishResult.getInt("age");
                    my_sex = getDishResult.getString("gender");

                    mArrData.add(new Person(my_name,my_sex,my_age));

                    return true;
                } else {
                    return false;
                }

            } catch (SQLException | URISyntaxException e) {
                Log.i("connection", e.toString());
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
//            TextView name_1 = findViewById(R.id.name_1);
//            name_1.setText(sname_1);
//            LinearLayout schowaj = findViewById(R.id.person1);
//            schowaj.setVisibility(View.GONE);
//            TextView sex_1 = findViewById(R.id.sex_1);
//            sex_1.setText(getDishResult.getInt("age"));
//            TextView age_1 = findViewById(R.id.age_1);
//            age_1.setText(getDishResult.getString("gender"));

            if( success) {
                ListView listView = findViewById(R.id.lista);
                // Set some data to array list

                // Initialize adapter and set adapter to list view
                MyCustomAdapter adapter = new MyCustomAdapter(mArrData, PeopleActivity.this);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {}
    }
}
