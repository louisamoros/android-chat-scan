package com.scan.chat.android.androidchatscan.model;

/**
 * Created by louis on 10/13/15.
 */
public class Message {

    private long id;
    private String login;
    private String message;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
