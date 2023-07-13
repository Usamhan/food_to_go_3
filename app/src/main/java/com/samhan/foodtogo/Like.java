package com.samhan.foodtogo;

public class Like {
    String postID ;
    String userID ;

    public Like(String postID , String userID ) {
        this.userID = userID ;
        this.postID = postID;
    }
    public Like() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostID() {
        return postID;
    }
    public void setPostID(String postID) {
        this.postID = postID;
    }
}
