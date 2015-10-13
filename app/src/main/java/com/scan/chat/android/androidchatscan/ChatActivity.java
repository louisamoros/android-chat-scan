package com.scan.chat.android.androidchatscan;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.widget.Toast.LENGTH_LONG;

public class ChatActivity extends Activity {

    private LoadMessagesTask mLoadMessagesTask = null;
    private String auth;
    private String allMessages;

    // UI references.
    private EditText mMessageText;
    private Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Retrieve auth extra passed from previous activity
        auth = getIntent().getStringExtra(MainActivity.EXTRA_AUTH);

        // Call method to load messages with EXTRA_LOGIN
        onLoadMessages();

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

    protected void onSendMessage() {
        //TODO: Post message in current discussion

        // Send message
        // >>>>>>>>

        // <<<<<<<<
        // If request too long or fail (400)
        // show something went wrong

        // Else request succeed (200)
        // Load all message onLoadMessages()

    }

    protected void showSpinner() {
        //TODO: show spinner when loading message or sending message.
    }

    protected void hideSpinner() {
        //TODO: hide spinner
    }
}
