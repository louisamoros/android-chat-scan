package com.scan.chat.android.androidchatscan.Activities;

import android.app.Activity;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.scan.chat.android.androidchatscan.R;
import com.scan.chat.android.androidchatscan.Tasks.UserLoginTask;


public class MainActivity extends Activity  /*implements LoaderCallbacks<Cursor>*/ {

    public static final String API_BASE_URL = "http://training.loicortola.com/chat-rest/2.0";
    public static final String PREFS_NAME = "MyPrefsFile";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    public static Activity la;

    // UI references.
    public static EditText mPasswordView;
    public static View mProgressView;
    public static View mLoginFormView;
    private AutoCompleteTextView nUsernameView;
    private Button mUsernameSignInButton;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        la = this;

        //first check if there is a user already connected
        //in this case, we can directly go to chat activity
        SharedPreferences sPrefs = getSharedPreferences(PREFS_NAME, 0);
        if ((sPrefs.contains("username") && sPrefs.contains("password") && sPrefs.contains("auth"))) {
            // Declare activity switch intent
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            // Start activity
            startActivity(intent);
        }


        // Title with special font
        TextView textViewLoginTitle =(TextView)findViewById(R.id.text_view_login_title);
        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/kaushanscriptregular-font.otf");
        textViewLoginTitle.setTypeface(face);

        // Set up the login form.
        nUsernameView = (AutoCompleteTextView) findViewById(R.id.username);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(nUsernameView, InputMethodManager.SHOW_IMPLICIT);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        //sign in button
        mUsernameSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mUsernameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        //sign up button
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //go to resister form
                // Declare activity switch intent
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);

                // Start activity
                startActivity(intent);
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    //private void populateAutoComplete() {getLoaderManager().initLoader(0, null, this);}

    @Override
    protected void onPause() {
        if (mAuthTask != null) {
            mAuthTask.cancel(true);
        }
        super.onPause();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Cancel previous task if it is still running
        if (mAuthTask != null && mAuthTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            mAuthTask.cancel(true);
        }

        // Reset errors.
        nUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = nUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            nUsernameView.setError(getString(R.string.error_field_required));
            focusView = nUsernameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            mAuthTask = new UserLoginTask(MainActivity.this);
            mAuthTask.execute(username, password);
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}
