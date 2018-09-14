package ade.leke.com.trackguard.db.db.entities;

/**
 * Created by SecureUser on 11/1/2015.
 */
public class Panic {

    private long _id;
    private String uid;
    private String lat;
    private String lng;
    private String address;
    private String date;
    private String status;

    public void setPid(long _id){this._id = _id;}
    public void setUid(String uid){this.uid = uid;}
    public void setLat(String lat){this.lat = lat;}
    public void setLng(String lng){this.lng = lng;}
    public void setAddress(String address){this.address = address;}
    public void setDate(String date){this.date = date;}
    public void setStatus(String status){this.status = status;}

    public long getPid(){return this._id;}
    public String getUid(){return this.uid;}
    public String getLat(){return this.lat;}
    public String getLng(){return this.lng;}
    public String getAddress(){return this.address;}
    public String getDate(){return this.date;}
    public String getStatus(){return this.status;}


}
