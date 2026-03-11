package com.dongyang.foodpage.controller;

import com.dongyang.foodpage.service.MemberService;
import com.dongyang.foodpage.dto.MemberDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin.do")
public class AdminController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        String role = session != null ? (String) session.getAttribute("role") : null;

        if (role == null || !"ADMIN".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
            return;
        }

        MemberService service = new MemberService();
        List<MemberDTO> members = service.listAll();

        req.setAttribute("members", members);
        req.getRequestDispatcher("/admin/admin.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
    	// ★ 한글 깨짐 방지 핵심 코드
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");


        HttpSession session = req.getSession(false);
        String role = session != null ? (String) session.getAttribute("role") : null;

        if (role == null || !"ADMIN".equals(role)) {
            resp.sendRedirect(req.getContextPath() + "/member/login.jsp");
            return;
        }

        MemberService service = new MemberService();

        // 회원 삭제 처리
        String deleteId = req.getParameter("deleteId");
        if (deleteId != null && !deleteId.isEmpty()) {
            service.delete(deleteId);
        }

        // 회원 추가 처리
        String addMode = req.getParameter("addMode");
        if ("insert".equals(addMode)) {
            MemberDTO dto = new MemberDTO();
            dto.setId(req.getParameter("id"));
            dto.setPw(req.getParameter("pw"));
            dto.setName(req.getParameter("name"));
            dto.setRole(req.getParameter("role"));
            dto.setAllergy(req.getParameter("allergy"));
            dto.setComp(req.getParameter("comp"));
            dto.setHome(req.getParameter("home"));

            service.addMember(dto);
        }

        // 처리 후 관리자 페이지로 리다이렉트
        resp.sendRedirect(req.getContextPath() + "/admin.do");
    }
}