package com.ma.socialapp.Model;

public class ReportsModel {

    private String fromUserId, toUserId, reportId, reportContent, imageReport, postId, commentId;

    public ReportsModel() {

    }

    public ReportsModel(String fromUserId, String toUserId, String reportId, String reportContent, String imageReport, String postId, String commentId) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.reportId = reportId;
        this.reportContent = reportContent;
        this.imageReport = imageReport;
        this.postId = postId;
        this.commentId = commentId;
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

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    public String getImageReport() {
        return imageReport;
    }

    public void setImageReport(String imageReport) {
        this.imageReport = imageReport;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }
}
