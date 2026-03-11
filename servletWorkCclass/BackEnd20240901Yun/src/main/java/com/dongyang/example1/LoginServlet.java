package com.dongyang.example1;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;


@WebServlet("/login.do")
public class LoginServlet extends HttpServlet {
 
	public void init(ServletConfig config) throws ServletException {
		//System.out.println("init메서드호출");
	
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//System.out.println("doget메서드호출");
		// 1. 파라미터를 받음
		
		
		String id = request.getParameter("id"); 
		String pw = request.getParameter("pw");
		System.out.println("아이디:"+id+", 암호"+pw);
		
		MemberDTO mdto = new MemberDTO();
		mdto.setMemberid(id); // mdto 변수에있는 Memberid 는 id를 가지고있음
		mdto.setPassword(pw); // mdto 변수에있는 Password 는 pw 를 가지고 있음
		MemberDAO mdao=  new MemberDAO();
		boolean result =mdao.loginCheck(mdto);
		// 2. 값이 데이터베이스에 존재하는지 확인 (jdbc)
		if(result) {
			/*
			 * HttpSession session=request.getSession(); session.setAttribute("name", "윤훈");
			 * session.setAttribute("mail", 3); response.sendRedirect("loginOk.jsp");
			 */
			
		ServletContext application = request.getServletContext();
		application.setAttribute("loginCheck", "ok");
		}
		else {
			/*
			 * RequestDispatcher dispatcher= request.getRequestDispatcher("loginFail.jsp");
			 * dispatcher.forward(request, response);
			 */
		}
		response.sendRedirect("index.jsp");
		// 3. 응답문서 응답
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
