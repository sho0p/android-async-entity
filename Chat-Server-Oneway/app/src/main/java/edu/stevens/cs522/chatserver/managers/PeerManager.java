package edu.stevens.cs522.chatserver.managers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.CursorAdapter;

import edu.stevens.cs522.chatserver.async.AsyncContentResolver;
import edu.stevens.cs522.chatserver.async.IContinue;
import edu.stevens.cs522.chatserver.async.IEntityCreator;
import edu.stevens.cs522.chatserver.async.IQueryListener;
import edu.stevens.cs522.chatserver.async.QueryBuilder;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;


/**
 * Created by dduggan.
 */

public class PeerManager extends Manager<Peer> {

    private static final int LOADER_ID = 2;

    private static final IEntityCreator<Peer> creator = new IEntityCreator<Peer>() {
        @Override
        public Peer create(Cursor cursor) {
            return new Peer(cursor);
        }
    };

    public PeerManager(Context context) {
        super(context, creator, LOADER_ID);
    }

    private static final String ALL_PEERS_TAG = "All Peers";

    public void getAllPeersAsync(IQueryListener<Peer> listener) {
        // use QueryBuilder to complete this
        //QueryBuilder.executeQuery(ALL_PEERS_TAG, (Activity) context, PeerContract.CONTENT_URI, LOADER_ID, creator, listener);
        executeQuery(PeerContract.CONTENT_URI, listener);

    }

    public void persistAsync(Peer peer, final IContinue<Long> callback) {
        // use AsyncContentResolver to complete this

        AsyncContentResolver asyncResolver = getAsyncResolver();//new AsyncContentResolver(context.getContentResolver());
        ContentValues values = new ContentValues();
        peer.writeToProvider(values);
        asyncResolver.insertAsync(PeerContract.CONTENT_URI, values,
                new IContinue<Uri>() {
                    @Override
                    public void kontinue(Uri value) {
                        Log.d("async getID find", value.toString());
                        callback.kontinue(PeerContract.getId(value));
                    }
                });

    }

}
