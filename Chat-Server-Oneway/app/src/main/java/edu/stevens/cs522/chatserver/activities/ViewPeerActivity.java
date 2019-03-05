package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.async.IQueryListener;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.managers.MessageManager;
import edu.stevens.cs522.chatserver.managers.TypedCursor;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity implements IQueryListener<Message> {

    public static final String PEER_KEY = "peer";

    private SimpleCursorAdapter peerAdapter;

    private MessageManager messageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        // TODO init the UI and initiate query of message database

        TextView username = findViewById(R.id.view_user_name);
        TextView lastseen = findViewById(R.id.view_timestamp);
        TextView address  = findViewById(R.id.view_address);
        ListView messages = findViewById(R.id.view_messages);

        username.setText(peer.name);
        lastseen.setText(peer.timestamp.toString());
        address.setText(peer.address.toString().substring(1));

        peerAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                null,
                new String[] {
                        MessageContract.SENDER,
                        MessageContract.MESSAGE_TEXT
                },
                new int[] {
                        android.R.id.text1,
                        android.R.id.text2
                }
        );

        messages.setAdapter(peerAdapter);


        messageManager = new MessageManager(this);
        messageManager.getMessagesByPeerAsync(peer, this);


    }

    @Override
    public void handleResults(TypedCursor<Message> results) {
        // TODO
        peerAdapter.swapCursor(results.getCursor());
        peerAdapter.notifyDataSetChanged();
    }

    @Override
    public void closeResults() {
        // TODO
        peerAdapter.swapCursor(null);
        peerAdapter.notifyDataSetChanged();
    }


}
