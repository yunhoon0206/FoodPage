package com.dongyang.foodpage.dao;

import com.dongyang.foodpage.dto.HistoryDTO;
import com.dongyang.foodpage.util.DBUtil; // DB 연결 유틸리티 임포트

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HistoryDAO {

    // 레시피 조회 기록 저장 또는 업데이트
    public void insertOrUpdateHistory(HistoryDTO history) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            
            // 1. 이미 같은 레시피 기록이 있는지 확인 (memberId, recipeName 기준)
            String checkSql = "SELECT seq FROM HISTORY WHERE memberId = ? AND recipeName = ?";
            pstmt = conn.prepareStatement(checkSql);
            pstmt.setString(1, history.getMemberId());
            pstmt.setString(2, history.getRecipeName());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // 2. 기록이 있다면 viewDate만 업데이트 (최근 조회 시간 반영)
                int seqToUpdate = rs.getInt("seq");
                String updateSql = "UPDATE HISTORY SET viewDate = CURRENT_TIMESTAMP WHERE seq = ?";
                pstmt.close(); // 이전 pstmt 닫기
                pstmt = conn.prepareStatement(updateSql);
                pstmt.setInt(1, seqToUpdate);
                pstmt.executeUpdate();
            } else {
                // 3. 기록이 없다면 새로 삽입
                String insertSql = "INSERT INTO HISTORY (memberId, recipeName, imgUrl, viewDate) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
                pstmt.close(); // 이전 pstmt 닫기 (rs.next()가 false일 경우 rs가 닫히지 않았을 수 있음)
                pstmt = conn.prepareStatement(insertSql);
                pstmt.setString(1, history.getMemberId());
                pstmt.setString(2, history.getRecipeName());
                pstmt.setString(3, history.getImgUrl());
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("HistoryDAO insertOrUpdateHistory error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 특정 사용자의 최근 본 레시피 기록 목록 조회
    public List<HistoryDTO> getHistoryList(String memberId) {
        List<HistoryDTO> historyList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();
            String sql = "SELECT seq, memberId, recipeName, imgUrl, viewDate FROM HISTORY WHERE memberId = ? ORDER BY viewDate DESC LIMIT 10"; // 최근 10개
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                HistoryDTO history = new HistoryDTO();
                history.setSeq(rs.getInt("seq"));
                history.setMemberId(rs.getString("memberId"));
                history.setRecipeName(rs.getString("recipeName"));
                history.setImgUrl(rs.getString("imgUrl"));
                history.setViewDate(rs.getTimestamp("viewDate").toLocalDateTime()); // Timestamp를 LocalDateTime으로 변환
                historyList.add(history);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("HistoryDAO getHistoryList error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return historyList;
    }
}
