package ade.leke.com.trackguard.db.db.entities;

/**
 * Created by SecureUser on 1/29/2016.
 */
public class News {

    private long id;
    private String uuid;
    private String client;
    private String bulletin;
    private String author;
    private String subject;
    private String news;
    private String image;
    private String date;
    private String status;

    public String getAuthor() {
        return author;
    }

    public String getBulletin() {
        return bulletin;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setBulletin(String bulletin) {
        this.bulletin = bulletin;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getClient() {
        return client;
    }

    public String getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public String getNews() {
        return news;
    }

    public String getSubject() {
        return subject;
    }

    public String getUuid() {
        return uuid;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
