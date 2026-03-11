package com.dongyang.foodpage.dto;

import java.util.Date; 

public class FridgeItemDTO {
    // fridge 테이블 정보
    private String memberId;
    private int itemId;
    private double quantity; // double로 수정
    private Date expiryDate;
    private String note;
    
    // ingredients_master 테이블에서 JOIN으로 가져올 정보
    private String itemName;
    private String defaultUnit;
    private String category; // [추가: 식재료/조미료 분류]
    
    public FridgeItemDTO() {}

    // Getter와 Setter (변경된 부분만 표시)

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getDefaultUnit() { return defaultUnit; }
    public void setDefaultUnit(String defaultUnit) { this.defaultUnit = defaultUnit; }
    
    public String getCategory() { return category; } // [추가]
    public void setCategory(String category) { this.category = category; } // [추가]
}