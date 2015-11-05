package com.scan.chat.android.androidchatscan.utils;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scan.chat.android.androidchatscan.R;
import com.scan.chat.android.androidchatscan.models.Message;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.MessageViewHolder> {

    List<Message> listMessages;
    Context context;

    public MessageRecyclerViewAdapter(List<Message> listMessages, Context context) {
        this.listMessages = listMessages;
        this.context = context;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_card_view, parent, false);
        MessageViewHolder messageViewHolder = new MessageViewHolder(v);
        return messageViewHolder;
    }

    @Override
    public void onBindViewHolder(MessageViewHolder messageViewHolder, int position) {
        if(listMessages.get(position).isMine(context)) {
            messageViewHolder.cardViewLogin.setTextColor(Color.BLACK);
            messageViewHolder.cardViewMessage.setTextColor(Color.BLACK);
            Uri uri = Uri.parse("http://www.gettyimages.co.uk/gi-resources/images/Homepage/Hero/UK/CMS_Creative_164657191_Kingfisher.jpg");
            Picasso.with(context)
                    .load(uri)
                    .into(messageViewHolder.cardViewImage);
        } else {
            messageViewHolder.cardViewLogin.setTextColor(Color.BLUE);
            messageViewHolder.cardViewMessage.setTextColor(Color.BLUE);
        }
        messageViewHolder.cardViewLogin.setText(listMessages.get(position).getLogin());
        messageViewHolder.cardViewMessage.setText(listMessages.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView cardViewLogin;
        TextView cardViewMessage;
        ImageView cardViewImage;

        MessageViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
            cardViewLogin = (TextView)itemView.findViewById(R.id.card_view_login);
            cardViewMessage = (TextView)itemView.findViewById(R.id.card_view_message);
//            cardViewImage = (ImageView)itemView.findViewById(R.id.card_view_image);
        }
    }

}