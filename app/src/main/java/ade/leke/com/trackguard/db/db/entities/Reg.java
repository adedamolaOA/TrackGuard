package ade.leke.com.trackguard.db.db.entities;

import java.util.Date;

/**
 * Created by SecureUser on 10/29/2015.
 */
public class Reg {

    private long id;
    private Date date;
    private Date updateDate;
    private String isUpdated;
    private String owing;
    private String payService;

    public void setRegId(long id){this.id = id;}
    public void setRegDate(Date date){this.date = date;}
    public void setUpdateDate(Date updateDate){this.updateDate = updateDate;}
    public void setIsUpdated(String isUpdated){this.isUpdated = isUpdated;}
    public void setOwing(String owing){this.owing = owing;}
    public void setPayService(String payService){this.payService = payService;}

    public long getRegId(){return this.id;}
    public Date getRegDate(){return this.date;}
    public Date getUpdateDate(){return this.updateDate;}
    public String getIsUpdated(){return this.isUpdated;}
    public String getOwing(){return this.owing;}
    public String getPayService(){return this.payService;}
}
