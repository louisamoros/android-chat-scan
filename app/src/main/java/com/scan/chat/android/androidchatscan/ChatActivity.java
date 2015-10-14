package com.scan.chat.android.androidchatscan;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scan.chat.android.androidchatscan.model.Message;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import static android.widget.Toast.LENGTH_LONG;

public class ChatActivity extends Activity {

    private UserSendTask sendTask;
    private String auth;
    private String username;
    private ListView listMessage;
    ArrayList<Message> allMessages;
    private static int RESULT_LOAD_IMAGE = 1;

    // UI references.
    private EditText mMessageText;
    private Button mSendButton;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Retrieve shared preferences content
        SharedPreferences sPrefs = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        username = sPrefs.getString("username", null);
        auth = sPrefs.getString("auth", null);


        // Call method to load messages with EXTRA_AUTH
        onLoadMessages();

        //get the "pull to refresh" view
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        //mSwipeRefreshLayout.setColorSchemeResources(Color.BLACK);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onLoadMessages();
            }
        });

        // send message button
        mMessageText = (EditText) findViewById(R.id.EditText);
        mSendButton = (Button) findViewById(R.id.Button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendMessage();
            }
        });


        /*Button buttonLoadImage = (Button) findViewById(R.id.importImgButton);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                openGallery();
            }
        });*/

        // List view setup
        listMessage = (ListView) findViewById(R.id.ListMessage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;

            case R.id.action_log_out:
                Intent k = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(k);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

        //return id == R.id.action_log_out;


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //ImageView imageView = (ImageView) findViewById(R.id.imgView);
            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

        }


    }

    private void openGallery()
    {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }


    class LoadMessagesTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
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
                Toast.makeText(ChatActivity.this, e.getMessage(), LENGTH_LONG).show();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                // Set the adapter
                ArrayAdapter<Message> adapter = new ArrayAdapter<>(ChatActivity.this, android.R.layout.simple_list_item_1,allMessages);
                listMessage.setAdapter(adapter);
                // Stop the animation after all the messages are fully loaded
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                Toast.makeText(ChatActivity.this, "Something went wrong.", LENGTH_LONG).show();
            }
        }
    }

    protected void onLoadMessages() {
        //TODO: Load chat messages.

        // Display spinner
        // showSpinner()

        // Request message list
        new LoadMessagesTask().execute();

        // <<<<<<<<
        // If request too long or fail (400)
        // hide spinner hideSpinner()
        // show something went wrong

        // Else request succeed (200)
        // hide spinner hideSpinner()
        // show message list
    }

    /**
     * This method gets the string from the message edittext and
     * executes the asynchronous task to send the message to the server
     */
    private void onSendMessage() {
        
        // get message string from editview
        String message = mMessageText.getText().toString();

        // execute asynchronus task to send message
        sendTask = new UserSendTask(message);
        sendTask.execute(message);
    }

    protected void showSpinner() {
        //TODO: show spinner when loading message or sending message.
    }

    protected void hideSpinner() {
        //TODO: hide spinner
    }

    /**
     * Represents an asynchronous message sending task
     */
    public class UserSendTask extends AsyncTask<String, Void, Boolean> {

        private final String message;

        UserSendTask(String message) {
            this.message = message;
        }

        @Override
        protected Boolean doInBackground(String... params) {

            String message = params[0];
            String urlString = new StringBuilder(MainActivity.API_BASE_URL + "/messages/").toString();
            OutputStreamWriter writer = null;
            InputStream is = null;
            BufferedReader reader = null;
            HttpURLConnection conn = null;


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

                //generate valid uuid
                String uuid = UUID.randomUUID().toString();


                //Create JSONObject here
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("uuid", uuid);
                jsonParam.put("login", username);
                jsonParam.put("message", message);
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
            sendTask = null;

            if (success) {
                Toast.makeText(ChatActivity.this, R.string.sent_success, LENGTH_LONG).show();
                onLoadMessages();
            }
            else {
                Toast.makeText(ChatActivity.this, R.string.sent_failed, LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            sendTask = null;
        }
    }
}
