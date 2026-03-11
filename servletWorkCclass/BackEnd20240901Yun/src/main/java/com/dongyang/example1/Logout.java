package com.dongyang.example1;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.http.HttpRequest;

import org.apache.catalina.core.ApplicationContextFacade;

import com.sun.net.httpserver.Request;


@WebServlet("/logif.do")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public Logout() {
        super();

    }

	public void init(ServletConfig config) throws ServletException {
		System.out.println("안녕하세요");
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String id = request.getParameter("id");
		String pw = request.getParameter("pw");
		String user = request.getParameter("user");
		if(id.equals("dong") && pw.equals("1122")) {
		HttpSession ses = request.getSession();
		ses.setAttribute("name", user);
		ses.setAttribute("mail", 3);
		response.sendRedirect("loginOk.jsp");
		ServletContext svl = request.getServletContext();
		svl.setAttribute("loginCheck", "ok");
		}
		else {
			RequestDispatcher rdp = request.getRequestDispatcher("loginFail.jsp");
			rdp.forward(request, response);
			response.sendRedirect("index.jsp");
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
