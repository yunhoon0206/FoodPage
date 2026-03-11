<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, com.dongyang.foodpage.dto.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>레시피 상세</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/common/style.css">
</head>
<body>
    <jsp:include page="../common/header.jsp" />

    <%  List<FoodDTO> courseList = (List<FoodDTO>) request.getAttribute("courseList");
        boolean isCourseMode = (courseList != null && !courseList.isEmpty());
        String outerContainerClass = isCourseMode ? "course-container" : "page-container";
        String contentBoxClass = isCourseMode ? "course-content" : "recipe-detail-container"; %>

    <div class="<%= outerContainerClass %>">
        <% if (isCourseMode) { %><jsp:include page="course_sidebar.jsp" /><% } %>

        <div class="<%= contentBoxClass %>">
            <%  FoodDTO food = (FoodDTO) request.getAttribute("food");
                List<RouteResultDTO> routeResults = (List<RouteResultDTO>) request.getAttribute("routeResults");
                List<String> allergyList = (List<String>) request.getAttribute("allergyList");
                List<ParsedIngredientDTO> fridgeList = (List<ParsedIngredientDTO>) request.getAttribute("fridgeList"); 
                
                boolean hasAllergyTrigger = false;
                String detectedAllergies = "";
                if (food != null && allergyList != null && !allergyList.isEmpty()) {
                    List<ParsedIngredientDTO> ings = food.getParsedIngredients();
                    if (ings != null) {
                        for (ParsedIngredientDTO ing : ings) {
                            for (String alg : allergyList) {
                                if (ing.getName().contains(alg)) {
                                    hasAllergyTrigger = true;
                                    if (!detectedAllergies.contains(alg)) detectedAllergies += (detectedAllergies.isEmpty() ? "" : ", ") + alg;
                                }
                            }
                        }
                    }
                }

                if (food != null) { %>
                <h1 class="recipe-title"><%= food.getName() %></h1>
                <% if (hasAllergyTrigger) { %><div class="allergy-warning-banner">&#9888; 주의: 회원님의 알레르기 유발 성분이 포함되어 있습니다! (<%= detectedAllergies %>)</div><% } %>

                <section class="route-section">
                    <% Map nearestMart = (Map) request.getAttribute("nearestMart");
                       if (nearestMart != null) { %>
                        <div class="mart-recommend-box">
                            <h3 class="mart-recommend-title">&#128722; 추천 경유지: <%= (String)nearestMart.get("place_name") %></h3>
                            <p style="margin: 5px 0; color: #555;"><%= (String)nearestMart.get("road_address_name") %> <span style="font-size: 0.9em; color: #888;">(도착지에서 <%= (String)nearestMart.get("distance") %>m)</span></p>
                            
                            <% 
                                // 경유지 구매 목록 로직 추가
                                List<ParsedIngredientDTO> martIngs = food.getParsedIngredients();
                                List<IngredientComparisonDTO> martComps = (List<IngredientComparisonDTO>) request.getAttribute("comparisonList");
                                List<String> martBuyList = new ArrayList<>();

                                if (martIngs != null && martComps != null && martIngs.size() == martComps.size()) {
                                    for (int k = 0; k < martIngs.size(); k++) {
                                        IngredientComparisonDTO mc = martComps.get(k);
                                        // 냉장고에 없거나 부족한 경우 (충분하지 않은 경우)
                                        if (mc != null && !mc.isSufficient()) {
                                            ParsedIngredientDTO mi = martIngs.get(k);
                                            String buyAmountStr = "";
                                            
                                            // 구매 필요량 계산 로직 (아래 재료 섹션 로직과 유사)
                                            if ("냉장고에 없음".equals(mc.getComparisonMessage())) {
                                                buyAmountStr = "구매 필요";
                                            } else {
                                                double buyQty = mc.getRequiredQuantity() - mc.getAvailableQuantity();
                                                if (buyQty > 0) {
                                                    String mUnit = mi.getUnit() != null ? mi.getUnit() : "";
                                                    buyAmountStr = String.format("%.1f%s", buyQty, mUnit);
                                                } else {
                                                    buyAmountStr = "구매 필요";
                                                }
                                            }
                                            martBuyList.add(mi.getName() + "(" + buyAmountStr + ")");
                                        }
                                    }
                                }
                                
                                if (!martBuyList.isEmpty()) {
                            %>
                                <div style="margin-top: 10px; padding-top: 10px; border-top: 1px dashed #bbb; font-size: 0.95em;">
                                    <strong style="color: #d32f2f;">🛒 경유지 구매 추천 목록:</strong>
                                    <span style="color: #444;"><%= String.join(", ", martBuyList) %></span>
                                </div>
                            <% } %>
                        </div>
                    <% } 
                       if (routeResults != null && !routeResults.isEmpty()) {
                           for (int i = 0; i < routeResults.size(); i++) {
                               RouteResultDTO route = routeResults.get(i);
                               String sectionTitle = "이동 경로 안내";
                               if (nearestMart != null && routeResults.size() >= 2) {
                                   if (i == 0) sectionTitle = "구간 1: 출발지 &#10145; " + (String)nearestMart.get("place_name");
                                   else sectionTitle = "구간 2: " + (String)nearestMart.get("place_name") + " &#10145; 도착지";
                               } %>
                            <h3 style="<%= i > 0 ? "margin-top: 40px;" : "" %>"><%= sectionTitle %> (소요시간: <%= route.getTotalTime() %>분)</h3>
                            <div class="route-path">
                                <div class="route-total-time">구간 소요 시간: <%= route.getTotalTime() %>분</div>
                                <ul class="route-steps">
                                    <% for (PathDTO path : route.getPaths()) {
                                            String trafficTypeIcon = "";
                                            String trafficTypeName = "";
                                            switch (path.getTrafficType()) {
                                                case 1: trafficTypeIcon = "&#128642;"; trafficTypeName = "지하철"; break;
                                                case 2: trafficTypeIcon = "&#128652;"; trafficTypeName = "버스"; break;
                                                case 3: trafficTypeIcon = "&#128694;"; trafficTypeName = "도보"; break;
                                                default: trafficTypeIcon = "&#128736;"; trafficTypeName = "기타"; break;
                                            } %>
                                            <li><span class="icon"><%= trafficTypeIcon %></span>
                                                <div class="step-info"><div class="step-time"><%= trafficTypeName %>: <%= path.getSectionTime() %>분</div>
                                                    <div class="step-name">
                                                        <% if (path.getStartName() != null && !path.getStartName().isEmpty()) out.print(path.getStartName());
                                                           if (path.getEndName() != null && !path.getEndName().isEmpty()) out.print(" &gt; " + path.getEndName());
                                                           if (path.getLaneInfo() != null && !path.getLaneInfo().isEmpty()) out.print(" (" + path.getLaneInfo() + ")"); %> 
                                                    </div></div></li>
                                    <% } %>
                                </ul>
                            </div>
                    <% } } else { %><% } %>
                </section>
                                
                <p class="recipe-meta">조리 시간: <%= food.getTime() != null ? food.getTime() : "정보 없음" %></p>
                <img src="<%= food.getImg() != null && !food.getImg().isEmpty() ? food.getImg() : "../images/default.jpg" %>" alt="<%= food.getName() %> 이미지" class="recipe-image">

                <section class="ingredients-section">
                   <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; margin-top: 30px; margin-bottom: 15px; padding-bottom: 10px;">
                        <h3 style="margin: 0; border: none; padding: 0;">재료 정보 (냉장고 비교)</h3>
                        
                        <form action="fridgeAdd.do" method="post" onsubmit="return confirm('부족한 재료를 일괄 구매(냉장고에 추가)하시겠습니까?');">
                            <input type="hidden" name="recipeName" value="<%= food.getName() %>">
                            <button type="submit" class="btn-bundle-buy">
                                🛒 일괄 구매 (부족분 채우기)
                            </button>
                        </form>
                    </div>
                    <% List<ParsedIngredientDTO> parsedIngs = food.getParsedIngredients();
                       List<IngredientComparisonDTO> comparisonList = (List<IngredientComparisonDTO>) request.getAttribute("comparisonList");
                       
                       if (parsedIngs != null && !parsedIngs.isEmpty()) {
                           String lastGroup = "___INIT___";
                           boolean ulOpened = false;

                           for (int i = 0; i < parsedIngs.size(); i++) {
                               ParsedIngredientDTO p = parsedIngs.get(i);
                               
                               // 컨트롤러에서 계산된 비교 결과 가져오기 (순서 동일 가정)
                               IngredientComparisonDTO comp = null;
                               if (comparisonList != null && comparisonList.size() > i) {
                                   comp = comparisonList.get(i);
                               }

                               String currentGroup = p.getGroup();
                               if (currentGroup == null) currentGroup = "재료";

                               if (!currentGroup.equals(lastGroup)) {
                                   if (ulOpened) { %> </ul> <% } 
                                   if (!"재료".equals(currentGroup) || i > 0 || (parsedIngs.get(parsedIngs.size()-1).getGroup() != null && !parsedIngs.get(parsedIngs.size()-1).getGroup().equals("재료"))) { %>
                                    <h4 style="margin: 20px 0 10px; color: #444; font-size: 1.1em;"><%= currentGroup %></h4>
                                <% } %>
                                <ul class="parsed-ingredient-list">
                                <% ulOpened = true; lastGroup = currentGroup; }

                                String ingName = p.getName();
                                String unit = (p.getUnit() != null) ? p.getUnit() : "";
                                String displayNeed = (p.getQuantity() > 0) ? (p.getQuantity() + unit) : p.getAmount();
                                
                                String statusHtml = "";
                                if (comp != null) {
                                    if (comp.isSufficient()) {
                                        statusHtml = "<span style='color: #1976D2; font-weight: bold;'>충분함</span>";
                                        if (comp.getAvailableQuantity() > 0) {
                                            statusHtml += String.format(" <span style='color: #4CAF50; font-size: 0.9em;'>(보유: %.1f%s)</span>", comp.getAvailableQuantity(), comp.getAvailableUnit());
                                        }
                                    } else if ("냉장고에 없음".equals(comp.getComparisonMessage())) {
                                        statusHtml = "<span style='color: #E53935; font-weight: bold;'>구매 필요</span>";
                                    } else if (comp.getComparisonMessage().contains("단위 불일치")) {
                                         statusHtml = "<span style='color: #FF9800; font-weight: bold;'>단위 불일치</span>";
                                         statusHtml += String.format(" <span style='color: #666; font-size: 0.9em;'>(보유: %.1f%s)</span>", comp.getAvailableQuantity(), comp.getAvailableUnit());
                                    } else {
                                        // 부족한 경우 (구매 필요량 계산)
                                        double buyAmount = comp.getRequiredQuantity() - comp.getAvailableQuantity();
                                        if (buyAmount > 0) {
                                            statusHtml = String.format("<span style='color: #E53935; font-weight: bold;'>구매: %.1f%s</span>", buyAmount, unit);
                                            statusHtml += String.format(" <span style='color: #4CAF50; font-size: 0.9em;'>(보유: %.1f%s)</span>", comp.getAvailableQuantity(), comp.getAvailableUnit());
                                        } else {
                                             statusHtml = "<span style='color: #E53935; font-weight: bold;'>구매 필요</span>";
                                        }
                                    }
                                } else {
                                    // 비회원이거나 비교 정보가 없는 경우 상태 메시지 표시 안 함 (필요량만 표시됨)
                                    statusHtml = "";
                                }
                                %>
                            <%
                                boolean isAllergyItem = false;
                                if (allergyList != null) {
                                    for (String alg : allergyList) {
                                        if (ingName.contains(alg)) {
                                            isAllergyItem = true;
                                            break;
                                        }
                                    }
                                }
                            %>
                            <li class="parsed-ingredient-item" style="display: flex; justify-content: space-between; align-items: center; padding: 8px 0; border-bottom: 1px solid #eee; <%= isAllergyItem ? "background-color: #ffebee;" : "" %>">
                                <div class="ing-left">
                                    <span class="ing-name" style="font-weight: bold; color: #333;">
                                        <%= ingName %>
                                        <% if (isAllergyItem) { %><span class="allergy-icon" title="알레르기 주의">&#9888;</span><% } %>
                                    </span>
                                </div>
                                <div class="ing-right" style="text-align: right; font-size: 0.9em; display: flex; flex-direction: column; align-items: flex-end;">
                                    <div style="margin-bottom: 2px;"><span style="color: #666;">필요: <%= displayNeed %></span></div>
                                    <div><%= statusHtml %></div>
                                </div>
                            </li>
                        <% } if (ulOpened) { %> </ul> <% } 
                       } else { %>
                        <div class="ingredients-list"><%= food.getIngredients() != null && !food.getIngredients().isEmpty() ? food.getIngredients().replace("\n", "<br>") : "재료 정보가 없습니다." %></div>
                    <% } %>
                </section>

               <section class="manuals-section">
                    <h3>조리 과정</h3>
                    <ul class="manuals-list">
                        <% List<String> manuals = food.getManuals();
                           if (manuals != null && !manuals.isEmpty()) {
                               for (int i = 0; i < manuals.size(); i++) { %>
                                <li><strong><%= (i + 1) %>.</strong> <%= manuals.get(i) %></li>
                        <% } } else { %><li>조리 과정 정보가 없습니다.</li><% } %>
                    </ul>
                    <div style="text-align: center; margin-top: 30px; margin-bottom: 20px;">
                        <form action="cooking.do" method="post" onsubmit="return confirm('조리를 완료하시겠습니까?\n냉장고에서 보유 중인 재료들이 자동으로 차감됩니다.');">
                            <input type="hidden" name="recipeName" value="<%= food.getName() %>">
                            <button type="submit" class="btn-finish-cooking">👨‍🍳 조리 완료 (재료 차감)</button>
                        </form>
                    </div>
                </section>
            <% } else { %><p>레시피 정보를 찾을 수 없습니다.</p><button onclick="history.back()">뒤로 가기</button><% } %>
        </div>
    </div>
    <%
        String cookingMsg = (String) session.getAttribute("cookingMsg");
        String addMsg = (String) session.getAttribute("addMsg");
        
        String alertMsg = null;
        if (cookingMsg != null) {
            alertMsg = cookingMsg;
            session.removeAttribute("cookingMsg");
        } else if (addMsg != null) {
            alertMsg = addMsg;
            session.removeAttribute("addMsg");
        }
        
        if (alertMsg != null) {
    %>
        <script>
            alert("<%= alertMsg %>");
        </script>
    <%
        }
    %>
</body>
</html>