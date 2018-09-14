package ade.leke.com.trackguard.model;

/**
 * Created by SecureUser on 1/29/2016.
 */
public class NewsDataObject {
    private String mText1;
    private String mText2;
    private String news;
    private String timeAgo;
    private String image;
    private String subject;

    public void setImage(String image) {
        this.image = image;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String getNews() {
        return news;
    }

    public String getImage() {
        return image;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setNews(String news) {
        this.news = news;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public NewsDataObject (String text1, String text2){
        mText1 = text1;
        mText2 = text2;
    }

    public NewsDataObject (){

    }

    public String getmText1() {
        return mText1;
    }

    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }

    public String getmText2() {
        return mText2;
    }

    public void setmText2(String mText2) {
        this.mText2 = mText2;
    }
}
