package com.dongyang.foodpage.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dongyang.foodpage.dao.MemberDAO;
import com.dongyang.foodpage.dto.FoodDTO;
import com.dongyang.foodpage.dto.MemberDTO;
import com.dongyang.foodpage.util.FoodApiHelper;
import com.dongyang.foodpage.util.RecipeUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/main.do")
public class MainController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        // 1. 회원 정보 조회 (주소 정보 등)
        HttpSession session = request.getSession();
        String memberId = (String) session.getAttribute("id");
        String memberComp = "";
        String memberHome = "";
        
        if (memberId != null) {
            try {
                MemberDAO memberDAO = new MemberDAO();
                MemberDTO member = memberDAO.findById(memberId);
                if (member != null) {
                    if (member.getComp() != null) memberComp = member.getComp();
                    if (member.getHome() != null) memberHome = member.getHome();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        // JSP로 전달 (기존 스크립트릿 변수와 호환되도록)
        request.setAttribute("memberComp", memberComp);
        request.setAttribute("memberHome", memberHome);

        // 2. 오늘의 추천 메뉴 로직 (랜덤 API 호출)
        // 검색 모드가 아니고(mode가 null이거나 1,2,3이 아님), 검색 결과도 없을 때 실행
        String mode = request.getParameter("mode");
        if (mode == null) mode = "1";
        request.setAttribute("mode", mode);

        // 검색 결과가 없는 초기 상태일 때만 추천 메뉴 로직 실행
        // (search.do를 거치지 않고 바로 main.do로 왔을 때)
        if (request.getAttribute("searchResult") == null && request.getAttribute("hanSangResult") == null) {
             List<FoodDTO> recommendList = null;
             boolean apiSuccess = false;
             try {
                 // API(RCP_PAT2)가 지원하는 카테고리 (반찬, 국, 후식, 일품, 밥)
                 String[] recommendKeywords = {"반찬", "후식", "일품"};
                 String randomKeyword = recommendKeywords[new java.util.Random().nextInt(recommendKeywords.length)];
                 String jsonResult = FoodApiHelper.searchRecipes(randomKeyword, "category", 1, 20);
                 recommendList = RecipeUtil.parseJsonToFoodList(jsonResult);
                 
                 if (recommendList != null && !recommendList.isEmpty()) {
                     java.util.Collections.shuffle(recommendList);
                     if (recommendList.size() > 3) recommendList = recommendList.subList(0, 3);
                     apiSuccess = true;
                 }
             } catch (Exception e) {
                 e.printStackTrace();
             }
             
             // 추천 메뉴가 있으면 searchResult 속성에 담아서 JSP가 '오늘의 추천 메뉴'로 인식하게 함
             // 단, searchFailed 플래그는 false여야 함
             if (apiSuccess) {
                 request.setAttribute("searchResult", recommendList);
                 request.setAttribute("searchKeyword", null); // 키워드가 없으면 '오늘의 추천 메뉴' 타이틀 출력
             } else {
                 // API 실패 시
                 request.setAttribute("searchFailed", true); 
             }
        }

        // 3. 뷰 포워딩
        request.getRequestDispatcher("/index.jsp").forward(request, response);
    }
}