package com.scan.chat.android.androidchatscan.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.scan.chat.android.androidchatscan.activities.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class Message {

    private String uuid;
    private String login;
    private String message;
    private List<Attachment> attachments;

    private String images[];

    public Message(String id, String login, String message) {
        this.uuid = id;
        this.login = login;
        this.message = message;
        this.attachments = new ArrayList<>();
    }

    public String getId() { return uuid; }

    public void setId(String id) { this.uuid = id; }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public void addAttachment(Attachment attachment)
    {
        attachments.add(attachment);
    }

    public String[] getImages() {
        return images;
    }

    public void setImages(String images[]){
        this.images = images;
    }

    /**
     * return true if this message comes from the user
     * @param context current context
     */
    public boolean isMine(Context context){
        SharedPreferences sprefs = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
        if(sprefs.getString("username", null).equals(login)) {
            return true;
        } else {
            return false;
        }
    }

}
