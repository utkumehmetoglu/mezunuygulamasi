package com.utku.odev.entities;

public class News {

    private String newsId;
    private String newsText;

    private String userID;
    private long created_Millis;

    public News() {
        // Boş yapıcı metod gerekli Firebase deserialization işlemi için



    }

    public News(String newsId, String newsText, String userID) {
        this.newsId = newsId;
        this.newsText = newsText;
        this.userID = userID;
        this.created_Millis = System.currentTimeMillis();
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getNewsText() {
        return newsText;
    }

    public void setNewsText(String newsText) {
        this.newsText = newsText;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getCreated_Millis() {
        return created_Millis;
    }

    public void setCreated_Millis(long created_Millis) {
        this.created_Millis = created_Millis;
    }
}
