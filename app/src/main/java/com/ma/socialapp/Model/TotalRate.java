package com.ma.socialapp.Model;

public class TotalRate {

    private String userId, ratingCount;

    public TotalRate() {

    }

    public TotalRate(String userId, String ratingCount) {
        this.userId = userId;
        this.ratingCount = ratingCount;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(String ratingCount) {
        this.ratingCount = ratingCount;
    }
}
