package com.ma.socialapp.Model;

public class Posts {

    private String userId, datePost, commentsNum, postDescription, imagePost, timePost, postId;

    public Posts() {
    }

    public Posts(String userId, String datePost, String commentsNum, String postDescription, String imagePost, String timePost, String postId) {
        this.userId = userId;
        this.datePost = datePost;
        this.commentsNum = commentsNum;
        this.postDescription = postDescription;
        this.imagePost = imagePost;
        this.timePost = timePost;
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDatePost() {
        return datePost;
    }

    public void setDatePost(String datePost) {
        this.datePost = datePost;
    }

    public String getCommentsNum() {
        return commentsNum;
    }

    public void setCommentsNum(String commentsNum) {
        this.commentsNum = commentsNum;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public String getImagePost() {
        return imagePost;
    }

    public void setImagePost(String imagePost) {
        this.imagePost = imagePost;
    }

    public String getTimePost() {
        return timePost;
    }

    public void setTimePost(String timePost) {
        this.timePost = timePost;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}