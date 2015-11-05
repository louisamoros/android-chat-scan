package com.scan.chat.android.androidchatscan.interfaces;

import com.scan.chat.android.androidchatscan.models.Message;

import java.util.List;

public interface LoadMessagesInterface {

    void onLoadMessagesSuccess(List<Message> listMessages);
    void onLoadMessageFailure(String error);

}
