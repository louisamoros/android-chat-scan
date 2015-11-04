package com.scan.chat.android.androidchatscan.model;

import android.util.Base64;

/**
 * Created by guillaumenostrenoff on 20/10/15.
 */
public class User {

    private String login;
    private String password;

    public User(String username, String password)
    {
        this.login = username;
        this.password = password;
    }

    public String getUsername() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.login = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * calls the encodeToString() method get encoded base 64
     * @return auth
     */
    public String getEncodedBase64()
    {
        if(login == null || password == null)
            return null;

        String credentials = new StringBuilder(login + ":" + password).toString();
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

    }


}
