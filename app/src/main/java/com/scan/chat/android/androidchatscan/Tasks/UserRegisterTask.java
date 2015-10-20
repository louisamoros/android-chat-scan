package com.scan.chat.android.androidchatscan.Tasks;

/**
 * Created by guillaumenostrenoff on 16/10/15.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.scan.chat.android.androidchatscan.Activities.ChatActivity;
import com.scan.chat.android.androidchatscan.Activities.MainActivity;
import com.scan.chat.android.androidchatscan.Activities.RegisterActivity;
import com.scan.chat.android.androidchatscan.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Represents an asynchronous registration task used to authenticate
 * the user.
 */
public class UserRegisterTask extends AsyncTask<String, Void, Boolean> {

    private Context mContext;
    private String username;
    private String password;
    private String basicAuth;

    public UserRegisterTask(Context context) {
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
        String urlString = new StringBuilder(MainActivity.API_BASE_URL + "/register/").toString();
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        HttpURLConnection conn = null;

        String userp = new StringBuilder(username + ":" + password).toString();

        //just in case of resistration success, extra to pass to chatActivity
        basicAuth = "Basic " + Base64.encodeToString(userp.getBytes(), Base64.NO_WRAP);

        try {
            URL imageUrl = new URL(urlString);
            conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("login", username);
            jsonParam.put("password", password);
            conn.setFixedLengthStreamingMode(jsonParam.toString().length());
            conn.connect();

            //start query
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(jsonParam.toString());

            //make sure writer is flushed
            writer.flush();

            //get response
            int response = conn.getResponseCode();
            if(response == 200)
                return true;


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{writer.close();}catch(Exception e){}
            try{reader.close();}catch(Exception e){}
            try{conn.disconnect();}catch(Exception e){}
        }

        return false;

    }

    @Override
    protected void onPostExecute(final Boolean success) {
        showProgress(false);

        if (success)
        {

            // Everything good!
            Toast.makeText(mContext, R.string.register_success, LENGTH_LONG).show();

            // Declare activity switch intent
            Intent intent = new Intent(mContext, ChatActivity.class);

            // save username and password using a shared preference
            SharedPreferences sPrefs = mContext.getSharedPreferences(MainActivity.PREFS_NAME, 0);
            SharedPreferences.Editor editor = sPrefs.edit();
            editor.putString("username", username);
            editor.putString("password", password);
            editor.putString("auth", basicAuth);
            editor.commit();

            // Start activity
            mContext.startActivity(intent);
            // we don't want the current activity to be in the backstack,
            try{
                RegisterActivity.ra.finish();
            } catch (Exception e) {
                int a = 3;
            }


        } else {
            Toast.makeText(mContext, R.string.register_error, LENGTH_LONG).show();
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

            RegisterActivity.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            RegisterActivity.mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    RegisterActivity.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            RegisterActivity.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            RegisterActivity.mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    RegisterActivity.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            RegisterActivity.mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            RegisterActivity.mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
