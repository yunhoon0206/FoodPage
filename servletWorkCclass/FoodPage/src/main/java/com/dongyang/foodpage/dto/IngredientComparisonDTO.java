package com.dongyang.foodpage.dto;

public class IngredientComparisonDTO
{
	private String ingredientName; // 재료명
    private double requiredQuantity; // 레시피 요구 수량
    private String requiredUnit; // 레시피 요구 단위
    private double availableQuantity; // 계산 가능한 냉장고 재고 수량
    private String availableUnit; // 계산에 사용된 냉장고 재고 단위
    private boolean isSufficient; // 재고 충족 여부 (true/false)
    private String comparisonMessage; // 사용자에게 보여줄 메시지 (예: "충분함", "50g 부족", "단위 불일치")
    private int fridgeItemId; // 냉장고 아이템 아이디
    
    public IngredientComparisonDTO(String name, double reqQty, String reqUnit, 
            double availQty, String availUnit, boolean isSuf, String msg, int fridgeItemId) {
    	this.ingredientName = name;
    	this.requiredQuantity = reqQty;
    	this.requiredUnit = reqUnit;
    	this.availableQuantity = availQty;
    	this.availableUnit = availUnit;
    	this.isSufficient = isSuf;
    	this.comparisonMessage = msg;
    	this.fridgeItemId = fridgeItemId;
    }
    
	public String getIngredientName() {
		return ingredientName;
	}
	public double getRequiredQuantity() {
		return requiredQuantity;
	}
	public String getRequiredUnit() {
		return requiredUnit;
	}
	public double getAvailableQuantity() {
		return availableQuantity;
	}
	public String getAvailableUnit() {
		return availableUnit;
	}
	public boolean isSufficient() {
		return isSufficient;
	}
	public String getComparisonMessage() {
		return comparisonMessage;
	}
	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}
	public void setRequiredQuantity(double requiredQuantity) {
		this.requiredQuantity = requiredQuantity;
	}
	public void setRequiredUnit(String requiredUnit) {
		this.requiredUnit = requiredUnit;
	}
	public void setAvailableQuantity(double availableQuantity) {
		this.availableQuantity = availableQuantity;
	}
	public void setAvailableUnit(String availableUnit) {
		this.availableUnit = availableUnit;
	}
	public void setSufficient(boolean isSufficient) {
		this.isSufficient = isSufficient;
	}
	public void setComparisonMessage(String comparisonMessage) {
		this.comparisonMessage = comparisonMessage;
	}

	public int getFridgeItemId() {
		return fridgeItemId;
	}

	public void setFridgeItemId(int fridgeItemId) {
		this.fridgeItemId = fridgeItemId;
	}
	
}