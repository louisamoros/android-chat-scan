package com.scan.chat.android.androidchatscan.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by louis on 10/13/15.
 */
public class Message {

    private String uuid;
    private String login;
    private String message;
    private List<Attachment> attachments;

    public Message(String id, String login, String message) {
        this.uuid = id;
        this.login = login;
        this.message = message;
        this.attachments = new ArrayList<Attachment>();
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

    public void setMessage(String message) {
        this.message = message;
    }

    public void addAttachment(Attachment attachment)
    {
        attachments.add(attachment);
    }

    @Override
    public String toString() {
        return this.login + ": " + this.message;
    }
}
