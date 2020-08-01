package com.nisith.firebasepagicnationapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.DatabasePagingOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.nisith.firebasepagicnationapp.Model.Message;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FirebasePagicnationAdapter extends FirebaseRecyclerAdapter<Message, FirebasePagicnationAdapter.MyViewHolder> {


    public FirebasePagicnationAdapter(@NonNull FirebaseRecyclerOptions<Message> options) {
        super(options);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_apperance,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Message message) {
        holder.messageTextView.setText(message.getMessage());
    }


    @Override
    public void onDataChanged() {
        super.onDataChanged();
        Log.d("ABCD","onDataChanged() is Called");
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.message_text_view);
        }
    }
}
