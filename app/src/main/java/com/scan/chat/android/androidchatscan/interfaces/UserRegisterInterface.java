package com.scan.chat.android.androidchatscan.interfaces;

import com.scan.chat.android.androidchatscan.models.User;

public interface UserRegisterInterface {

    void onRegisterSuccess(User newUser);
    void onRegisterFailure();

}

