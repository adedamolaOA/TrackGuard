package ade.leke.com.trackguard.model;

/**
 * Created by SecureUser on 10/1/2015.
 */
public class NotificationListModel {
    private  String subject="";
    private  String message="";
    private  String date="";
    private  String viewStatus="";
    private  String rPhoneId="";

    /*********** Set Methods ******************/

    public void setPhoneId(String rPhoneId){
        this.rPhoneId = rPhoneId;
    }
    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public void setViewStatus(String viewStatus)
    {
        this.viewStatus = viewStatus;
    }

    public void setDate(String date){this.date = date;}

    /*********** Get Methods ****************/

    public String getSubject()
    {
        return this.subject;
    }

    public String getMessage()
    {
        return this.message;
    }

    public String getViewStatus()
    {
        return this.viewStatus;
    }

    public String getPhoneId(){return this.rPhoneId;}
    public String getDate(){return this.date;}
}
