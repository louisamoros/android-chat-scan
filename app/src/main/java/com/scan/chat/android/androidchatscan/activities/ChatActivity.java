package com.scan.chat.android.androidchatscan.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.scan.chat.android.androidchatscan.R;
import com.scan.chat.android.androidchatscan.tasks.LoadMessagesTask;
import com.scan.chat.android.androidchatscan.tasks.UserSendTask;

import java.io.ByteArrayOutputStream;


public class ChatActivity extends Activity{

    public static ListView listMessage;
    private static int RESULT_LOAD_IMAGE = 1;
    public static Activity mChatActivity;

    private UserSendTask userSendTask;
    private LoadMessagesTask loadMessagesTask;
    private String message;
    private String encodedImage;

    // UI references.
    public static EditText mMessageText;
    private ImageButton mSendButton;
    public static SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Keep a reference to this activity to be able to finish it from another entity
        mChatActivity = this;

        // Load theme
        SharedPreferences prefs = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        int theme = prefs.getInt("theme", 0);
        mChatActivity.setTheme(loadTheme(theme));

        setContentView(R.layout.activity_chat);

        // List view setup
        listMessage = (ListView) findViewById(R.id.ListMessage);

        // Call task to load messages
        loadMessagesTask = new LoadMessagesTask(mChatActivity);
        loadMessagesTask.execute();

        // Get the "pull to refresh" view and define its behavior
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMessagesTask = new LoadMessagesTask(mChatActivity);
                loadMessagesTask.execute();
            }
        });

        // Set 'send message' button
        mMessageText = (EditText) findViewById(R.id.EditText);
        mSendButton = (ImageButton) findViewById(R.id.Button);
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

        switch (id) {
            case R.id.import_img_button:
                openGalleryAndSend();
                return true;

            case R.id.action_settings:
                Intent settingsActivity = new Intent(mChatActivity, SettingsActivity.class);
                startActivity(settingsActivity);
                return true;

            case R.id.action_log_out:
                // Clear user's data to disconnect
                SharedPreferences sPrefs = getSharedPreferences(MainActivity.PREFS_NAME, 0);
                SharedPreferences.Editor editor = sPrefs.edit();
                editor.clear();
                editor.commit();
                Intent mainActivity = new Intent(mChatActivity, MainActivity.class);
                startActivity(mainActivity);
                // We don't want the current activity to be in the backstack,
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
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

            // Make the user confirm he wants to send the picture, and make him add a message
            message = "";

            AlertDialog.Builder builder = new AlertDialog.Builder(mChatActivity);
            builder.setTitle("Send an image");

            // Set up the input
            final EditText input = new EditText(mChatActivity);

            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setMessage("add a message: ");

            // Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    message = input.getText().toString();
                    userSendTask = new UserSendTask(true, mChatActivity);
                    userSendTask.execute(message, encodedImage);
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


    /**
     * return a int value according to the given theme
     * @param theme value of theme to load
     * @return appropriate int value
     */
    protected static int loadTheme(int theme){

        switch(theme) {

            case 1:
                return R.style.Theme1;
            case 2:
                return R.style.Theme2;
            default:
                return R.style.DefaultTheme;

        }
    }

    /**
     * This method opens the phone's gallery and makes the user chose one,
     * then sends it to the server
     */
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
        if (userSendTask != null && userSendTask.getStatus().equals(AsyncTask.Status.RUNNING)) {
            userSendTask.cancel(true);
        }

        // Get message string from editview
        String message = mMessageText.getText().toString();

        // Execute asynchronus task to send message
        userSendTask = new UserSendTask(false, mChatActivity);
        userSendTask.execute(message, null);
    }

}
