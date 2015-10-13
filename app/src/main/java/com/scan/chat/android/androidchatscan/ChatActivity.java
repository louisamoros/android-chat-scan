package com.scan.chat.android.androidchatscan;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.widget.SwipeRefreshLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.util.UUID;

import org.json.JSONObject;

import static android.widget.Toast.LENGTH_LONG;

public class ChatActivity extends Activity {

    private LoadMessagesTask mLoadMessagesTask = null;
    private UserSendTask sendTask;
    private String auth;
    private String username;
    private String password;
    private String allMessages;

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
        password = sPrefs.getString("password", null);
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
        // Set up the login form.
        mMessageText = (EditText) findViewById(R.id.EditText);
        mSendButton = (Button) findViewById(R.id.Button);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSendMessage();
            }
        });
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
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_profile) {
            return true;
        }

        if (id == R.id.action_log_out) {
            return true;
        }
        else return false;

    }


    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    class LoadMessagesTask extends AsyncTask<String, Void, Boolean> {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 1500;

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
                    is = conn.getInputStream();
                    // Convert the InputStream into a string
                    allMessages = readIt(is, len);
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
            mLoadMessagesTask = null;
            //showProgress(false);

            if (success) {
                // Everything good!
                TextView messages = (TextView) findViewById(R.id.Messages);
                messages.setText(allMessages);
                //stop the animation after all the messages are fully loaded
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
        new LoadMessagesTask().execute("http://training.loicortola.com/chat-rest/2.0");

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

        // Reads an InputStream and converts it to a String.
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
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
