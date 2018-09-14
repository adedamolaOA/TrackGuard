package ade.leke.com.trackguard.db.db.entities;

/**
 * Created by SecureUser on 10/31/2015.
 */
public class Notification {

    private long nid;
    private String uid;
    private String contact;
    private String request;
    private String message;
    private String response;
    private String date;
    private String status;

    public void setNid(long nid){this.nid = nid;}
    public void setUid(String  uid){this.uid = uid;}
    public void setNotificationContact(String  contact){this.contact = contact;}
    public void setNotificationRequest(String  request){this.request = request;}
    public void setNotificationResponse(String  response){this.response = response;}
    public void setNotificationMessage(String  message){this.message = message;}
    public void setNotificationDate(String  date){this.date = date;}
    public void setNotificationStatus(String  status){this.status = status;}

    public long getNid(){return this.nid;}
    public String getUid(){return this.uid;}
    public String getNotificationContact(){return this.contact;}
    public String getNotificationRequest(){return this.request;}
    public String getNotificationResponse(){return this.response;}
    public String getNotificationMessage(){return this.message;}
    public String getNotificationDate(){return this.date;}
    public String getNotificationStatus(){return this.status;}

}
