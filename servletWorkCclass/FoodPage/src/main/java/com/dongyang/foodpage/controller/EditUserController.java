package com.dongyang.foodpage.controller;

import java.io.IOException;
import com.dongyang.foodpage.dao.MemberDAO;
import com.dongyang.foodpage.dto.MemberDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/editUser.do")
public class EditUserController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String memberId = request.getParameter("memberId");

        MemberDAO dao = new MemberDAO();
        MemberDTO member = dao.findById(memberId);
        if (member == null) {
            response.sendRedirect("admin.do");
            return;
        }

        request.setAttribute("member", member);
        request.getRequestDispatcher("/admin/editUser.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String id = request.getParameter("id");
        String pw = request.getParameter("pw");
        String name = request.getParameter("name");
        String role = request.getParameter("role");
        String allergy = request.getParameter("allergy");
        String comp = request.getParameter("comp");
        String home = request.getParameter("home");

        MemberDAO dao = new MemberDAO();

        MemberDTO m = new MemberDTO();
        m.setId(id);
        m.setPw(pw);
        m.setName(name);
        m.setRole(role);
        m.setAllergy(allergy);
        m.setComp(comp);
        m.setHome(home);

        dao.adminUpdate(m);

        response.sendRedirect("admin.do");
    }
}
