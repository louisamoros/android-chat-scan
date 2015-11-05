package com.scan.chat.android.androidchatscan.interfaces;

import com.scan.chat.android.androidchatscan.models.User;

/**
 * Created by guillaumenostrenoff on 04/11/15.
 */
public interface UserRegisterInterface {

    public void onRegisterSuccess(User newUser);
    public void onRegisterFailure();
}

