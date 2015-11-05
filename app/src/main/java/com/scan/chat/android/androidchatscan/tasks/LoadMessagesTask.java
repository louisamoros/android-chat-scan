package com.scan.chat.android.androidchatscan.tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scan.chat.android.androidchatscan.activities.MainActivity;
import com.scan.chat.android.androidchatscan.interfaces.LoadMessagesInterface;
import com.scan.chat.android.androidchatscan.models.Message;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoadMessagesTask extends AsyncTask<String, Void, Boolean> {

    private LoadMessagesInterface activityInterface;
    private List<Message> listMessages;
    private String basicAuth;

    public LoadMessagesTask(LoadMessagesInterface activityInterface){
        this.activityInterface = activityInterface;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        basicAuth = params[0];

        try {
            String urlString = new StringBuilder(MainActivity.API_BASE_URL + "/messages?&limit=100&offset=20")
                    .toString();
            URL imageUrl = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setRequestProperty("Authorization", basicAuth);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();

            if(response == 200) {
                Type type = new TypeToken<List<Message>>() {}.getType();
                String isToString = IOUtils.toString(conn.getInputStream(), "UTF-8");
                listMessages = new Gson().fromJson(isToString, type);
                return true;
            }
        }
        catch(IOException e){
            // Set flag
        }

        return false;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success)
            activityInterface.onLoadMessagesSuccess(listMessages);
        else
            activityInterface.onLoadMessageFailure("Fail to load messages.");
    }

}