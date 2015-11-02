package com.scan.chat.android.androidchatscan.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scan.chat.android.androidchatscan.activities.MainActivity;
import com.scan.chat.android.androidchatscan.model.Message;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by louis on 11/2/15.
 */
public class LoadImageTask extends AsyncTask<String, Void, Bitmap>{

    private Context mContext;
    ImageView bmImage;

    public  LoadImageTask(ImageView bmImage, Context context) {
        this.bmImage = bmImage;
        this.mContext = context;
    }

    protected Bitmap doInBackground(String... urls) {
        String urlDisplay = urls[0];
        Bitmap mIcon11 = null;

        // Get user info from sharedPreferences
        SharedPreferences sPrefs = mContext.getSharedPreferences(MainActivity.PREFS_NAME, 0);
        String auth = sPrefs.getString("auth", null);

        try {
            URL imageUrl = new URL(urlDisplay);
            HttpURLConnection conn = (HttpURLConnection) imageUrl.openConnection();
            conn.setRequestProperty("Authorization", auth);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            mIcon11 = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}