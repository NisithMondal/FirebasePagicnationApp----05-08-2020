package com.nisith.firebasepagicnationapp;

import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.TextView;


import com.nisith.firebasepagicnationapp.Model.Message;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FirebasePaginationAdapter extends RecyclerView.Adapter<FirebasePaginationAdapter.MyViewHolder> {

    private List<Message> messageList;

    public FirebasePaginationAdapter(List<Message> messageList){
        this.messageList = messageList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_apperance,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.messageTextView.setText(messageList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        int totalItems = 0;
        if (messageList != null){
            totalItems = messageList.size();
        }
        return totalItems;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text_view);
        }
    }
}
