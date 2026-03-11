package com.dongyang.foodpage.dao;

import com.dongyang.foodpage.dto.MemberDTO;
import com.dongyang.foodpage.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    // 회원 가입
    public boolean insert(MemberDTO member) {
        String sql = "INSERT INTO member (id, pw, name, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getPw());
            pstmt.setString(3, member.getName());
            pstmt.setString(4, member.getRole());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ID로 회원 조회
    public MemberDTO findById(String id) {
        String sql = "SELECT * FROM member WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new MemberDTO(
                        rs.getString("id"),
                        rs.getString("pw"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("allergy"),
                        rs.getString("comp"),
                        rs.getString("home")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 일부만 수정 + 빈칸이면 기존값 유지 
    public boolean updateProfile(String id, String newName, String newPw, String newAllergy, String newComp, String newHome) {

        // 기존 정보 불러오기
        MemberDTO old = findById(id);
        if (old == null) return false;

        // 새 값이 비어있으면 기존값 유지
        String finalName = (newName == null || newName.isBlank()) ? old.getName() : newName;
        String finalPw   = (newPw == null || newPw.isBlank()) ? old.getPw() : newPw;
        String finalAll  = (newAllergy == null || newAllergy.isBlank()) ? old.getAllergy() : newAllergy;
        String finalComp = (newComp == null || newComp.isBlank()) ? old.getComp() : newComp;
        String finalHome = (newHome == null || newHome.isBlank()) ? old.getHome() : newHome;

        // 통합 UPDATE
        String sql = "UPDATE member SET name=?, pw=?, allergy=?, comp=?, home=? WHERE id=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, finalName);
            pstmt.setString(2, finalPw);
            pstmt.setString(3, finalAll);
            pstmt.setString(4, finalComp);
            pstmt.setString(5, finalHome);
            pstmt.setString(6, id);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 전체 목록 조회
    public List<MemberDTO> findAll() {
        String sql = "SELECT * FROM member ORDER BY role DESC, id ASC";
        List<MemberDTO> list = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(new MemberDTO(
                        rs.getString("id"),
                        rs.getString("pw"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("allergy"),
                        rs.getString("comp"),
                        rs.getString("home")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ID로 삭제
    public boolean deleteById(String id) {
        String sql = "DELETE FROM member WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 관리자 기능 - 전체 필드 insert
    public boolean addMember(MemberDTO member) {
        String sql = "INSERT INTO member (id, pw, name, role, allergy, comp, home) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, member.getId());
            pstmt.setString(2, member.getPw());
            pstmt.setString(3, member.getName());
            pstmt.setString(4, member.getRole());
            pstmt.setString(5, member.getAllergy());
            pstmt.setString(6, member.getComp());
            pstmt.setString(7, member.getHome());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
 // 관리자 전용 - 모든 필드 수정
    public boolean adminUpdate(MemberDTO m) {
        String sql = "UPDATE member SET pw=?, name=?, role=?, allergy=?, comp=?, home=? WHERE id=?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, m.getPw());
            pstmt.setString(2, m.getName());
            pstmt.setString(3, m.getRole());
            pstmt.setString(4, m.getAllergy());
            pstmt.setString(5, m.getComp());
            pstmt.setString(6, m.getHome());
            pstmt.setString(7, m.getId());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    

}
