package com.ma.socialapp.Model;

public class ReviewUsersApp {

    private String userId, reviewContent;

    private float rating;

    public ReviewUsersApp() {

    }

    public ReviewUsersApp(String userId, String reviewContent, float rating) {
        this.userId = userId;
        this.reviewContent = reviewContent;
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
