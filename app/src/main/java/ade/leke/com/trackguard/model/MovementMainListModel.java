package ade.leke.com.trackguard.model;

/**
 * Created by SecureUser on 11/6/2015.
 */

public class MovementMainListModel {

    private String title;
    private String description;
    private boolean status;


    /***********
     * Set Methods
     ******************/

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    /***********
     * Get Methods
     ****************/

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean getStatus() {
        return this.status;
    }



}
