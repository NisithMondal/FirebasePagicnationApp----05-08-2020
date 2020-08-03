package com.nisith.firebasepagicnationapp.Model;

import android.util.Log;

import java.util.Objects;

public class Message {
    private String message;
    private String messageKey;

    public Message(){
    }
    public Message(String message, String messageKey){
        this.message = message;
        this.messageKey = messageKey;
    }


    public String getMessage(){
        return message;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public boolean equals(Object object) {
        boolean result = false;
        Message message1 = (Message) object;
//        Log.d("CVBN","v = "+this.getMessageKey());
        if (this.messageKey.equals(message1.messageKey)){
           result = true;
        }
        return result;
    }
}
