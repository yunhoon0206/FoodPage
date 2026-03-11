<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Header</title>
<style>
    body { margin: 0; font-family: Arial, sans-serif; }
    .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 15px 30px;
        background-color: #f8f8f8;
        border-bottom: 1px solid #eee;
    }
    .header-left { 
        flex: 1; 
        text-align: left; 
        display: flex; 
        align-items: center; 
    }
    .header-center { flex: 2.6; text-align: center; }
    .header-right { flex: 1; text-align: right; }
    .header-logo { font-size: 24px; font-weight: bold; text-decoration: none; color: #333; }
    .header-nav a {
        margin: 0 15px;
        text-decoration: none;
        color: #555;
        font-weight: bold;
    }
    .header-auth a {
        margin-left: 15px;
        text-decoration: none;
        color: #555;
    }
    /* 숨겨진 에러 테스트 버튼 스타일 */
    .hidden-error-trigger {
        display: inline-block;
        width: 20px;
        height: 20px;
        opacity: 0; /* 투명하게 설정 */
        cursor: default; /* 커서도 기본값으로 유지하여 숨김 */
        margin-left: 10px;
    }
</style>
</head>
<body>
    <div class="header">
        <div class="header-left">
            <a href="${pageContext.request.contextPath}/main.do" class="header-logo">MyFoodApp</a>
            <%-- 로고 옆에 숨겨진 에러 테스트 링크 (클릭 가능하지만 보이지 않음) --%>
            <a href="${pageContext.request.contextPath}/errorTest.do" class="hidden-error-trigger" title="Error Test"></a>
        </div>
        <div class="header-center header-nav">
            <a href="${pageContext.request.contextPath}/main.do">검색</a>
           <a href="${pageContext.request.contextPath}/fridge">냉장고</a>
            <a href="${pageContext.request.contextPath}/historyList.do">기록</a>
        </div>
        <div class="header-right header-auth">
            <%
                String sessionName = (String) session.getAttribute("name");
                String sessionId = (String) session.getAttribute("id");
                if (sessionId == null) {
            %>
                <a href="${pageContext.request.contextPath}/loginForm.do">로그인</a>
                <a href="${pageContext.request.contextPath}/joinForm.do">회원가입</a>
            <% } else { %>
                <span style="color:#555; font-size:14px; margin-right:10px;">
                    <b><%= sessionName != null ? sessionName : sessionId %></b>님 환영합니다
                </span>
                <a href="${pageContext.request.contextPath}/logout.do">로그아웃</a>
                <a href="${pageContext.request.contextPath}/myPage.do">마이페이지</a>
            <% } %>
        </div>
    </div>
</body>
</html>