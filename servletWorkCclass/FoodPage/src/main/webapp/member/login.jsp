<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/common/style.css">
</head>
<body>
<jsp:include page="../common/header.jsp" />

<div class="auth-container">
    <div class="auth-box">
        <h2>로그인</h2>
        <% if ("1".equals(request.getParameter("joined"))) { %>
            <div style="color: green; margin-bottom: 15px; font-weight: bold;">
                회원가입이 완료되었습니다.<br>로그인 해주세요.
            </div>
        <% } %>
        <form action="${pageContext.request.contextPath}/login.do" method="post" class="auth-form">
            <input type="text" name="id" placeholder="아이디" required>
            <input type="password" name="pw" placeholder="비밀번호" required>
            <button type="submit">로그인</button>
        </form>
        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/joinForm.do">회원가입</a> | <a href="#">아이디/비밀번호 찾기</a>
        </div>
    </div>
</div>

</body>
</html>