package com.example.dinemate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * A register screen that offers register via simple form.
 */
public class RegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Keep track of the register task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private EditText mNicknameView;
    private EditText mAgeView;
    private EditText mContactView;
    private EditText mDescriptionView;
    private RadioGroup radioSexGroup;
    private RadioButton radioSexButton;

    private View mProgressView;
    private View mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupActionBar();
        // Set up the register form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mNicknameView = (EditText) findViewById(R.id.nickname);
        mAgeView = (EditText) findViewById(R.id.age);
        mContactView = (EditText) findViewById(R.id.contact_data);
        mDescriptionView = (EditText) findViewById(R.id.description);
        radioSexGroup = (RadioGroup) findViewById(R.id.radioSex);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mUsernameSignInButton = (Button) findViewById(R.id.username_sign_in_button);
        mUsernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Attempts to register the account specified by the register form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual register attempt is made.
     */
    private void attemptRegister() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mNicknameView.setError(null);
        mContactView.setError(null);
        mDescriptionView.setError(null);
        mAgeView.setError(null);

        // Store values at the time of the register attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String nickname = mNicknameView.getText().toString();
        String contact = mContactView.getText().toString();
        String description = mDescriptionView.getText().toString();
        String ages = mAgeView.getText().toString();
        int age = Integer.parseInt(ages);

        int selectedId = radioSexGroup.getCheckedRadioButtonId();
        radioSexButton = (RadioButton) findViewById(selectedId);
        String sex = radioSexButton.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (!isUsernameValid(username)) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid nickname.
        if (TextUtils.isEmpty(nickname)) {
            mNicknameView.setError(getString(R.string.error_field_required));
            focusView = mNicknameView;
            cancel = true;
        }

        // Check for a valid contact.
        if (TextUtils.isEmpty(contact)) {
            mContactView.setError(getString(R.string.error_field_required));
            focusView = mContactView;
            cancel = true;
        }

        // Check for a valid description.
        if (TextUtils.isEmpty(description)) {
            mDescriptionView.setError(getString(R.string.error_field_required));
            focusView = mDescriptionView;
            cancel = true;
        }

        // Check for a valid age.
        if (TextUtils.isEmpty(username)) {
            mAgeView.setError(getString(R.string.error_field_required));
            focusView = mAgeView;
            cancel = true;
        } else {
            if (!isAgeValid(age)) {
                mAgeView.setError(getString(R.string.error_incorrect_age));
                focusView = mAgeView;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt register and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user register attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(username, password, nickname, age, sex, contact, description);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isUsernameValid(String username) {
        return username.length() >= 3;
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isAgeValid(int age) {
        return age < 120 && age > 15;
    }

    /**
     * Shows the progress UI and hides the register form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> usernames = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            usernames.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addUsernamesToAutoComplete(usernames);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {}

    private void addUsernamesToAutoComplete(List<String> usernamesCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, usernamesCollection);

        mUsernameView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private static final String ERR_CON = "Can't connect to a server, try again later";
        private static final String ERR_USR = "WRONG";

        private final String mUsername;
        private final String mPassword;
        private final String mName;
        private final int mBirthYear;
        private final String mGender;
        private final String mContactForm;
        private final String mDescription;
        private String errorType;

        UserRegisterTask(String username, String password, String nickname, int age, String gender,
                      String contactForm, String description) {
            mUsername = username;
            mPassword = password;
            mName = nickname;
            mBirthYear = 2018 - age;
            mGender = gender;
            mContactForm = contactForm;
            mDescription = description;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Connection connection = AppUtils.getConnection();

                String query = "SELECT * FROM users WHERE login=?;";
                PreparedStatement stmt = connection.prepareStatement(query);

                stmt.setString(1, mUsername);

                ResultSet resultSet = stmt.executeQuery();
                if( resultSet.next() ) {
                    errorType = ERR_USR;
                    return false;
                }
            } catch (Exception e){
                Log.e("DB exception", e.toString());
                errorType = ERR_CON;
                return false;
            }

            try {
                Connection connection = AppUtils.getConnection();

                String query = "INSERT INTO users VALUES(DEFAULT,?,?,?,?,?,?,?);";
                PreparedStatement stmt = connection.prepareStatement(query);

                stmt.setString(1, mUsername);
                stmt.setString(2, mPassword);
                stmt.setString(3, mName);
                stmt.setInt(4, mBirthYear);
                stmt.setString(5, mGender);
                stmt.setString(6, mContactForm);
                stmt.setString(7, mDescription);

                stmt.execute();
                return true;
            } catch (Exception e){
                Log.e("BD exception", e.toString());
                errorType = ERR_CON;
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            }
            else if (errorType.equals(ERR_USR)) {
                mUsernameView.setError(getString(R.string.username_taken));
                mUsernameView.requestFocus();
            } else if (errorType.equals(ERR_CON)) {
                AppUtils.DisplayDialog(RegisterActivity.this, "Error",
                        "Can't connect to a server, try again later");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

