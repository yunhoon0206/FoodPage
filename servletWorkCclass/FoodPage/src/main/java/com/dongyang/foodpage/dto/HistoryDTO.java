package com.dongyang.foodpage.dto;

import java.time.LocalDateTime; // Java 8 날짜 및 시간 API 사용

public class HistoryDTO {
    private int seq;
    private String memberId;
    private String recipeName;
    private String imgUrl;
    private LocalDateTime viewDate; // DATETIME에 매핑

    public HistoryDTO() {}

    public HistoryDTO(int seq, String memberId, String recipeName, String imgUrl, LocalDateTime viewDate) {
        this.seq = seq;
        this.memberId = memberId;
        this.recipeName = recipeName;
        this.imgUrl = imgUrl;
        this.viewDate = viewDate;
    }

    // Getters and Setters
    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public LocalDateTime getViewDate() {
        return viewDate;
    }

    public void setViewDate(LocalDateTime viewDate) {
        this.viewDate = viewDate;
    }

    @Override
    public String toString() {
        return "HistoryDTO{" +
               "seq=" + seq +
               ", memberId='" + memberId + "'" +
               ", recipeName='" + recipeName + "'" +
               ", imgUrl='" + imgUrl + "'" +
               ", viewDate=" + viewDate +
               '}';
    }
}
