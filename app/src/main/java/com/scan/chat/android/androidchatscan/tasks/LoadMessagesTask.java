package com.scan.chat.android.androidchatscan.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scan.chat.android.androidchatscan.activities.ChatActivity;
import com.scan.chat.android.androidchatscan.activities.MainActivity;
import com.scan.chat.android.androidchatscan.model.Message;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_LONG;

public class LoadMessagesTask extends AsyncTask<String, Void, Boolean> {

    private Context mContext;
    private  ArrayList<Message> allMessages;

    public LoadMessagesTask(Context context){
        this.mContext = context;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        String auth = params[0];

        try {
            String urlString = new StringBuilder(MainActivity.API_BASE_URL + "/messages?&limit=10&offset=20")
                    .toString();
            URL imageUrl = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setRequestProperty("Authorization", auth);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();

            int response = conn.getResponseCode();

            if(response == 200) {
                Type type = new TypeToken<ArrayList<Message>>() {}.getType();
                String isToString = IOUtils.toString(conn.getInputStream(), "UTF-8");
                allMessages = new Gson().fromJson(isToString, type);
                return true;
            }
        }
        catch(IOException e){
            Toast.makeText(mContext, e.getMessage(), LENGTH_LONG).show();
        }
        return false;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        if (success) {
            // Set the adapter
            ArrayAdapter<Message> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1,allMessages);
            ChatActivity.listMessage.setAdapter(adapter);
            // Stop the animation after all the messages are fully loaded
            ChatActivity.mSwipeRefreshLayout.setRefreshing(false);
        } else {
            Toast.makeText(mContext, "Something went wrong.", LENGTH_LONG).show();
        }
    }
}