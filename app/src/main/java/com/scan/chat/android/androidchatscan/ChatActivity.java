package com.scan.chat.android.androidchatscan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.widget.Toast.LENGTH_LONG;

public class ChatActivity extends Activity {

    // UI references.
    private EditText mMessageText;
    private Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Retrieve auth extra passed from previous activity
        String auth = getIntent().getStringExtra(MainActivity.EXTRA_AUTH);

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


    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected Boolean doInBackground(String... params) {

            String username = params[0];
            String password = params[1];

            // Webservice URL
            String urlString = new StringBuilder(API_BASE_URL + "/connect/").toString();
            String userp = new StringBuilder(username + ":" + password).toString();
            basicAuth = "Basic " + Base64.encodeToString(userp.getBytes(), Base64.NO_WRAP);

            //check connection
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isConnected()) {
                // everything is so far

                try {

                    //authentification
                    URL imageUrl = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
                    conn.setRequestProperty("Authorization", basicAuth);

                    conn.setConnectTimeout(30000);
                    conn.setReadTimeout(30000);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();

                    int response = conn.getResponseCode();

                    if(response == 200)
                        return true;
                }
                catch(IOException e){
                    Toast.makeText(MainActivity.this, e.getMessage(), LENGTH_LONG).show();

                }

            } else {
                // display error
                Toast.makeText(MainActivity.this, R.string.no_connection, LENGTH_LONG).show();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success)
            {
                // Everything good!
                Toast.makeText(MainActivity.this, R.string.login_success, LENGTH_LONG).show();

                // Declare activity switch intent
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra(EXTRA_AUTH, basicAuth);

                // Start activity
                startActivity(intent);
                // If you don't want the current activity to be in the backstack,
                // uncomment the following line:
                // finish();

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }
    }

    protected void onLoadMessages() {
        //TODO: Load chat messages.

        // Display spinner
        // showSpinner()

        // Request message list
        new RequestTask().execute("http://training.loicortola.com/chat-rest/2.0");

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
