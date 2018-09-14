package ade.leke.com.trackguard.model;

/**
 * Created by SecureUser on 10/28/2015.
 */
public class MovementListModel {

    private  String date="";
    private  String address="";


    /*********** Set Methods ******************/

    public void setDate(String date)
    {
        this.date = date;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }


    /*********** Get Methods ****************/

    public String getDate()
    {
        return this.date;
    }

    public String getAddress()
    {
        return this.address;
    }



}
