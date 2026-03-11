package com.dongyang.foodpage.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.dongyang.foodpage.dto.FridgeItemDTO;
import com.dongyang.foodpage.util.DBUtil;

public class FridgeDAO {

    // 냉장고 목록 조회
    public List<FridgeItemDTO> getFridgeList(String memberId) throws SQLException {
        List<FridgeItemDTO> list = new ArrayList<>();

        String sql = "SELECT f.id, f.item_id, f.quantity, f.expiry_date, f.note, " +
                     "im.item_name, im.default_unit, im.category " +
                     "FROM fridge f JOIN ingredient_master im ON f.item_id = im.item_id " +
                     "WHERE f.id = ? ORDER BY im.category, im.item_name";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, memberId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                FridgeItemDTO item = new FridgeItemDTO();
                item.setMemberId(rs.getString("id"));
                item.setItemId(rs.getInt("item_id"));
                item.setQuantity(rs.getDouble("quantity"));
                item.setExpiryDate(rs.getDate("expiry_date"));
                item.setNote(rs.getString("note"));
                item.setItemName(rs.getString("item_name"));
                item.setDefaultUnit(rs.getString("default_unit"));
                item.setCategory(rs.getString("category"));
                list.add(item);
            }
        }
        return list;
    }


    // 재료 등록/수정(추가)
    public void addOrUpdate(String memberId, int itemId, double quantity, Date expiryDate, String note) throws SQLException {

        String checkSql = "SELECT quantity FROM fridge WHERE id = ? AND item_id = ?";
        String insertSql = "INSERT INTO fridge (id, item_id, quantity, expiry_date, note) VALUES (?,?,?,?,?)";
        String updateSql = "UPDATE fridge SET quantity = quantity + ?, expiry_date = ?, note = ? WHERE id = ? AND item_id = ?";

        try (Connection conn = DBUtil.getConnection()) {

            PreparedStatement check = conn.prepareStatement(checkSql);
            check.setString(1, memberId);
            check.setInt(2, itemId);
            ResultSet rs = check.executeQuery();

            if (rs.next()) {
                PreparedStatement update = conn.prepareStatement(updateSql);
                update.setDouble(1, quantity);
                update.setDate(2, expiryDate);
                update.setString(3, note);
                update.setString(4, memberId);
                update.setInt(5, itemId);
                update.executeUpdate();
            } else {
                PreparedStatement insert = conn.prepareStatement(insertSql);
                insert.setString(1, memberId);
                insert.setInt(2, itemId);
                insert.setDouble(3, quantity);
                insert.setDate(4, expiryDate);
                insert.setString(5, note);
                insert.executeUpdate();
            }
        }
    }


    // 감소용: 특정 재료 조회
    public FridgeItemDTO getByMemberAndItem(String memberId, int itemId) throws SQLException {

        String sql = "SELECT f.id, f.item_id, f.quantity, f.expiry_date, f.note, " +
                     "im.item_name, im.default_unit, im.category " +
                     "FROM fridge f JOIN ingredient_master im ON f.item_id = im.item_id " +
                     "WHERE f.id = ? AND f.item_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, memberId);
            ps.setInt(2, itemId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                FridgeItemDTO item = new FridgeItemDTO();
                item.setMemberId(rs.getString("id"));
                item.setItemId(rs.getInt("item_id"));
                item.setQuantity(rs.getDouble("quantity"));
                item.setExpiryDate(rs.getDate("expiry_date"));
                item.setNote(rs.getString("note"));
                item.setItemName(rs.getString("item_name"));
                item.setDefaultUnit(rs.getString("default_unit"));
                item.setCategory(rs.getString("category"));
                return item;
            }
        }
        return null;
    }


    // 감소용: 수량 감소(음수값을 전달 → 감소)
    public void updateQuantity(String memberId, int itemId, double changeQty) throws SQLException {
        String sql = "UPDATE fridge SET quantity = quantity + ? WHERE id = ? AND item_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, changeQty);
            ps.setString(2, memberId);
            ps.setInt(3, itemId);

            ps.executeUpdate();
        }
    }


    // 0 이하 → 재료 삭제
    public void delete(String memberId, int itemId) throws SQLException {
        String sql = "DELETE FROM fridge WHERE id = ? AND item_id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, memberId);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        }
    }
}
