<%@ page contentType="text/html;charset=UTF-8" %>
<%
    String id = (String) session.getAttribute("id");
    if (id == null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>
<html>
<head><title>로그인 성공</title></head>
<body>
    <h2>환영합니다, <%= id %>님!</h2>

    <!-- 로그아웃 버튼 -->
    <form action="logout.jsp" method="post">
        <input type="submit" value="로그아웃">
    </form>

    <!-- 홈으로 버튼 (컨텍스트 패스 반영) -->
    <p>
        <a href="<%= request.getContextPath() %>/index.jsp">홈으로</a>
    </p>
</body>
</html>