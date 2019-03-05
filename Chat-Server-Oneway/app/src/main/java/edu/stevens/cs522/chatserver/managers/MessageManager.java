package edu.stevens.cs522.chatserver.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import edu.stevens.cs522.chatserver.async.AsyncContentResolver;
import edu.stevens.cs522.chatserver.async.IContinue;
import edu.stevens.cs522.chatserver.async.IEntityCreator;
import edu.stevens.cs522.chatserver.async.IQueryListener;
import edu.stevens.cs522.chatserver.async.QueryBuilder;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

import static edu.stevens.cs522.chatserver.contracts.BaseContract.getId;


/**
 * Created by dduggan.
 */

public class MessageManager extends Manager<Message> {

    private static final int LOADER_ID = 1;

    private static final IEntityCreator<Message> creator = new IEntityCreator<Message>() {
        @Override
        public Message create(Cursor cursor) {
            return new Message(cursor);
        }
    };


    public MessageManager(Context context) {
        super(context, creator, LOADER_ID);
    }

    public void getAllMessagesAsync(IQueryListener<Message> listener) {
       // QueryBuilder.executeQuery(ALL_MESS_ASYNC, (Activity)context, MessageContract.CONTENT_URI, LOADER_ID, creator, listener);
        reexecuteQuery(MessageContract.CONTENT_URI, MessageContract.PROJECTION, null, null, MessageContract.TIMESTAMP, listener);
    }

    public void getMessagesByPeerAsync(Peer peer, IQueryListener<Message> listener) {
        // Remember to reset the loader!
        String selection = MessageContract.SENDER_ID + "=?";
        String[] selectionArgs = {Long.toString(peer.id)};
        Log.d("Peer Id Searched By", selectionArgs[0]);
        reexecuteQuery(MessageContract.CONTENT_URI, MessageContract.PROJECTION, selection, selectionArgs, MessageContract.TIMESTAMP, listener);
    }

    public void persistAsync(final Message Message) {
        AsyncContentResolver asyncResolver = getAsyncResolver();//new AsyncContentResolver(context.getContentResolver());
        ContentValues values = new ContentValues();
        Message.writeToProvider(values);
        asyncResolver.insertAsync(MessageContract.CONTENT_URI, values,
                new IContinue<Uri>() {
                    @Override
                    public void kontinue(Uri value) {
                        Message.id = getId(value);
                    }
                });
    }

}
