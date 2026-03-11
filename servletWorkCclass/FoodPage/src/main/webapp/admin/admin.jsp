<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="com.dongyang.foodpage.dto.MemberDTO" %>
<%
  String role = (String) session.getAttribute("role");
  if (role == null || !"ADMIN".equals(role)) {
    response.sendRedirect(request.getContextPath() + "/member/login.jsp");
    return;
  }
  List<MemberDTO> members = (List<MemberDTO>) request.getAttribute("members");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원관리</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/common/style.css">
</head>
<body>
<jsp:include page="../common/header.jsp" />

<div class="admin-container">
  <div class="admin-header">
    <h2>회원 관리 (ADMIN)</h2>
    <a href="${pageContext.request.contextPath}/member/mypage.jsp" style="text-decoration: none; color: #555; font-weight: bold;">← 마이페이지로</a>
  </div>

  <!-- 회원 목록 테이블 -->
  <table class="data-table">
    <thead>
      <tr>
        <th>아이디</th>
        <th>이름</th>
        <th>권한</th>
        <th>관리</th>
        <th>수정</th>
      </tr>
    </thead>
    <tbody>
      <% if (members != null && !members.isEmpty()) {
           for (MemberDTO m : members) { %>
      <tr>
        <td><%= m.getId() %></td>
        <td><%= m.getName() %></td>
        <td><%= m.getRole() %></td>
        <td>
          <% if (!"admin".equals(m.getId()) && !"ADMIN".equals(m.getRole())) { %>
          <form action="${pageContext.request.contextPath}/admin.do" method="post" style="display:inline;" onsubmit="return confirm('정말 삭제하시겠습니까?');">
            <input type="hidden" name="deleteId" value="<%= m.getId() %>">
            <button type="submit" class ="btn-del">삭제</button>
      
          </form>
          <% } else { %>
            <span style="color: #999; font-size: 13px;">보호됨</span>
          <% } %>
        </td>
        <td>
          <% if (!"admin".equals(m.getId()) && !"ADMIN".equals(m.getRole())) { %>
         <form action="${pageContext.request.contextPath}/editUser.do" method="get" style="display:inline;">
            <input type="hidden" name="memberId" value="<%= m.getId() %>">
            <button type="submit" class="btn-edit">수정</button>
        </form>
    <% } else { %>
        <span style="color: #999; font-size: 13px;">보호됨</span>
    <% } %>
        </td>
      </tr>
      <% } } else { %>
      <tr>
        <td colspan="4" style="text-align: center; padding: 30px;">등록된 회원이 없습니다.</td>
      </tr>
      <% } %>
    </tbody>
  </table>

  <!-- 신규 회원 추가 박스 -->
  <div class="admin-header">
    <h3>신규 회원 추가</h3>
  </div>

  <form action="${pageContext.request.contextPath}/admin.do" method="post" class="add-user-box">
    <input type="hidden" name="addMode" value="insert">
    <label>아이디: <input type="text" name="id" required></label>
    <label>비밀번호: <input type="password" name="pw" required></label>
    <label>이름: <input type="text" name="name" required></label>
    <label>권한:
      <select name="role">
        <option value="USER">USER</option>
        <option value="ADMIN">ADMIN</option>
      </select>
    </label>
    <label>알러지: <input type="text" name="allergy"></label>
    <label>회사: <input type="text" name="comp"></label>
    <label>집주소: <input type="text" name="home"></label>
    <button type="submit">회원 추가</button>
  </form>
</div>
</body>
</html>