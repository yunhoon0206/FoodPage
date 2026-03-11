<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="com.dongyang.foodpage.dto.MemberDTO" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원정보수정</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/common/style.css">
<style>
    body {
        margin: 0;
        padding: 0;
        background-color: #f4f4f4;
        font-family: 'Segoe UI', sans-serif;
    }

    .edit-wrapper {
        width: 550px;
        margin: 60px auto;
    }

    .edit-box {
        background: #fff;
        padding: 30px;
        border-radius: 12px;
        box-shadow: 0 4px 14px rgba(0,0,0,0.08);
    }

    .title {
        font-size: 22px;
        font-weight: bold;
        margin-bottom: 20px;
        text-align: center;
    }

    label {
        font-size: 14px;
        font-weight: 600;
        display: block;
        margin-top: 10px;
        margin-bottom: 6px;
    }

    input, select {
        width: 100%;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 6px;
        margin-bottom: 12px;
        font-size: 14px;
        box-sizing: border-box;
    }

    .btn-save {
        margin-top: 10px;
        padding: 12px;
        background-color: #2980b9;
        color: white;
        border: none;
        border-radius: 6px;
        width: 100%;
        font-size: 15px;
        cursor: pointer;
    }
    .btn-save:hover {
        background-color: #1f6696;
    }

    .btn-back {
        width: 100%;
        margin-top: 12px;
        padding: 11px;
        border-radius: 6px;
        background-color: #555;
        border: none;
        color: #fff;
        cursor: pointer;
    }
    .btn-back:hover {
        background-color: #333;
    }
</style>
</head>
<body>
<jsp:include page="../common/header.jsp" />
<div class="edit-wrapper">
    <div class="edit-box">

        <div class="title">회원 정보 수정</div>

        <form action="editUser.do" method="post">

            <!-- 수정할 회원의 아이디는 변경 불가 -->
            <label>아이디</label>
            <input type="text" name="id" value="${member.id}" readonly>

            <label>비밀번호</label>
            <input type="password" name="pw" value="${member.pw}">


            <label>이름</label>
            <input type="text" name="name" value="${member.name}">

            <label>권한</label>
            <select name="role">
                <option value="USER"  ${member.role == 'USER' ? 'selected' : ''}>USER</option>
                <option value="ADMIN" ${member.role == 'ADMIN' ? 'selected' : ''}>ADMIN</option>
            </select>

            <label>알레르기</label>
            <input type="text" name="allergy" value="${member.allergy}">

            <label>회사</label>
            <input type="text" name="comp" value="${member.comp}">

            <label>주소</label>
            <input type="text" name="home" value="${member.home}">

            <button class="btn-save">저장하기</button>
        </form>

        <button class="btn-back" onclick="location.href='admin.do'">뒤로가기</button>

    </div>
</div>

</body>
</html>
