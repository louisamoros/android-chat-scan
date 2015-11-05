package com.scan.chat.android.androidchatscan.models;

public class Attachment {

    private String mimeType;
    private String data;

    public Attachment(String data)
    {
        this.mimeType = "image/png";
        this.data = data;
    }
}
