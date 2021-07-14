package com.ma.socialapp.Model;

public class OpinionUsers {

    private String fromUserId, toUserId, opinionContent, ratingCount;

    private float rating;

    public OpinionUsers() {

    }

    public OpinionUsers(String fromUserId, String toUserId, String opinionContent, String ratingCount, float rating) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.opinionContent = opinionContent;
        this.ratingCount = ratingCount;
        this.rating = rating;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getOpinionContent() {
        return opinionContent;
    }

    public void setOpinionContent(String opinionContent) {
        this.opinionContent = opinionContent;
    }

    public String getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(String ratingCount) {
        this.ratingCount = ratingCount;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
