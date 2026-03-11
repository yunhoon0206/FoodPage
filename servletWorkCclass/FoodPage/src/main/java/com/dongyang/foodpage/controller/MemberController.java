package com.dongyang.foodpage.controller;

import com.dongyang.foodpage.dto.MemberDTO;
import com.dongyang.foodpage.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login.do")
public class MemberController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String id = req.getParameter("id");
        String pw = req.getParameter("pw");

        MemberService service = new MemberService();
        MemberDTO member = service.login(id, pw);

        if (member != null) {
            HttpSession session = req.getSession(true);
            session.setAttribute("id", member.getId());
            session.setAttribute("name", member.getName()); // 수정된 부분
            session.setAttribute("role", member.getRole());
            session.setAttribute("allergy", member.getAllergy());
            session.setAttribute("comp", member.getComp());
            session.setAttribute("home", member.getHome());
            
            // 로그인 성공 시 main.do로 이동하여 초기화 로직(추천 메뉴 등) 수행
            resp.sendRedirect(req.getContextPath() + "/main.do");
        } else {
            resp.sendRedirect(req.getContextPath() + "/member/loginFail.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
    }
}