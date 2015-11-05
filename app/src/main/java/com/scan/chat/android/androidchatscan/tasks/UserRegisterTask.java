package com.scan.chat.android.androidchatscan.tasks;

/**
 * Created by guillaumenostrenoff on 16/10/15.
 */

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scan.chat.android.androidchatscan.activities.MainActivity;
import com.scan.chat.android.androidchatscan.interfaces.UserRegisterInterface;
import com.scan.chat.android.androidchatscan.models.User;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Represents an asynchronous registration task used to authenticate
 * the user.
 */
public class UserRegisterTask extends AsyncTask<String, Void, Boolean> {

    private  UserRegisterInterface activityInterface;
    private User newUser;

    public UserRegisterTask(UserRegisterInterface activityInterface) {
        this.activityInterface = activityInterface;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        String username = params[0];
        String password = params[1];
        String urlString = new StringBuilder(MainActivity.API_BASE_URL + "/register/").toString();
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        HttpURLConnection conn = null;

        //instantiate new user
        newUser = new User(username, password);

        try {
            URL imageUrl = new URL(urlString);
            conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);

            //create gson models
            Type type = new TypeToken<User>() {}.getType();
            String gsonString = new Gson().toJson(newUser,type);

            //start query
            writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(gsonString);

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

        if (success)
            activityInterface.onRegisterSuccess(newUser);
        else
            activityInterface.onRegisterFailure();
    }
}
