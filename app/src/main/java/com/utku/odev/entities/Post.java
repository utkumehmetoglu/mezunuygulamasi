package com.utku.odev.entities;

public class Post {
    private String id;
    private String uid;

    private long created_Millis;
    private String comment;

    public Post(){

    }

    public Post(String id, String uid, String comment) {
        this.id = id;
        this.uid = uid;
        this.comment = comment;
        created_Millis = System.currentTimeMillis();

    }

    public long getCreated_Millis() {
        return created_Millis;
    }

    public void setCreated_Millis(long created_Millis) {
        this.created_Millis = created_Millis;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUid() {
        return uid;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
