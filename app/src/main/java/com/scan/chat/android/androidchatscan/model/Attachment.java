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

    public String getMimeType() {
        return mimeType;
    }

    public String getData() {
        return data;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setData(String data) {
        this.data = data;
    }


}
