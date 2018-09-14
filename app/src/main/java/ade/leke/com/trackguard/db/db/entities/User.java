package ade.leke.com.trackguard.db.db.entities;

/**
 * Created by SecureUser on 10/29/2015.
 */
public class User {

    private long id;
    private String firstname;
    private String lastname;
    private String mobile;
    private String email;
    private String status;
    private String sim;
    private String version;
    private String passCode;
    private String image;

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setId(long id){this.id = id;}
    public void setFirstname(String firstname){this.firstname = firstname;}
    public void setLastname(String lastname){this.lastname = lastname;}
    public void setMobileNumber(String mobile){this.mobile = mobile;}
    public void setEmail(String email){this.email = email;}
    public void setStatus(String status){this.status = status;}
    public void setSIM(String sim){this.sim = sim;}
    public void setVersion(String version){this.version=version;}
    public void setPassCode(String passCode){this.passCode = passCode;}

    public String getMobileNumber(){return this.mobile;}
    public String getFirstname(){return this.firstname;}
    public String getLastname(){return this.lastname;}
    public String getEmail(){return this.email;}
    public String getStatus(){return this.status;}
    public long getId(){return this.id;}
    public String getSim(){return this.sim;}
    public String getVersion(){return this.version;}
    public String getPassCode(){return this.passCode;}
}
