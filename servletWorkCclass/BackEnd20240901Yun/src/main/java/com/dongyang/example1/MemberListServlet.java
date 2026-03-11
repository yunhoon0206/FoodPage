package com.dongyang.example1;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/list.do")
public class MemberListServlet extends HttpServlet {


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		MemberDAO dao = new MemberDAO();
		ArrayList<MemberDTO> mlist =dao.memberAll();
		MemberDTO dto = new MemberDTO();
		request.setAttribute("memList", mlist);
		request.setAttribute("name", dto);
		

		
		HttpSession session =  request.getSession();
		session.setAttribute("loginCheck", "ok");
		
		RequestDispatcher dsipatcher = request.getRequestDispatcher("memberList2.jsp");
		dsipatcher.forward(request,response);
	}

}
