package ade.leke.com.trackguard.model;

/**
 * Created by SecureUser on 9/19/2015.
 */
public class AgencyListModel {

    private  String name="";
    private  String image="";
    private  String description="";

    /*********** Set Methods ******************/

    public void setName(String name)
    {
        this.name = name;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public void setDescription(String description)
    {
        this.description = description;
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

    public String getDescription()
    {
        return this.description;
    }
}
