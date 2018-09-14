package ade.leke.com.trackguard.db.db.entities;

/**
 * Created by SecureUser on 11/1/2015.
 */
public class Settings {

    private long _id;
    private String uid;
    private String pInterval;
    private String mInterval;
    private String mgs;
    private String sms;
    private String goDark;
    private String numberOfSMS;
    private String status;

    public void setSid(long _id){this._id = _id;}
    public void setUid(String uid){this.uid = uid;}
    public void setpInterval(String pInterval){this.pInterval = pInterval;}
    public void setmInterval(String mInterval){this.mInterval = mInterval;}
    public void setMGS(String mgs){this.mgs = mgs;}
    public void setSMS(String sms){this.sms = sms;}
    public void setGoDark(String goDark){this.goDark = goDark;}
    public void setNumberOfSMS(String numberOfSMS){this.numberOfSMS=numberOfSMS;}
    public void setSettingsStatus(String status){this.status = status;}

    public long getSid(){return this._id;}
    public String getUid(){return this.uid;}
    public String getpInterval(){return this.pInterval;}
    public String getmInterval(){return this.mInterval;}
    public String getMGS(){return this.mgs;}
    public String getSMS(){return this.sms;}
    public String getGoDark(){return this.goDark;}
    public String getNumberOfSMS(){return this.numberOfSMS;}
    public String getSettingsStatus(){return this.status;}
}
