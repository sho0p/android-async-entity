package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chatserver.contracts.MessageContract;

/**
 * Created by dduggan.
 */

public class Message implements Parcelable, Persistable {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public long senderId;

    public Message() {
    }

    public Message(Cursor cursor) {
       // this.id = MessageContract.getId(cursor);
        this.messageText = MessageContract.getMessageText(cursor);
        this.timestamp = new Date(MessageContract.getTimestamp(cursor));
        this.sender = MessageContract.getSender(cursor);
        this.senderId = MessageContract.getSenderId(cursor);
    }

    public Message(Parcel in) {
     //   this.id = in.readLong();
        this.messageText = in.readString();
        this.timestamp = new Date(in.readLong());
        this.sender = in.readString();
        this.senderId = in.readLong();
    }

    @Override
    public void writeToProvider(ContentValues out) {
     //   MessageContract.putId(out, this.id);
        MessageContract.putMessageText(out, this.messageText);
        MessageContract.putTimestamp(out, this.timestamp.getTime());
        MessageContract.putSender(out, this.sender);
        MessageContract.putSenderId(out, this.senderId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    //    dest.writeLong(this.id);
        dest.writeString(this.messageText);
        dest.writeLong(this.timestamp.getTime());
        dest.writeString(this.sender);
        dest.writeLong(this.senderId);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        @Override
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }

    };

}

