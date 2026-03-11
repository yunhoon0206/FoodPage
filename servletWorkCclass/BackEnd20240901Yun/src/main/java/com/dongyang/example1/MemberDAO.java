package com.dongyang.example1;

import java.sql.*;
import java.util.ArrayList;

public class MemberDAO {
	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	final String SQL_ALL = "select * from membertbl;";
	final String SQL_CHECK = "select * from membertbl where memberid =? and password =? ;";
	public ArrayList<MemberDTO> memberAll(){
		con = JdbcConnectUtil.getConnection();
		ArrayList<MemberDTO> aList = new ArrayList<MemberDTO>();
		try {
			pstmt = con.prepareStatement(SQL_ALL);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				MemberDTO dto = new MemberDTO();
				dto.setMemberid(rs.getString("memberid"));
				dto.setPassword(rs.getString("password"));
				dto.setName(rs.getString("name"));
				dto.setEmail(rs.getString("email"));
				aList.add(dto);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
//			step4. close
			JdbcConnectUtil.close(con, pstmt, rs);
		}
	
		return aList;
	}
	public boolean loginCheck(MemberDTO mdto) {
		boolean loginResult = false;
		try {
			
			
			con = JdbcConnectUtil.getConnection();
			// 파라미터가 다른건 오버로딩
//				step3. 쿼리실행
			pstmt = con.prepareStatement(SQL_CHECK);
			//pstmt.executeUpdate(); select를 제외한 모든 쿼리실행시
			pstmt.setString(1, mdto.getMemberid());
			pstmt.setString(2, mdto.getPassword());
			rs = pstmt.executeQuery();		// select실행시
			loginResult = rs.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
//			step4. close
			JdbcConnectUtil.close(con, pstmt, rs);
		}
		return loginResult ;


	}
	
	
	
}
