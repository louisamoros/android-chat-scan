package com.scan.chat.android.androidchatscan;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

    protected void onLoadMessages() {
        //TODO: Load chat messages.

        // Display spinner
        // showSpinner()

        // Request message list
        class RequestTask extends AsyncTask<String, String, String> {

            @Override
            protected String doInBackground(String... uri) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                String responseString = null;
                try {
                    response = httpclient.execute(new HttpGet(uri[0]));
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        responseString = out.toString();
                        out.close();
                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }
                } catch (ClientProtocolException e) {
                    //TODO Handle problems..
                } catch (IOException e) {
                    //TODO Handle problems..
                }
                return responseString;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                //Do anything with response..
            }
            // >>>>>>>>

            // <<<<<<<<
            // If request too long or fail (400)
            // hide spinner hideSpinner()
            // show something went wrong

            // Else request succeed (200)
            // hide spinner hideSpinner()
            // show message list

        }
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
