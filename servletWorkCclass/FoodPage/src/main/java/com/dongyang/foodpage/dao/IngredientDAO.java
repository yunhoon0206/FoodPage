package com.dongyang.foodpage.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.dongyang.foodpage.dto.IngredientDTO;
import com.dongyang.foodpage.util.DBUtil;

public class IngredientDAO {
    private Connection conn;

    public IngredientDAO() throws SQLException {
        this.conn = DBUtil.getConnection();
    }

    // 전체 재료 목록 조회
    public List<IngredientDTO> getAll() throws SQLException {
        // ✅ 수정: ingredients_master -> ingredient_master (s 삭제)
        String sql = "SELECT item_id, item_name, category, default_unit FROM ingredient_master ORDER BY item_name";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        List<IngredientDTO> list = new ArrayList<>();
        while(rs.next()) {
            IngredientDTO ing = new IngredientDTO();
            ing.setItemId(rs.getInt("item_id"));
            ing.setItemName(rs.getString("item_name"));
            ing.setCategory(rs.getString("category"));
            ing.setDefaultUnit(rs.getString("default_unit"));
            list.add(ing);
        }
        rs.close();
        ps.close();
        return list;
    }

    // 특정 재료 조회 (itemId 기준)
    public IngredientDTO getById(int itemId) throws SQLException {
        // ✅ 수정: ingredients_master -> ingredient_master (s 삭제)
        String sql = "SELECT item_id, item_name, category, default_unit FROM ingredient_master WHERE item_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, itemId);
        ResultSet rs = ps.executeQuery();

        IngredientDTO ing = null;
        if(rs.next()) {
            ing = new IngredientDTO();
            ing.setItemId(rs.getInt("item_id"));
            ing.setItemName(rs.getString("item_name"));
            ing.setCategory(rs.getString("category"));
            ing.setDefaultUnit(rs.getString("default_unit"));
        }
        rs.close();
        ps.close();
        return ing;
    }
    public int findOrInsert(String name, String category, String unit) throws SQLException {
        int itemId = -1;
        
        // 단위가 없으면 기본값 "개"로 설정 (DB 저장 시와 동일하게 맞춤)
        String searchUnit = (unit != null && !unit.isBlank()) ? unit.trim() : "개";
        
        // 1. 이미 존재하는지 확인 (이름과 단위가 모두 일치해야 함)
        String searchSql = "SELECT item_id FROM ingredient_master WHERE item_name = ? AND default_unit = ?";
        try (PreparedStatement ps = conn.prepareStatement(searchSql)) {
            ps.setString(1, name);
            ps.setString(2, searchUnit);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("item_id"); // 존재하면 ID 반환
                }
            }
        }

        // 2. 없으면 새로 등록
        String insertSql = "INSERT INTO ingredient_master (item_name, category, default_unit) VALUES (?, ?, ?)";
        // 키 생성 옵션 추가 (Statement.RETURN_GENERATED_KEYS)
        try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, category != null ? category : "기타"); // 카테고리 없으면 '기타'
            ps.setString(3, searchUnit);       // 검색에 사용한 단위 저장
            
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        itemId = rs.getInt(1); // 방금 생성된 ID 반환
                    }
                }
            }
        }
        
        return itemId;
    }
}