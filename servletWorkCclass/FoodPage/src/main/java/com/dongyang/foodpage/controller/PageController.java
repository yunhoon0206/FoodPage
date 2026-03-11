package com.dongyang.foodpage.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/loginForm.do", "/joinForm.do", "/myPage.do", "/logout.do"})
public class PageController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();
        String viewPage = "/index.jsp"; // 기본값

        switch (action) {
            case "/loginForm.do":
                viewPage = "/member/login.jsp";
                break;
            case "/joinForm.do":
                viewPage = "/member/join.jsp";
                break;
            case "/myPage.do":
                viewPage = "/member/mypage.jsp";
                break;
            case "/logout.do":
                viewPage = "/member/logout.jsp";
                break;
        }

        request.getRequestDispatcher(viewPage).forward(request, response);
    }
}
