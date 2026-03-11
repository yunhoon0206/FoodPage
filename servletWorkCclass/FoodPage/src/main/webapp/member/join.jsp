<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/common/style.css">
</head>
<body>
<jsp:include page="../common/header.jsp" />

<div class="auth-container">
    <div class="auth-box">
        <h2>회원가입</h2>
        <form action="${pageContext.request.contextPath}/join.do" method="post" class="auth-form">
            <input type="text" name="id" placeholder="아이디" required>
            <input type="password" name="pw" placeholder="비밀번호" required>
            <input type="text" name="name" placeholder="이름" required>
            <button type="submit">가입하기</button>
        </form>
        <div class="auth-links">
            이미 계정이 있으신가요? <a href="login.jsp">로그인</a>
        </div>
    </div>
</div>

</body>
</html>