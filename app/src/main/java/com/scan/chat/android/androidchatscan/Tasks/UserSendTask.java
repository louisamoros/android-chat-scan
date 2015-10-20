package com.scan.chat.android.androidchatscan.Tasks;

/**
 * Created by guillaumenostrenoff on 15/10/15.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.scan.chat.android.androidchatscan.Activities.MainActivity;
import com.scan.chat.android.androidchatscan.R;
import com.scan.chat.android.androidchatscan.model.Attachment;
import com.scan.chat.android.androidchatscan.model.Message;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import static android.widget.Toast.LENGTH_LONG;

/**
 * Represents an asynchronous message sending task
 */
public class UserSendTask extends AsyncTask<String, Void, Boolean> {

    private boolean img;    //true if image must be attached
    private String auth;
    private Context mContext;
    private LoadMessagesTask loadUserTask;

    public UserSendTask(boolean img, Context context) {
        this.img = img;
        this.mContext = context;

    }

    @Override
    protected Boolean doInBackground(String... params) {

        String message = params[0];
        String username = params[1];
        auth = params[2];
        String encodedImage = params[3];

        String urlString = new StringBuilder(MainActivity.API_BASE_URL + "/messages/").toString();
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        HttpURLConnection conn = null;

        //create message to send
        Message mess = new Message(UUID.randomUUID().toString(),username,message);

        //add image to message if required
        if(img)
        {
            Attachment att = new Attachment(encodedImage);
            mess.addAttachment(att);
            mess.setMessage("image sent by " + username);
        }

        //http request process
        try {
            //open connection
            URL imageUrl = new URL(urlString);
            conn = (HttpURLConnection) imageUrl.openConnection();

            //authentification
            conn.setRequestProperty("Authorization", auth);
            //json post type request
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestMethod("POST");

            conn.setDoOutput(true);
            conn.setDoInput(true);

            //create gson model
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
        //sendTask = null;

        if (success) {
            Toast.makeText(mContext, R.string.sent_success, LENGTH_LONG).show();
            //load messages if success
            loadUserTask = new LoadMessagesTask(mContext);
            loadUserTask.execute(auth);
        }
        else {
            Toast.makeText(mContext, R.string.sent_failed, LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCancelled() {
        //sendTask = null;
    }
}