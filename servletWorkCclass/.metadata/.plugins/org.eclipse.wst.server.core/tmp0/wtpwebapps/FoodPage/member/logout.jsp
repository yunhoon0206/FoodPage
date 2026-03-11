<%@ page contentType="text/html;charset=UTF-8" %>
<%
    session.invalidate(); // 세션 끊기
    response.sendRedirect(request.getContextPath() + "/index.jsp"); // 로그인 화면으로 이동
%>