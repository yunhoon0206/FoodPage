<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, com.dongyang.foodpage.dto.FoodDTO, java.net.URLEncoder" %>
<%
    List<FoodDTO> courseList = (List<FoodDTO>) request.getAttribute("courseList");
    FoodDTO activeFood = (FoodDTO) request.getAttribute("food");
    String setId = (String) request.getAttribute("setId");
    
    String allMenus = "";
    if (courseList != null) {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<courseList.size(); i++) {
             sb.append(courseList.get(i).getName());
             if (i < courseList.size() - 1) sb.append(",");
        }
        allMenus = sb.toString();
    }
%>
<div class="course-sidebar">
    <h3>코스 메뉴 (조리시간순)</h3>
    <% if (courseList != null) { 
        for (FoodDTO courseFood : courseList) {
            boolean isActive = activeFood != null && courseFood.getName().equals(activeFood.getName()); %>
    <a href="courseDetail.do?setId=<%= setId %>&menus=<%= URLEncoder.encode(allMenus, "UTF-8") %>&active=<%= URLEncoder.encode(courseFood.getName(), "UTF-8") %>" 
       class="course-menu-item <%= isActive ? "active" : "" %>">
        <%= courseFood.getName() %>
        <span class="time-badge"><%= courseFood.getTime() %></span>
    </a>
    <%  } 
       } %>
    <div style="margin-top: 20px; text-align: center;">
        <button onclick="location.href='index.jsp?mode=2'" style="padding: 10px 20px; cursor: pointer; background: #555; color: white; border: none; border-radius: 5px;">다른 한상 찾기</button>
    </div>
</div>