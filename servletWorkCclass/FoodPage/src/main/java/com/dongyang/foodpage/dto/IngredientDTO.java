package com.dongyang.foodpage.dto;

public class IngredientDTO {
    private int itemId;        // ingredients_master.item_id
    private String itemName;   // ingredients_master.item_name
    private String category;   // ingredients_master.category
    private String defaultUnit;// ingredients_master.default_unit

    // Getter & Setter
    public int getItemId() {
        return itemId;
    }
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getDefaultUnit() {
        return defaultUnit;
    }
    public void setDefaultUnit(String defaultUnit) {
        this.defaultUnit = defaultUnit;
    }
}