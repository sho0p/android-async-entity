package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.InetAddress;

import static edu.stevens.cs522.chatserver.contracts.BaseContract.withExtendedPath;

/**
 * Created by dduggan.
 */

public class PeerContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Peer");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public static final String ID = _ID;

    public static final String NAME = "name";

    public static final String TIMESTAMP  = "timestamp";

    public static final String ADDRESS = "address";

    public static final String PEER_NAME_INDEX = "PeerNameIndex";


    private static int idColumn = -1;
    private static int nameColumn = -1;
    private static int timestampColumn = -1;
    private static int addressColumn = -1;

    public static long getId (Cursor cursor){
        if(idColumn < 0){
            idColumn = cursor.getColumnIndexOrThrow(ID);
        }
        return cursor.getLong(idColumn);
    }

    public  static void putId(ContentValues out, long id){
        out.put(ID, id);
    }

    public static String getName(Cursor cursor){
        if (nameColumn < 0){
            nameColumn = cursor.getColumnIndexOrThrow(NAME);
        }
        return cursor.getString(nameColumn);
    }

    public static void putName (ContentValues out, String name){
        out.put(NAME, name);
    }

    public static long getTimestamp (Cursor cursor){
        if(timestampColumn < 0){
            timestampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return cursor.getLong(timestampColumn);
    }

    public static void putTimestamp (ContentValues out, long time){
        out.put(TIMESTAMP, time);
    }

    public static String getAddress(Cursor cursor){
        if(addressColumn < 0){
            addressColumn = cursor.getColumnIndexOrThrow(ADDRESS);
        }
        return cursor.getString(addressColumn);
    }

    public static void putAddress(ContentValues out, String address){
        out.put(ADDRESS, address);
    }

}
