package edu.stevens.cs522.chatserver.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import edu.stevens.cs522.chatserver.contracts.BaseContract;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;

public class ChatProvider extends ContentProvider {

    public ChatProvider() {

    }

    private static final String AUTHORITY = BaseContract.AUTHORITY;

    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;

    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;

    private static final String PEER_CONTENT_PATH = PeerContract.CONTENT_PATH;

    private static final String PEER_CONTENT_PATH_ITEM = PeerContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "chat.db";

    private static final int DATABASE_VERSION = 1;

    private static final String MESSAGES_TABLE = "Message";

    private static final String PEERS_TABLE = "Peer";

    // Create the constants used to differentiate between the different URI  requests.
    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int PEERS_ALL_ROWS = 3;
    private static final int PEERS_SINGLE_ROW = 4;

    public static class DbHelper extends SQLiteOpenHelper {

        private static final String MESSAGE_DATABASE_CREATE =
                "CREATE TABLE IF NOT EXISTS " +
                        MESSAGES_TABLE + " (" +
                        MessageContract.MESSAGE_TEXT + " TEXT NOT NULL, " +
                        MessageContract.TIMESTAMP + " LONG NOT NULL, " +
                        MessageContract.SENDER + " TEXT NOT NULL, " +
                        MessageContract.SENDER_ID + " INTEGER NOT NULL, " +
                        MessageContract.ID + " INTEGER PRIMARY KEY, " +
                        "FOREIGN KEY (" + MessageContract.SENDER_ID+ ") REFERENCES " + PEERS_TABLE + "(" + PeerContract.ID + ") ON DELETE CASCADE" +
                        "); ";
        private static final String PEER_DATABASE_CREATE =
                "CREATE TABLE IF NOT EXISTS " +
                        PEERS_TABLE + "( " +
                        PeerContract.ID + " INTEGER PRIMARY KEY, " +
                        PeerContract.NAME + " TEXT NOT NULL, " +
                        PeerContract.TIMESTAMP + " TEXT NOT NULL, " +
                        PeerContract.ADDRESS + " TEXT NOT NULL" +
                        "); ";
        private static final String MESSAGES_INDEX_CREATE =
                "CREATE INDEX " + MessageContract.MESSAGES_PEER_INDEX + " ON " + MESSAGES_TABLE + "(" + MessageContract.SENDER_ID + ");";

        private static final String PEER_INDEX_CREATE =
                "CREATE INDEX " + PeerContract.PEER_NAME_INDEX + " ON " + PEERS_TABLE + "(" + PeerContract.NAME + ");";

        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(MESSAGE_DATABASE_CREATE);
            db.execSQL(PEER_DATABASE_CREATE);
            db.execSQL(MESSAGES_INDEX_CREATE);
            db.execSQL(PEER_INDEX_CREATE);
            db.execSQL("PRAGMA foreign_keys=ON;");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + PEERS_TABLE);
            onCreate(db);
        }
    }

    private DbHelper dbHelper;


    @Override
    public boolean onCreate() {
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        // at the given URI.
        switch(uriMatcher.match(uri)){
            case MESSAGES_ALL_ROWS:
                return "messages";
            case MESSAGES_SINGLE_ROW:
                return "message";
            case PEERS_ALL_ROWS:
                return "peers";
            case PEERS_SINGLE_ROW:
                return "peer";
            default:
                return null;
        }
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentResolver cr = getContext().getContentResolver();
        Log.d("Debug", "Things are getting inserted " + uriMatcher.match(uri));
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // Make sure to notify any observers
                long mess_row = db.insert(MESSAGES_TABLE, null, values);
                if(mess_row >= 0){
                    Uri instanceUri = MessageContract.CONTENT_URI(mess_row);
                    Log.d("Debug", "message instanceUri = " + instanceUri.toString());
                    cr.notifyChange(instanceUri, null);
                    return instanceUri;
                }
                throw new UnsupportedOperationException("Not Supported Input: " + mess_row);
            case PEERS_ALL_ROWS:
                String selection = PeerContract.NAME + "=?";
                String[] selectionArgs = {values.getAsString(PeerContract.NAME)};
                if(!query(uri, null, selection, selectionArgs, null).moveToFirst()) {
                    long peerrow = db.insert(PEERS_TABLE, null, values);
                    if (peerrow > 0) {
                        Uri instanceUri = PeerContract.CONTENT_URI(peerrow);
                        Log.d("Debug", "peer instanceUri = " + instanceUri.toString());
                        cr.notifyChange(uri, null);
                        return instanceUri;
                    }
                }
                Uri instanceUri = PeerContract.CONTENT_URI(update(uri, values, selection, selectionArgs));
                Log.d("Debug", "peer instanceUri = " + instanceUri.toString());
                return  instanceUri;
               // return PeerContract.CONTENT_URI(db.insertWithOnConflict(PEERS_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE));
            case MESSAGES_SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                Cursor mess_all_curs=db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                if(mess_all_curs.moveToFirst()){
                    do{
                        Log.d("Debug Provider", mess_all_curs.getString(mess_all_curs.getColumnIndexOrThrow(MessageContract.MESSAGE_TEXT)));
                        Log.d("PeerIdQuery", Long.toString(mess_all_curs.getLong(mess_all_curs.getColumnIndexOrThrow(MessageContract.SENDER_ID))));
                    }while(mess_all_curs.moveToNext());
                }
                mess_all_curs.setNotificationUri(getContext().getContentResolver(), uri);
                return mess_all_curs;
            case PEERS_ALL_ROWS:
                Cursor peer_all_curs=db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                peer_all_curs.setNotificationUri(getContext().getContentResolver(), uri);
                if(peer_all_curs.moveToFirst()){
                    do{
                        Log.d("PeerQuery", Long.toString(peer_all_curs.getLong(peer_all_curs.getColumnIndexOrThrow(PeerContract.ID))));
                    }while(peer_all_curs.moveToNext());
                }
                return peer_all_curs;
            case MESSAGES_SINGLE_ROW:
                Cursor mess_sing_row=db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                mess_sing_row.setNotificationUri(getContext().getContentResolver(), uri);
                return mess_sing_row;
            case PEERS_SINGLE_ROW:
                Cursor peer_sing_row = db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                peer_sing_row.setNotificationUri(getContext().getContentResolver(), uri);
                return peer_sing_row;
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.update(uri.getLastPathSegment(), values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String uripath = uri.toString();
        String table = uripath.split("/")[3];
        return db.delete(table, selection, selectionArgs);
    }

}
