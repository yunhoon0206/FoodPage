<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>가입 실패</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/common/style.css">
</head>
<body>
<jsp:include page="../common/header.jsp" />

<div class="auth-container">
    <div class="auth-box">
        <h2 style="color: #E07A5F;">가입 실패</h2>
        <p>회원가입에 실패했습니다.<br>이미 사용 중인 아이디일 수 있습니다.</p>
        <div class="auth-links">
            <a href="join.jsp">다시 시도</a> | <a href="${pageContext.request.contextPath}/index.jsp">홈으로</a>
        </div>
    </div>
</div>
</body>
</html>