<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.dongyang.foodpage.dto.HistoryDTO, java.time.format.DateTimeFormatter, java.net.URLEncoder" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>최근 본 레시피</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/common/style.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    <div class="page-container">
        <div class="history-container">
            <h2>최근 본 레시피</h2>
            <ul class="history-list">
                <% List<HistoryDTO> historyList = (List<HistoryDTO>) request.getAttribute("historyList");
                   if (historyList != null && !historyList.isEmpty()) {
                       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
                       for (HistoryDTO history : historyList) { %>
                        <li class="history-item" onclick="location.href='${pageContext.request.contextPath}/recipeDetail.do?name=<%= URLEncoder.encode(history.getRecipeName(), "UTF-8") %>'">
                            <img src="<%= history.getImgUrl() != null && !history.getImgUrl().isEmpty() ? history.getImgUrl() : "../images/default.jpg" %>" alt="<%= history.getRecipeName() %>">
                            <div class="history-content">
                                <h3><%= history.getRecipeName() %></h3>
                                <p>최근 조회: <span class="history-date"><%= history.getViewDate().format(formatter) %></span></p>
                            </div>
                        </li>
                <%     }
                   } else { %><p>최근 본 레시피가 없습니다.</p><% } %>
            </ul>
        </div>
    </div>
</body>
</html>