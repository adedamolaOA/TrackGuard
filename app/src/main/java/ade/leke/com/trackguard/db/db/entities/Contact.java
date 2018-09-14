package ade.leke.com.trackguard.db.db.entities;

/**
 * Created by SecureUser on 10/29/2015.
 */
public class Contact {

    private long id;
    private String uid;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String status;


    public void setContactId(long id){this.id = id;}
    public void setUid(String uid){this.uid = uid;}
    public void setFirstname(String firstname){this.firstname = firstname;}
    public void setLastname(String lastname){this.lastname = lastname;}
    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}
    public void setStatus(String status){this.status = status;}



    public long getContactId(){return this.id;}
    public String getUid(){return  this.uid;}
    public String getFirstname(){return  this.firstname;}
    public String getLastname(){return this.lastname;}
    public String getPhoneNumber(){return  this.phoneNumber;}
    public String getStatus(){return this.status;}



}
