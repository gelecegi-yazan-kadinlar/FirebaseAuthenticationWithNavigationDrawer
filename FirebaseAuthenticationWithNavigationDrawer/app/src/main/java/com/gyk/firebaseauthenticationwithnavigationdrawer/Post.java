package com.gyk.firebaseauthenticationwithnavigationdrawer;

public class Post {
    private String title;
    private String content;
    private int photo;

    public Post() {
    }

    public Post(String title, String content, int photo) {
        this.title = title;
        this.content = content;
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }
}
