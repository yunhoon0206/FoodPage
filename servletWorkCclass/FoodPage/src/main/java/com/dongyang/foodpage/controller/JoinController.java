package com.dongyang.foodpage.controller;

import com.dongyang.foodpage.dto.MemberDTO;
import com.dongyang.foodpage.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/join.do")
public class JoinController extends HttpServlet {
    
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
		
        req.setCharacterEncoding("UTF-8"); // 한글 인코딩 처리

        String id = req.getParameter("id");
        String pw = req.getParameter("pw");
        String name = req.getParameter("name");
        String allergy = req.getParameter("allergy");
        String comp = req.getParameter("comp");
        String home = req.getParameter("home");

        MemberService service = new MemberService();
        // DTO 순서 수정: id, pw, name, role("USER"), allergy, comp, home
        boolean ok = service.register(new MemberDTO(id, pw, name, "USER", allergy, comp, home));

        if (ok) {
            resp.sendRedirect(req.getContextPath() + "/member/login.jsp?joined=1");
        } else {
            resp.sendRedirect(req.getContextPath() + "/member/joinFail.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/member/join.jsp");
    }
}