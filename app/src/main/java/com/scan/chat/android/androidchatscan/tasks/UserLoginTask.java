package com.scan.chat.android.androidchatscan.tasks;

/**
 * Created by guillaumenostrenoff on 16/10/15.
 */

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import com.scan.chat.android.androidchatscan.activities.MainActivity;
import com.scan.chat.android.androidchatscan.interfaces.UserLoginInterface;
import com.scan.chat.android.androidchatscan.models.User;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<String, Void, Boolean> {

    private UserLoginInterface activityInterface;
    private String username;
    private String password;
    private String basicAuth;
    private User user;

    public UserLoginTask(UserLoginInterface activityInterface) {
        this.activityInterface = activityInterface;
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

        // Instantiate user and get auth
        user = new User(username,password);
        basicAuth = user.getEncodedBase64();

        try {

            // Authentication
            URL imageUrl = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setRequestProperty("Authorization", basicAuth);

            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();

            if (response == 200) {
                return true;
            }
        } catch (IOException e) {
            // Set flag
        }

        return false;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        // Stop showing loading animation and make sure the user object is no longer referenced
        showProgress(false);
        user = null;

        if (success)
            activityInterface.onSuccess(basicAuth);
        else
            activityInterface.onFailure();

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
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

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
        }*/
    }
}