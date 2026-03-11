package com.dongyang.foodpage.controller;

import com.dongyang.foodpage.dao.HistoryDAO;
import com.dongyang.foodpage.dto.HistoryDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/historyList.do")
public class HistoryListController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String memberId = (String) session.getAttribute("id"); // 세션 속성 이름을 "id"로 수정

        if (memberId == null) {
            // 로그인되어 있지 않다면 로그인 페이지로 리다이렉트 또는 에러 메시지
            response.sendRedirect("loginForm.do"); // 로그인 페이지로 가정
            return;
        }

        HistoryDAO historyDAO = new HistoryDAO();
        List<HistoryDTO> historyList = historyDAO.getHistoryList(memberId);

        request.setAttribute("historyList", historyList);
        request.getRequestDispatcher("/food/history.jsp").forward(request, response);
    }
}
