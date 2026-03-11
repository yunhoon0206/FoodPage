package com.dongyang.foodpage.controller;

import com.dongyang.foodpage.dto.MemberDTO;
import com.dongyang.foodpage.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/update.do")
public class UpdateController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("id") == null) {
            resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
            return;
        }

        String id = (String) session.getAttribute("id");
        String newName = req.getParameter("name");
        String newPw = req.getParameter("pw");
        String newAllergy = req.getParameter("allergy");
        String newComp = req.getParameter("comp");
        String newHome = req.getParameter("home");

        MemberService service = new MemberService();
        boolean ok = service.updateProfile(id, newName, newPw, newAllergy, newComp, newHome);

        // 수정 직후 세션 최신값으로 다시 넣기
        if (ok) {
            MemberDTO updated = service.getMember(id);
            if (updated != null) {
                session.setAttribute("name", updated.getName());
                session.setAttribute("allergy", updated.getAllergy());
                session.setAttribute("comp", updated.getComp());
                session.setAttribute("home", updated.getHome());
            }
        }

        resp.sendRedirect(req.getContextPath() + "/member/mypage.jsp?updated=" + (ok ? "1" : "0"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/member/mypage.jsp");
    }
}
