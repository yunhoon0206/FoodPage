package com.dongyang.foodpage.filter;

import java.io.IOException;
import com.dongyang.foodpage.dao.MemberDAO;
import com.dongyang.foodpage.dto.MemberDTO;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*")
public class SessionUpdateFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 초기화 로직 필요 시 작성
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // HTTP 요청인 경우에만 처리
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpSession session = httpRequest.getSession(false); // 기존 세션이 있으면 가져옴

            // 로그인 상태인지 확인 (id가 세션에 있는지)
            if (session != null && session.getAttribute("id") != null) {
                String id = (String) session.getAttribute("id");
                
                try {
                    // DB에서 최신 회원 정보 조회
                    MemberDAO dao = new MemberDAO();
                    MemberDTO member = dao.findById(id);

                    if (member != null) {
                        // DB의 최신 정보를 세션에 덮어씌움 (이름, 권한, 알레르기, 주소 등)
                        session.setAttribute("name", member.getName());
                        session.setAttribute("role", member.getRole());
                        session.setAttribute("allergy", member.getAllergy());
                        session.setAttribute("comp", member.getComp());
                        session.setAttribute("home", member.getHome());
                        
                        // 로그 출력 (디버깅용, 필요 시 주석 처리)
                        // System.out.println("[SessionUpdateFilter] User info synchronized for: " + id);
                    }
                } catch (Exception e) {
                    System.out.println("[SessionUpdateFilter] Failed to update session info: " + e.getMessage());
                }
            }
        }

        // 다음 필터 또는 서블릿/JSP 실행
        chain.doFilter(request, response);
    }
}
