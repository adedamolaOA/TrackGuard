package ade.leke.com.trackguard.model;

/**
 * Created by SecureUser on 9/18/2015.
 */
public class ContactListModel {

    private  String name="";
    private  String image="";
    private  String mobileNumber="";

    /*********** Set Methods ******************/

    public void setName(String name)
    {
        this.name = name;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public void setMobileNumber(String mobileNumber)
    {
        this.mobileNumber = mobileNumber;
    }

    /*********** Get Methods ****************/

    public String getName()
    {
        return this.name;
    }

    public String getImage()
    {
        return this.image;
    }

    public String getMobileNumber()
    {
        return this.mobileNumber;
    }
}
