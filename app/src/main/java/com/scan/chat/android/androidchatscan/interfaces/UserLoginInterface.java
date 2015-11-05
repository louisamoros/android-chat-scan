package com.scan.chat.android.androidchatscan.interfaces;

/**
 * Created by guillaumenostrenoff on 04/11/15.
 */
public interface UserLoginInterface {

    public void onLoginSuccess(String basicAuth);
    public void onLoginFailure();
}
