package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dduggan.
 */

public class MessageContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Message");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public static final String ID = _ID;

    public static final String MESSAGE_TEXT = "message_text";

    public static final String TIMESTAMP = "timestamp";

    public static final String SENDER = "sender";

    public static final String SENDER_ID = "sender_id";

    public static final String MESSAGES_PEER_INDEX = "MessagesPeerIndex";




    public static final String[] PROJECTION = {ID, MESSAGE_TEXT, TIMESTAMP, SENDER };

    private static int messageTextColumn = -1;
    private static int timeStampColumn = -1;
    private static int senderColumn = -1;
    private static int idColumn = -1;
    private static int senderIdColumn = -1;

    public static String getMessageText(Cursor cursor) {
        if (messageTextColumn < 0) {
            messageTextColumn = cursor.getColumnIndexOrThrow(MESSAGE_TEXT);
        }
        return cursor.getString(messageTextColumn);
    }

    public static void putMessageText(ContentValues out, String messageText) {
        out.put(MESSAGE_TEXT, messageText);
    }


    public static long getTimestamp(Cursor cursor){
        if (timeStampColumn < 0){
            timeStampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return cursor.getLong(timeStampColumn);
    }

    public static void putTimestamp(ContentValues out, long timeStamp){
        out.put(TIMESTAMP, timeStamp);
    }

    public static String getSender(Cursor cursor){
        if (senderColumn < 0){
            senderColumn = cursor.getColumnIndexOrThrow(SENDER);
        }
        return cursor.getString(senderColumn);
    }

    public static void putSender(ContentValues out, String sender){
        out.put(SENDER, sender);
    }

    public static long getId(Cursor cursor){
        if(idColumn < 0){
            idColumn = cursor.getColumnIndexOrThrow(ID);
        }
        return cursor.getLong(idColumn);
    }

    public static void putId(ContentValues out, long id){
        out.put(ID, id);
    }

    public static long getSenderId(Cursor cursor){
        if (senderIdColumn < 0){
            senderColumn = cursor.getColumnIndexOrThrow(ID);
        }
        return cursor.getLong(idColumn);
    }

    public static void putSenderId(ContentValues out, long senderId){
        out.put(SENDER_ID, senderId);
    }
}
