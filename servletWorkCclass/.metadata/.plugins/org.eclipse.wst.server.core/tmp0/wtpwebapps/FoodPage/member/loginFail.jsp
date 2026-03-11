<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인 실패</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/common/style.css">
</head>
<body>
<jsp:include page="../common/header.jsp" />

<div class="auth-container">
    <div class="auth-box">
        <h2 style="color: #E07A5F;">로그인 실패</h2>
        <p>아이디 또는 비밀번호가 일치하지 않습니다.</p>
        <div class="auth-links">
            <a href="login.jsp">다시 시도</a> | <a href="join.jsp">회원가입</a>
        </div>
    </div>
</div>
</body>
</html>