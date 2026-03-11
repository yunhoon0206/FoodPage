<%@page import="com.dongyang.example1.MemberDTO"%>
<%@page import="java.util.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page errorPage = "errorMessage.jsp" %>
<%@ taglib prefix = "c" uri="http://java.sun.com/jsp/jstl/core" %>
   
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<title>회원목록</title>
</head>
<body>
<%
	int num =  Integer.parseInt("aaa");
%>
<%@ include file = "header.jsp" %>

<h1>이름 :저는${name}입니다 , 로그인여부 : ${loginCheck}  </h1>
<h1> 회원목록 </h1>
<table border =1>
	<tr>
		<td>아이디</td><td>암호</td><td>이름</td><td>이메일</td>
	</tr>
	<c:forEach items = "${memList}" var="dtoItem"> 
		<tr>
		<td>${dtoItem.memberid}</td>
		<td>${dtoItem.password}</td>
		<td>${dtoItem.name}</td>
		<td>${dtoItem.email}</td>
		</tr>
	</c:forEach>

</body>
</html>