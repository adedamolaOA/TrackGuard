package ade.leke.com.trackguard.db.db.entities;

/**
 * Created by SecureUser on 10/30/2015.
 */
public class Movement {

    private String uid;
    private String lat;
    private String lng;
    private String address;
    private String date;
    private long _id;

    public void setMid(long _id){this._id = _id;}
    public void setUid(String uid){this.uid = uid;}
    public void setLat(String lat){this.lat = lat;}
    public void setLng(String lng){this.lng = lng;}
    public void setAddress(String address){this.address = address;}
    public void setMDate(String date){this.date = date;}

    public long getMid(){return this._id;}
    public String getUid(){return this.uid;}
    public String getLat(){return this.lat;}
    public String getLng(){return this.lng;}
    public String getAddress(){return this.address;}
    public String getMDate(){return this.date;}
}
