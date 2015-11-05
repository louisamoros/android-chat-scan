package com.scan.chat.android.androidchatscan.utils;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.scan.chat.android.androidchatscan.R;
import com.scan.chat.android.androidchatscan.models.Message;
import com.scan.chat.android.androidchatscan.tasks.LoadImageTask;

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
            messageViewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.light_yellow));
        } else {
            messageViewHolder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.light_gray));
        }
        messageViewHolder.cardViewLogin.setText(listMessages.get(position).getLogin());
        messageViewHolder.cardViewMessage.setText(listMessages.get(position).getMessage());
        String [] images = listMessages.get(position).getImages();
        if(images != null && images.length > 0) {
            new LoadImageTask(messageViewHolder.cardViewImage, context).execute(images[0]);
        }
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
        ProgressBar cardViewImageLoader;

        MessageViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
            cardViewLogin = (TextView)itemView.findViewById(R.id.card_view_login);
            cardViewMessage = (TextView)itemView.findViewById(R.id.card_view_message);
            cardViewImage = (ImageView)itemView.findViewById(R.id.card_view_image);
        }
    }

}