package com.scan.chat.android.androidchatscan.tasks;

/**
 * Created by guillaumenostrenoff on 16/10/15.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import com.scan.chat.android.androidchatscan.R;
import com.scan.chat.android.androidchatscan.activities.ChatActivity;
import com.scan.chat.android.androidchatscan.activities.MainActivity;
import com.scan.chat.android.androidchatscan.models.User;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<String, Void, Boolean> {

    private Context mContext;
    private String username;
    private String password;
    private String basicAuth;
    private User user;

    public UserLoginTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        showProgress(true);
    }

    @Override
    protected Boolean doInBackground(String... params) {

        username = params[0];
        password = params[1];

        // Webservice URL
        String urlString = new StringBuilder(MainActivity.API_BASE_URL + "/connect/").toString();

        //instantiate user and get auth
        user = new User(username,password);
        basicAuth = user.getEncodedBase64();

        //check connection
        ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // everything is good so far

            try {

                //authentification
                URL imageUrl = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                conn.setRequestProperty("Authorization", basicAuth);

                conn.setConnectTimeout(30000);
                conn.setReadTimeout(30000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                int response = conn.getResponseCode();

                if (response == 200)
                    return true;
            } catch (IOException e) {
                Toast.makeText(mContext, e.getMessage(), LENGTH_LONG).show();

            }

        } else {
            // display error
            Toast.makeText(mContext, R.string.no_connection, LENGTH_LONG).show();
        }

        return false;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        //stop showing loading animation and make sure the user object is no longer referenced
        showProgress(false);
        user = null;

        if (success) {

            // Everything good!
            Toast.makeText(mContext, R.string.login_success, LENGTH_LONG).show();

            // Declare activity switch intent
            Intent intent = new Intent(mContext, ChatActivity.class);

            // save username, password and auth using a shared preference
            SharedPreferences sPrefs = mContext.getSharedPreferences(MainActivity.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sPrefs.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putString("auth", basicAuth);
            editor.commit();

            // Start activity
            mContext.startActivity(intent);
            // we don't want the current activity to be in the backstack,
            MainActivity.mLoginActivity.finish();

        } else {
            MainActivity.mPasswordView.setError(mContext.getString(R.string.error_incorrect_password));
            MainActivity.mPasswordView.requestFocus();
        }
    }

    @Override
    protected void onCancelled() {
        showProgress(false);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = mContext.getResources().getInteger(android.R.integer.config_shortAnimTime);

            MainActivity.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            MainActivity.mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    MainActivity.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            MainActivity.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            MainActivity.mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    MainActivity.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            MainActivity.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            MainActivity.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}