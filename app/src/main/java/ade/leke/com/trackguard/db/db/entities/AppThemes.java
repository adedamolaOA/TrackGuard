package ade.leke.com.trackguard.db.db.entities;

/**
 * Created by SecureUser on 1/29/2016.
 */
public class AppThemes {

    private String primaryColor;
    private String primaryColorDark;
    private String background;
    private String name;
    private String status;
    private long id;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setPrimaryColorDark(String primaryColorDark) {
        this.primaryColorDark = primaryColorDark;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public String getPrimaryColorDark() {
        return primaryColorDark;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackground() {
        return background;
    }

    public String getName() {
        return name;
    }
}
