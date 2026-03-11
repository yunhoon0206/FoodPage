package com.dongyang.foodpage.dto;

public class ParsedIngredientDTO {
    private String name;   // 재료명
    private String rawAmount; // 용량
    private String group;  // 그룹명 (예: 샐러드, 드레싱, 양념 등)
    private double quantity;  // 계산 가능한 용량
    private String unit;
    
    public ParsedIngredientDTO() {}
    public ParsedIngredientDTO(String name, String rawAmount, String group, double quantity, String unit)
    {
        this.name = name;
        this.rawAmount = rawAmount;
        this.group = group;
        this.quantity = quantity;
        this.unit = unit;
    }
    public ParsedIngredientDTO(FridgeItemDTO f)
    {
    	this.name = f.getItemName();
    	this.rawAmount = f.getQuantity() + f.getDefaultUnit();
    	this.group = f.getCategory();
    	
    	this.quantity = f.getQuantity();
    	this.unit = f.getDefaultUnit();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return rawAmount;
    }

    public void setAmount(String amount) {
        this.rawAmount = amount;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

	public double getQuantity() {
		return quantity;
	}
	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}
	
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
}