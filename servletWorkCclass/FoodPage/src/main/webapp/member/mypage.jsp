<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String id = (String) session.getAttribute("id");
  String name = (String) session.getAttribute("name");
  String role = (String) session.getAttribute("role");
  String allergy =(String) session.getAttribute("allergy");
  String comp =(String) session.getAttribute("comp");
  String home =(String) session.getAttribute("home");
  if(allergy == null){
	  allergy = "";
  }
  if(comp == null){
	  comp = "";
  }
  if(home == null){
	  home = "";
  }
  if (id == null) {
    response.sendRedirect(request.getContextPath() + "/member/login.jsp");
    return;
  }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>마이페이지</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/common/style.css">
<script src="${pageContext.request.contextPath}/address/addrPopup.js"></script>
</head>
<body>
<jsp:include page="../common/header.jsp" />

<div class="auth-container">
    <div class="auth-box">
        <h2>마이페이지</h2>
        
        <div class="section-title">내 정보</div>
        <div class="info-list">
            <p>아이디 <span><%= id %></span></p>
            <p>이름 <span><%= name %></span></p>
            <p>권한 <span><%= role %></span></p>
            <p>알레르기 <span><%= allergy %></span></p>
            <p>회사주소 <span><%= comp %></span></p>
            <p>집주소 <span><%= home %></span></p>
            
        </div>

        <div class="divider"></div>

        <div class="section-title">정보 수정</div>
        <% if ("1".equals(request.getParameter("updated"))) { %>
            <div style="color:green; margin-bottom: 10px;">정보가 수정되었습니다.</div>
        <% } else if ("0".equals(request.getParameter("updated"))) { %>
            <div style="color:red; margin-bottom: 10px;">수정에 실패했습니다.</div>
        <% } %>

        <form action="${pageContext.request.contextPath}/update.do" method="post" class="auth-form">
            <input type="text" name="name" placeholder="새 이름 (변경 시 입력)">
            <input type="password" name="pw" placeholder="새 비밀번호 (변경 시 입력)">
            <input type="text" name="allergy" placeholder="알레르기정보를 , 를 이용해서 구분하여 입력해주세요">
            <div class="input-wrapper">
                <input type="text" id="comp_addr" name="comp" placeholder="회사주소 변경" readonly>
                <button type="button" onclick="openAddrPopup(document.getElementById('comp_addr'))">검색</button>
            </div>
            <div class="input-wrapper">
                <input type="text" id="home_addr" name="home" placeholder="집주소 변경" readonly>
                <button type="button" onclick="openAddrPopup(document.getElementById('home_addr'))">검색</button>
            </div>
            <button type="submit">수정하기</button>
        </form>

        <% if ("ADMIN".equals(role)) { %>
            <div class="divider"></div>
            <div class="auth-links">
                <a href="${pageContext.request.contextPath}/admin.do" style="color: #E07A5F; font-weight: bold;">[관리자] 회원 관리 페이지로 이동</a>
            </div>
        <% } %>
        
        <div class="auth-links">
             <a href="logout.jsp" style="color: #999;">로그아웃</a>
        </div>
    </div>
</div>

</body>
</html>