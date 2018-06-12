package com.example.dinemate;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;

public class UserInfoActivity extends AppCompatActivity {

    private int userId;
    private String name;
    private String gender;
    private String contact_data;
    private String description;
    private Integer age;

    private TextView nameView;
    private TextView genderView;
    private TextView contact_dataView;
    private TextView descriptionView;
    private TextView ageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userId = getIntent().getIntExtra("userId", 0);
        UserLoginTask userLoginTask = new UserLoginTask();
        userLoginTask.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }

        return true;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Connection connection = AppUtils.getConnection();
                Statement stmt = connection.createStatement();
                String query = "SELECT * FROM users WHERE user_id='" + userId + "';";
                ResultSet resultSet = stmt.executeQuery(query);

                if(resultSet.next()){
                    Log.i("maa","ceedenia");
                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    age = year - resultSet.getInt("year_of_birth");
                    name = resultSet.getString("name");
                    gender = resultSet.getString("sex");
                    contact_data = resultSet.getString("contact");
                    description = resultSet.getString("description");
                    return true;
                }

                return false;

            } catch (Exception e){
                Log.i("exception", e.toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                nameView = findViewById(R.id.username);
                ageView = findViewById(R.id.user_age);
                genderView = findViewById(R.id.user_sex);
                contact_dataView = findViewById(R.id.user_contact);
                descriptionView = findViewById(R.id.user_description);

                nameView.setText(name);
                ageView.append(age.toString());
                genderView.append(gender);
                contact_dataView.append(contact_data);
                descriptionView.append(description);

            } else {
                AppUtils.DisplayDialog(UserInfoActivity.this, "Error",
                        "Can't connect to a server, try again later");
            }
        }

        @Override
        protected void onCancelled() {
            AppUtils.DisplayDialog(UserInfoActivity.this, "Error",
                    "Can't connect to a server, try again later");
        }
    }
}
