package com.scan.chat.android.androidchatscan.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scan.chat.android.androidchatscan.R;
import com.scan.chat.android.androidchatscan.activities.MainActivity;

import java.util.ArrayList;

/**
 * Created by louis on 10/30/15.
 */
public class MessageAdapter extends ArrayAdapter<Message> {

    Context context;
    int layoutResourceId;
    ArrayList<Message> listMessages = null;

    public MessageAdapter(Context context, int layoutResourceId, ArrayList<Message> listMessages) {
        super(context, layoutResourceId, listMessages);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.listMessages= listMessages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MessageHolder holder = null;

        // Get username of the current user to display right bubble message
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFS_NAME, 0);
        String currentUsername = preferences.getString("username", null);

        // Get current message in the list
        Message message = listMessages.get(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MessageHolder();
            holder.message = (TextView) row.findViewById(R.id.message_row);
            LinearLayout messageRowContainer = (LinearLayout) row.findViewById(R.id.message_row_container);

            ShapeDrawable shape = row.find(R.id.shape_bubble);
            GradientDrawable bgShape = (GradientDrawable)shape.getBackground();
            bgShape.setColor(Color.BLACK);

            if(currentUsername == message.getLogin().toString()) {
//            if ((position & 1) == 0 ) {
                messageRowContainer.setGravity(Gravity.RIGHT);
            } else {
                messageRowContainer.setGravity(Gravity.LEFT);
            }

            row.setTag(holder);

        } else {
            holder = (MessageHolder)row.getTag();
        }

        holder.message.setText(message.getMessage());

        return row;
    }

    static class MessageHolder
    {
        TextView login;
        TextView message;
    }
}
