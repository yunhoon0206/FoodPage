<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>냉장고 관리</title>
    <link rel="stylesheet" type="text/css" href="../common/style.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/common/style.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>
<jsp:include page="/common/header.jsp" />

<div class="page-title">
    <h2>냉장고 관리</h2>
</div>

<div class="fridge-container">

<!-- 식재료 목록 -->
<div class="fridge-section ingredients-section">
    <h3>식재료 목록</h3>
    <ul>
        <c:set var="foodCount" value="0"/>
        <c:forEach var="item" items="${fridgeList}">
            <c:if test="${item.category eq '식재료'}">
                <c:set var="foodCount" value="${foodCount + 1}"/>
                <li title="${item.note}">
                    <span class="qty-text">${item.itemName} - ${item.quantity}${item.defaultUnit}</span>
                    <form action="${pageContext.request.contextPath}/fridge" method="post" style="display:inline;">
                        <input type="hidden" name="memberId" value="${memberId}">
                        <input type="hidden" name="itemId" value="${item.itemId}">
                        <input type="hidden" name="action" value="delete">
                        <button type="submit" class="delete-btn"><i class="fas fa-trash-alt"></i></button>
                    </form>
                </li>
            </c:if>
        </c:forEach>
        <c:if test="${foodCount eq 0}">
            <li>보유 중인 식재료가 없습니다.</li>
        </c:if>
    </ul>
</div>

<!-- 조미료 목록 -->
<div class="fridge-section seasoning-section">
    <h3>조미료 목록</h3>
    <ul>
        <c:set var="seasoningCount" value="0"/>
        <c:forEach var="item" items="${fridgeList}">
            <c:if test="${item.category eq '조미료'}">
                <c:set var="seasoningCount" value="${seasoningCount + 1}"/>
                <li title="${item.note}">
                    <span class="qty-text">${item.itemName} - ${item.quantity}${item.defaultUnit}</span>
                    <form action="${pageContext.request.contextPath}/fridge" method="post" style="display:inline;">
                        <input type="hidden" name="memberId" value="${memberId}">
                        <input type="hidden" name="itemId" value="${item.itemId}">
                        <input type="hidden" name="action" value="delete">
                        <button type="submit" class="delete-btn"><i class="fas fa-trash-alt"></i></button>
                    </form>
                </li>
            </c:if>
        </c:forEach>
        <c:if test="${seasoningCount eq 0}">
            <li>보유 중인 조미료가 없습니다.</li>
        </c:if>
    </ul>
</div>

<!-- 재료 추가 -->
<div class="fridge-section add-section">
    <h3>재료 추가</h3>
    <form action="${pageContext.request.contextPath}/fridge" method="post" class="add-ingredient-form">
        <input type="hidden" name="memberId" value="${memberId}">
        <input type="hidden" name="action" value="add">

        <label>재료 선택:</label>
        <select name="itemId" required>
            <option value="" disabled selected>-- 재료 선택 --</option>
            <optgroup label="식재료">
                <c:forEach var="item" items="${ingredientList}">
                    <c:if test="${item.category eq '식재료'}">
                        <option value="${item.itemId}">${item.itemName} (${item.defaultUnit})</option>
                    </c:if>
                </c:forEach>
            </optgroup>
            <optgroup label="조미료">
                <c:forEach var="item" items="${ingredientList}">
                    <c:if test="${item.category eq '조미료'}">
                        <option value="${item.itemId}">${item.itemName} (${item.defaultUnit})</option>
                    </c:if>
                </c:forEach>
            </optgroup>
        </select>

        <label>수량:</label>
        <!-- 자유 소수 입력 허용 -->
        <input type="number" step="any" name="quantity" min="0.01" value="1" required>

        <label>메모:</label>
        <input type="text" name="note" placeholder="예: 필요 용도 / 메모">

        <button type="submit">재료 추가</button>
    </form>
</div>

<!-- 재료 소모 -->
<div class="fridge-section reduce-section">
    <h3>재료 소모</h3>
    <form action="${pageContext.request.contextPath}/fridge" method="post" class="reduce-ingredient-form">
        <input type="hidden" name="memberId" value="${memberId}">
        <input type="hidden" name="action" value="decrease">

        <label>재료 선택:</label>
        <select name="itemId" required>
            <option value="" disabled selected>-- 재료 선택 --</option>
            <c:forEach var="item" items="${fridgeList}">
                <option value="${item.itemId}">${item.itemName} (${item.quantity}${item.defaultUnit})</option>
            </c:forEach>
        </select>

        <label>소모 수량:</label>
        <input type="number" step="any" name="quantity" min="0.01" value="1" required>

        <button type="submit">재료 감소</button>
    </form>
</div>

</div>
</body>
</html>
