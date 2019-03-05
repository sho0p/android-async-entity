package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.InetAddressUtils;
import edu.stevens.cs522.chatserver.contracts.PeerContract;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable, Persistable {

    // Will be database key
    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    // Where we heard from them
    public InetAddress address;

    public Peer() {
    }

    public Peer(Cursor cursor) {
        this.name = PeerContract.getName(cursor);
        this.timestamp = new Date(PeerContract.getTimestamp(cursor));
        this.address = InetAddressUtils.fromString(PeerContract.getAddress(cursor).substring(1));
    }

    public Peer(Parcel in) throws UnknownHostException {
        byte[] ipIn = new byte[4];
        this.name = in.readString();
        this.timestamp = new Date(in.readLong());
        in.readByteArray(ipIn);
        this.address = InetAddress.getByAddress(ipIn);
    }

    @Override
    public void writeToProvider(ContentValues out) {
        PeerContract.putName(out, this.name);
        PeerContract.putTimestamp(out, this.timestamp.getTime());
        PeerContract.putAddress(out, this.address.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.name);
        DateUtils.writeDate(out, this.timestamp);
        out.writeByteArray(this.address.getAddress());
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {

        @Override
        public Peer createFromParcel(Parcel source) {
            try {
                return new Peer(source);
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }

    };
}
