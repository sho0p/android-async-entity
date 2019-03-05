/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;
import java.util.List;

import edu.stevens.cs522.base.DatagramSendReceive;
import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.async.IContinue;
import edu.stevens.cs522.chatserver.async.IQueryListener;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.managers.MessageManager;
import edu.stevens.cs522.chatserver.managers.PeerManager;
import edu.stevens.cs522.chatserver.managers.TypedCursor;
import edu.stevens.cs522.chatserver.providers.ChatProvider;

import static edu.stevens.cs522.chatserver.contracts.BaseContract.getId;

public class ChatServer extends Activity implements OnClickListener, IQueryListener<Message> {

	final static public String TAG = ChatServer.class.getCanonicalName();
		
	/*
	 * Socket used both for sending and receiving
	 */
    private DatagramSendReceive serverSocket;
    //  private DatagramSocket serverSocket;
	/*
	 * True as long as we don't get socket errors
	 */
	private boolean socketOK = true; 

    /*
     * UI for displayed received messages
     */

	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private MessageManager messageManager;

    private PeerManager peerManager;

    private Button next;


	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * Let's be clear, this is a HACK to allow you to do network communication on the messages thread.
         * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
         * this right in a future assignment (using a Service managing background threads).
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

		try {
            int port = getResources().getInteger(R.integer.app_port);

            serverSocket = new DatagramSendReceive(port);

		} catch (Exception e) {
            throw new IllegalStateException("Cannot open socket", e);
		}

        setContentView(R.layout.messages);


        messageList = (ListView) findViewById(R.id.message_list);

        messagesAdapter = new SimpleCursorAdapter(
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

        messageList.setAdapter(messagesAdapter);


        next = (Button) findViewById(R.id.next);
        next.setOnClickListener(this);


    //    ChatProvider chatProvider = new ChatProvider(this);

        messageManager = new MessageManager(this);
        peerManager = new PeerManager(this);

        messageManager.getAllMessagesAsync(this);

	}

    public void onClick(View v) {


     //   messageManager.getAllMessagesAsync(this);

        byte[] receiveData = new byte[1024];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        try {

            serverSocket.receive(receivePacket);
            Log.i(TAG, "Received a packet");

            InetAddress sourceIPAddress = receivePacket.getAddress();
            Log.i(TAG, "Source IP Address: " + sourceIPAddress);

            String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(":");

            final Message message = new Message();
            message.sender = msgContents[0];
            message.timestamp = new Date(Long.parseLong(msgContents[1]));
            message.messageText = msgContents[2];

            Log.i(TAG, "Received from " + message.sender + ": " + message.messageText);

            final Peer sender = new Peer();
            sender.name = message.sender;
            sender.timestamp = message.timestamp;
            sender.address = receivePacket.getAddress();


         //   sender.id = sender.name.hashCode();

          //  if (sender.id < 0) sender.id = -1 * sender.id;

          //  message.senderId = sender.id;
         //   message.id = (message.sender + message.timestamp + message.messageText).hashCode();
          //  if (message.id < 0) message.id = -1 * message.id;


  //          messageManager.getAllMessagesAsync(this);
           // messageManager.persistAsync(message);
            peerManager.persistAsync(sender,
                    new IContinue<Long>() {
                        @Override
                        public void kontinue(Long id) {
                            Log.d("Returned id from peer", id.toString());
                            message.senderId = id;
                            Log.d("Message senderID", Long.toString(message.senderId));
                            messageManager.persistAsync(message);
                        }
                   });
            Log.d(TAG, "messenger sender id: " + message.senderId);
            messageManager.getAllMessagesAsync(this);

            // For this assignment, must use managers to do this asynchronously


        } catch (Exception e) {

            Log.e(TAG, "Problems receiving packet: " + e.getMessage());
            socketOK = false;
        }

    }

    /*
     * Close the socket before exiting application
     */
    public void closeSocket() {
        if (serverSocket != null) {
            serverSocket.close();
            serverSocket = null;
        }
    }

    /*
     * If the socket is OK, then it's running
     */
    boolean socketIsOK() {
        return socketOK;
    }

    /**
     * Callbacks for query builder (which itself handles LM callbacks)
     */

    @Override
    public void handleResults(TypedCursor<Message> results) {
        this.messagesAdapter.swapCursor(results.getCursor());
        this.messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void closeResults() {
        this.messagesAdapter.swapCursor(null);
        this.messagesAdapter.notifyDataSetChanged();
    }

    public void onDestroy() {
        super.onDestroy();
        closeSocket();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatserver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            case R.id.peers:
                Intent intent = new Intent(this, ViewPeersActivity.class);
                startActivity(intent);
                break;

            default:
        }
        return false;
    }

}