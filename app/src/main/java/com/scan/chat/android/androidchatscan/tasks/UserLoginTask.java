package com.scan.chat.android.androidchatscan.tasks;

/**
 * Created by guillaumenostrenoff on 16/10/15.
 */

import android.os.AsyncTask;

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
        if (success)
            activityInterface.onLoginSuccess(basicAuth);
        else
            activityInterface.onLoginFailure();

    }
}