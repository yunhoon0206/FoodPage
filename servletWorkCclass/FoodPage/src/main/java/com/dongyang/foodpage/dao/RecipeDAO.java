package com.dongyang.foodpage.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.dongyang.foodpage.util.DBUtil;

public class RecipeDAO {
    
    /**
     * DB에서 요리 이름으로 재료 정보를 가져옵니다.
     * @param menuName 요리 이름
     * @return 재료 원본 텍스트 (ingredient_raw), 없으면 null
     * @throws SQLException
     */
    public String getIngredientsByMenuName(String menuName) throws SQLException {
        String query = "SELECT ingredient_raw FROM recipe_data WHERE menu_name = ?";
        String ingredientRaw = null;
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, menuName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ingredientRaw = rs.getString("ingredient_raw");
                }
            }
        }
        return ingredientRaw;
    }
}
