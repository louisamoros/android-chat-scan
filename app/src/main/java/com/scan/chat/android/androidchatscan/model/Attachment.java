package com.scan.chat.android.androidchatscan.model;

/**
 * Created by guillaumenostrenoff on 14/10/15.
 */
public class Attachment {

    private String mimeType;
    private String data;

    public Attachment(String data)
    {
        this.mimeType = "image/png";
        this.data = data;
    }
}
