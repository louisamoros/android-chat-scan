package com.scan.chat.android.androidchatscan.model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scan.chat.android.androidchatscan.R;
import com.scan.chat.android.androidchatscan.activities.ChatActivity;
import com.scan.chat.android.androidchatscan.activities.MainActivity;
import com.scan.chat.android.androidchatscan.tasks.LoadImageTask;

import java.util.ArrayList;

import static android.widget.Toast.LENGTH_LONG;

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
            holder.login = (TextView) row.findViewById(R.id.login_row);
            holder.image = (ImageView) row.findViewById(R.id.image_row);
            LinearLayout messageRowContainer = (LinearLayout) row.findViewById(R.id.message_row_container);

            if(holder.image != null)
            {
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        ImageView imgView = (ImageView)view;
                        Bitmap bitmap = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
                        //String path = ChatActivity.mChatActivity.save

                        /*if(path == null)
                            Toast.makeText(context, R.string.save_image_success, LENGTH_LONG).show();
                        else
                            Toast.makeText(context, R.string.save_image_failed, LENGTH_LONG).show();
*/
                    }
                });
            }

            if(message.isMine(context)) {
                messageRowContainer.setGravity(Gravity.RIGHT);
            }

            row.setTag(holder);

        } else {
            holder = (MessageHolder)row.getTag();
        }

        holder.login.setText(message.getLogin());
        holder.message.setText(message.getMessage());
        String images[] = message.getImages();

        if(images != null && images.length != 0) {
            new LoadImageTask(holder.image, context).execute(images[0]);
        }

        return row;
    }

    static class MessageHolder
    {
        TextView login;
        TextView message;
        ImageView image;
    }
}
