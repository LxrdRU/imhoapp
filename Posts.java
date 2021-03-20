package com.example.imho_socialv101;

public class Posts {

    public String uid, time, post, date, postimage, gamename, fullname;



    public Posts(){

    }





    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }


    public String getGamename() {
        return gamename;
    }

    public void setGamename(String gamename) {
        this.gamename = gamename;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Posts(String uid, String time, String post, String date, String postimage, String gamedes, String gamename, String fullname) {
        this.uid = uid;
        this.time = time;
        this.post = post;
        this.date = date;
        this.postimage = postimage;
        this.gamename = gamename;
        this.fullname = fullname;
    }
}
