package com.ma.socialapp.Model;

public class CommentModel {

    private String commentId, userId, commentContent, postId, commentDate, commentTime, imageComment;

    public CommentModel() {

    }

    public CommentModel(String commentId, String userId, String commentContent, String postId, String commentDate, String commentTime, String imageComment) {
        this.commentId = commentId;
        this.userId = userId;
        this.commentContent = commentContent;
        this.postId = postId;
        this.commentDate = commentDate;
        this.commentTime = commentTime;
        this.imageComment = imageComment;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getImageComment() {
        return imageComment;
    }

    public void setImageComment(String imageComment) {
        this.imageComment = imageComment;
    }
}
