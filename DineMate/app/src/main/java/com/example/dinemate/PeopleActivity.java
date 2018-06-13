package com.example.dinemate;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Button;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import android.widget.ListView;

class Person{
    String name;
    String sex;
    int age;
    int Id;
    Person(String name,String sex, int age, int Id){
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.Id = Id;
    }
}

class MyCustomAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Person> list;
    private Context context;

    MyCustomAdapter(ArrayList<Person> list, Context context) {
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
        return list.get(pos).Id;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.content_itemlist, null);
        }

        TextView user_sex= view.findViewById(R.id.sex);
        user_sex.setText(list.get(position).sex);

        TextView user_age= view.findViewById(R.id.age);
        user_age.setText( Integer.valueOf(list.get(position).age).toString() );

        final Button callbtn= (Button)view.findViewById(R.id.name);
        callbtn.setText(list.get(position).name);

        callbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Integer userId = list.get(position).Id;
                Intent profileIntent = new Intent(context, UserInfoActivity.class);
                profileIntent.putExtra("userId", userId);
                context.startActivity(profileIntent);
            }
        });

        return view;
    }
}


public class PeopleActivity extends AppCompatActivity {

    String my_name;
    String my_sex;
    int my_age;
    int my_id;
    ArrayList<Person> mArrData = new ArrayList<>();
    int dineMatesCounter=0;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);

        userId = getIntent().getIntExtra("userId", 0);

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
                ResultSet getDishResult = dbStatement.executeQuery(getDishSql);

                while (getDishResult.next()) {
                    dineMatesCounter++;
                    my_name = getDishResult.getString("nick");
                    my_age = getDishResult.getInt("age");
                    my_sex = getDishResult.getString("gender");
                    my_id = getDishResult.getInt("so_id");

                    mArrData.add(new Person(my_name,my_sex,my_age, my_id));
                }
                return (dineMatesCounter > 0);
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
                MyCustomAdapter adapter = new MyCustomAdapter(mArrData, PeopleActivity.this);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        protected void onCancelled() {}
    }
}
