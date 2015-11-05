package com.scan.chat.android.androidchatscan.interfaces;

public interface UserLoginInterface {

    void onLoginSuccess(String basicAuth);
    void onLoginFailure();

}
