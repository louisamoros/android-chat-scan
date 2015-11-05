package com.scan.chat.android.androidchatscan.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.scan.chat.android.androidchatscan.R;
import com.scan.chat.android.androidchatscan.interfaces.UserRegisterInterface;
import com.scan.chat.android.androidchatscan.models.User;
import com.scan.chat.android.androidchatscan.tasks.UserRegisterTask;

import static android.widget.Toast.LENGTH_LONG;

public class RegisterActivity extends Activity implements UserRegisterInterface{

    private UserRegisterTask userRegisterTask = null;
    private UserRegisterInterface activityInterface;
    private String username;
    private String password;

    // UI references.
    public static View mLoginFormView;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private Button mRegisterButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Title with special font
        TextView textViewLoginTitle =(TextView)findViewById(R.id.text_view_register_title);
        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/kaushanscriptregular-font.otf");
        textViewLoginTitle.setTypeface(face);

        activityInterface = this;

        // Hide action bar
        ActionBar actionBar =getActionBar();
        actionBar.hide();

        // Set up the register form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordConfirmView = (EditText) findViewById(R.id.password_confirmation);
        mLoginFormView = findViewById(R.id.login_form);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_register);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.username || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptRegister() {
        // Cancel previous task if it is still running
        if (userRegisterTask != null && userRegisterTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            userRegisterTask.cancel(true);
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmView.setError(null);

        // Store values at the time of the login attempt.
        username = mUsernameView.getText().toString();
        password = mPasswordView.getText().toString();
        String passwordConfirm = mPasswordConfirmView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(passwordConfirm)) {
            mPasswordConfirmView.setError(getString(R.string.error_field_required));
            focusView = mPasswordConfirmView;
            cancel = true;
        }
        // Compare passwords
        else {
            if(!password.equals(passwordConfirm))
            {
                mPasswordConfirmView.setError(getString(R.string.error_no_match));
                focusView = mPasswordConfirmView;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            progressBar.setVisibility(View.VISIBLE);
            userRegisterTask = new UserRegisterTask(activityInterface);
            userRegisterTask.execute(username, password);
        }
    }


    @Override
    public void onRegisterSuccess(User newUser) {
        // Everything good!
        progressBar.setVisibility(View.GONE);
        Toast.makeText(RegisterActivity.this, R.string.register_success, LENGTH_LONG).show();

        // Declare activity switch intent
        Intent intent = new Intent(RegisterActivity.this, ChatActivity.class);

        // save username and password using a shared preference
        SharedPreferences sPrefs = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = sPrefs.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("auth", newUser.getEncodedBase64());
        editor.commit();

        // Start activity
        startActivity(intent);
        // we don't want the current activity to be in the backstack,
        finish();
    }

    @Override
    public void onRegisterFailure() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(RegisterActivity.this, R.string.register_error, LENGTH_LONG).show();
    }
}

