package com.nisith.firebasepagicnationapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.PointF;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nisith.firebasepagicnationapp.Model.Message;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FirebasePaginationAdapter.OnDeleteIconClickListener {

    private RecyclerView recyclerView;
    private EditText messageEditText;
    private ImageView sendImageView;
    private FirebasePaginationAdapter adapter;
    //Firebase Instances
    private DatabaseReference firebaseDatabaseRef;


    private List<Message> messageList;

    private LinearLayoutManager layoutManager;
    private boolean isScrolling = false;
    private int scrollOutItems;
    private final int initialPageSize = 10;
    private final int pageSize = 10;
    private String lastMessageKey;
    private int index = 0;
    private boolean isChildRemove1 =  false;
    private boolean isChildRemove2 =  false;
    private String key = "";
    private String key1 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycler_view);
        messageEditText = findViewById(R.id.message_edit_text);
        sendImageView = findViewById(R.id.send_image_view);
        firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference().child("messages");
        messageList = new ArrayList<>();
        setRecyclerView();
        setPaginationKey();

        sendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        firebaseDatabaseRef.orderByKey().limitToLast(initialPageSize).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (!messageList.contains(message) && message.getMessageKey().compareTo(key)>0) {
                    messageList.add(message);
                 adapter.notifyDataSetChanged();
                 recyclerView.smoothScrollToPosition(adapter.getItemCount());
                 key = message.getMessageKey();
                }else {
                    Log.d("CVBN", "false  ");
                    Log.d("CVBN", "isChildRemove1 =  "+isChildRemove1);

                }
                isChildRemove1 = false;
                isChildRemove2 = false;
                Log.d("CVBN", "initial onChildAdded() is called  "+message.getMessageKey().compareTo(key));
                Log.d("CVBN", "initial message key =  "+message.getMessageKey());
                Log.d("CVBN", "initial key = "+key);

            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Message message = snapshot.getValue(Message.class);
                if (messageList.contains(message)){
                    messageList.get(messageList.indexOf(message)).setMessage(message.getMessage());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                Message message = snapshot.getValue(Message.class);
//                if (messageList.contains(message)){
//                    messageList.remove(message);
//                    adapter.notifyDataSetChanged();
//                    isChildRemove1 = true;
//                    Log.d("CVBN", "onChildRemoved is called");
//                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void fetchMoreDataFromServer(){

        firebaseDatabaseRef.orderByKey().endAt(lastMessageKey).limitToLast(pageSize)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            Message message = snapshot.getValue(Message.class);
                            //  && message.getMessageKey().compareTo(key1)>0
                            if (!messageList.contains(message)){
                                messageList.add(index,message);
                                index++;
                            }
                            adapter.notifyDataSetChanged();
                        isChildRemove1 = false;
                        isChildRemove2 = false;
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Message message = snapshot.getValue(Message.class);
                        if (messageList.contains(message)){
                            messageList.get(messageList.indexOf(message)).setMessage(message.getMessage());
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                        Message message = snapshot.getValue(Message.class);
//                        if (messageList.contains(message)){
//                            messageList.remove(message);
//                            adapter.notifyDataSetChanged();
//                            isChildRemove2 = true;
//                        }
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setRecyclerView(){
        adapter = new FirebasePaginationAdapter(this,messageList);
        layoutManager = new SpeedyLinearLayoutManager(getApplicationContext(), SpeedyLinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollOutItems = layoutManager.findFirstCompletelyVisibleItemPosition();
                if (isScrolling && scrollOutItems == 0){
//                    query();
                    if (messageList.size()>0) {
                        lastMessageKey = messageList.get(0).getMessageKey();
                        index = 0;
                        fetchMoreDataFromServer();
                        Log.d("WERT","lastMessageKey"+lastMessageKey);
                    }
                    isScrolling = false;
                }
            }
        });
    }



    private void sendMessage(){
        String message = messageEditText.getText().toString();
        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Enter Message", Toast.LENGTH_SHORT).show();
        }else {
            sendMessageToFirebase(message);
//            messageEditText.setText("");
        }
    }



    private void sendMessageToFirebase(String message){
       final String messageKey = firebaseDatabaseRef.push().getKey();
       firebaseDatabaseRef.child(messageKey).setValue(new Message(message,messageKey))
               .addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful()){
                           Log.d("ABCD","Push Key = "+messageKey);
                       }else {
                           Log.d("ABCD",task.getException().getMessage());
                       }
                   }
               });
    }

    private void setPaginationKey(){
        firebaseDatabaseRef.limitToLast(initialPageSize +1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()){
                            lastMessageKey = child.getKey();
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


//    private void query(){
//        if (lastMessageKey != null){
//            firebaseDatabaseRef.orderByKey().endAt(lastMessageKey).limitToLast(pageSize + 1)
//                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            List<Message> list = new ArrayList<>();
//                            int index = 0;
//                            for (DataSnapshot child : snapshot.getChildren()){
//                                if (index == 0 ) {
//                                    lastMessageKey = child.getKey();
//                                    index++;
//                                }else {
//                                    Message message = child.getValue(Message.class);
//                                    message.setMessageKey(child.getKey());
//                                    list.add(message);
//                                }
//                            }
//                            if (list.size()>0) {
//                                messageList.addAll(0, list);
//                                adapter.notifyDataSetChanged();
//                                recyclerView.smoothScrollToPosition((int) snapshot.getChildrenCount()+3);
//                            }
//                        }
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//        }else {
//            setPaginationKey();
//        }
//
//
//    }

    @Override
    public void onDeleteIconClick(final Message message) {
        if (message != null){
            isChildRemove1 = true;
            isChildRemove2 = true;
            Log.d("CVBN","onDeleteIconClick() is called");
            firebaseDatabaseRef.child(message.getMessageKey()).removeValue(new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error == null){
                        messageList.remove(message);
                        adapter.notifyDataSetChanged();
                    }else {
                        isChildRemove1 = false;
                        isChildRemove2 = false;
                    }
                }
            });
        }
    }

    /////////////////////////////////////////////////
    public static class SpeedyLinearLayoutManager extends LinearLayoutManager {

        private static final float MILLISECONDS_PER_INCH = 5f; //default is 25f (bigger = slower)

        public SpeedyLinearLayoutManager(Context context) {
            super(context);
        }

        public SpeedyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public SpeedyLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {

            final LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {

                @Override
                public PointF computeScrollVectorForPosition(int targetPosition) {
                    return super.computeScrollVectorForPosition(targetPosition);
                }

                @Override
                protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                    return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                }
            };

            linearSmoothScroller.setTargetPosition(position);
            startSmoothScroll(linearSmoothScroller);
        }
    }


}