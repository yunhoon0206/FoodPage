<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<h3>메뉴 : <a href="index.jsp">홈</a>
 		  <a href = "intro.jsp">학교소개</a>
<% String lo = (String)application.getAttribute("loginCheck");
String locheck = (String)application.getAttribute("loginstate");
if(lo != null)
	{out.print("<a href = #>마이페이지</a>");
	}else{
	
		out.print("<a href = #>회원가입</a>");
	}

	%>
	 <a href = "list.do">회원목록 </a>
	</h3>
