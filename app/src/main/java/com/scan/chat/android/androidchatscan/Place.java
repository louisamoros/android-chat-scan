package com.scan.chat.android.androidchatscan;

import android.content.Context;

/**
 * Created by louis on 10/9/15.
 */
public class Place {
    public String name;
    public String imageName;
    public boolean isFav;

    public int getImageResourceId(Context context) {
        return context.getResources().getIdentifier(this.imageName, "drawable", context.getPackageName());
    }
}
