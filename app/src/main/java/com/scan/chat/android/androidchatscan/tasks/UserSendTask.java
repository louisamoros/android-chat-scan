package com.scan.chat.android.androidchatscan.tasks;

/**
 * Created by guillaumenostrenoff on 15/10/15.
 */

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scan.chat.android.androidchatscan.activities.MainActivity;
import com.scan.chat.android.androidchatscan.interfaces.UserSendInterface;
import com.scan.chat.android.androidchatscan.models.Attachment;
import com.scan.chat.android.androidchatscan.models.Message;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

/**
 * Represents an asynchronous message sending task
 */
public class UserSendTask extends AsyncTask<String, Void, Boolean> {

    private boolean img;    //true if image must be attached
    private UserSendInterface activityInterface;

    public UserSendTask(boolean img, UserSendInterface activityInterface) {
        this.img = img;
        this.activityInterface = activityInterface;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        String message = params[0];
        String encodedImage = params[1];
        String username = params[2];
        String auth = params[3];

        String urlString = new StringBuilder(MainActivity.API_BASE_URL + "/messages/").toString();
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        HttpURLConnection conn = null;

        // Put empty images string array for the constructor
        String images[] = new String[1];

        // Create message to send
        Message mess = new Message(UUID.randomUUID().toString(),username,message,images);

        // Add image to message if required
        if(img)
        {
            Attachment att = new Attachment(encodedImage);
            mess.addAttachment(att);
        }

        //http request process
        try {
            //open connection
            URL imageUrl = new URL(urlString);
            conn = (HttpURLConnection) imageUrl.openConnection();

            //authentication
            conn.setRequestProperty("Authorization", auth);
            //json post type request
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");

            conn.setDoOutput(true);
            conn.setDoInput(true);

            //create gson models
            Type type = new TypeToken<Message>() {}.getType();
            String gsonString = new Gson().toJson(mess,type);

            conn.setFixedLengthStreamingMode(gsonString.length());
            conn.connect();

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
            activityInterface.onSendSuccess();
        else
            activityInterface.onSendFailure();

    }
}