package com.scan.chat.android.androidchatscan.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.InputType;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.scan.chat.android.androidchatscan.R;
import com.scan.chat.android.androidchatscan.tasks.LoadMessagesTask;
import com.scan.chat.android.androidchatscan.tasks.UserSendTask;

import java.io.ByteArrayOutputStream;



public class ChatActivity extends Activity {

    public static ListView listMessage;
    private static int RESULT_LOAD_IMAGE = 1;
    private UserSendTask sendTask;
    private LoadMessagesTask loadMessagesTask;
    private String message;
    private String encodedImage;


    // UI references.
    private EditText mMessageText;
    private Button mSendButton;
    public static SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // List view setup
        listMessage = (ListView) findViewById(R.id.ListMessage);


        // Call method to load messages with EXTRA_AUTH
        loadMessagesTask = new LoadMessagesTask(ChatActivity.this);
        loadMessagesTask.execute();

        //get the "pull to refresh" view
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        //mSwipeRefreshLayout.setColorSchemeResources(Color.BLACK);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMessagesTask = new LoadMessagesTask(ChatActivity.this);
                loadMessagesTask.execute();
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
            case R.id.import_img_button:
                //openGalleryAndSend();

                Drawable loul = getResources().getDrawable( R.drawable.ic_launcher);
                Bitmap imageBitmap = ((BitmapDrawable)loul).getBitmap();

                //get a encode64 image from the bitmap
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

                message = "";

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Send an image");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setMessage("add a message: ");

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        message = input.getText().toString();
                        sendTask = new UserSendTask(true, ChatActivity.this);
                        sendTask.execute(message, encodedImage);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

                return true;

            case R.id.action_settings:
                Intent j = new Intent(this, SettingsActivity.class);
                startActivity(j);
                return true;

            case R.id.action_log_out:
                //clear user's data to disconnect
                SharedPreferences sPrefs = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = sPrefs.edit();
                editor.clear();
                editor.commit();
                Intent k = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(k);
                // we don't want the current activity to be in the backstack,
                finish();
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

            Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            //make the user confirm he wants to send the picture, and make him add a message

            message = "";

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Send an image");

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setMessage("add a message: ");

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    message = input.getText().toString();
                    sendTask = new UserSendTask(true, ChatActivity.this);
                    sendTask.execute(message, encodedImage);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }


    }

    private void openGalleryAndSend()
    {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }


    /**
     * This method gets the string from the message edittext and
     * executes the asynchronous task to send the message to the server
     */
    private void onSendMessage() {
        // Cancel previous task if it is still running
        if (sendTask != null && sendTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            sendTask.cancel(true);
        }

        // get message string from editview
        String message = mMessageText.getText().toString();

        // execute asynchronus task to send message
        sendTask = new UserSendTask(false, ChatActivity.this);
        sendTask.execute(message, null);
    }

    protected void showSpinner() {
        //TODO: show spinner when loading message or sending message.
    }

    protected void hideSpinner() {
        //TODO: hide spinner
    }

}
