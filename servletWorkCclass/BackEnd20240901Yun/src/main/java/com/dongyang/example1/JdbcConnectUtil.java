package com.dongyang.example1;

import java.sql.*;

public class JdbcConnectUtil {
	
	public static Connection getConnection() {
		Connection con = null;
		//1단계,2단계
		//step1. 커넥터 로딩 
//		step2.  DB 서버 접속 (ip:포트번호/db명,user,password)
		 try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/servletdb","root" , "password");
		} catch (SQLException |ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return con;
	}
	public static void close(Connection con, PreparedStatement pstmt) {
		//4단계 
		try {
			con.close();
			pstmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// 메소드 오버로딩 
	public static void close(Connection con, PreparedStatement pstmt, ResultSet rs) {
		//4단계
		try {
			con.close();
			pstmt.close();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
