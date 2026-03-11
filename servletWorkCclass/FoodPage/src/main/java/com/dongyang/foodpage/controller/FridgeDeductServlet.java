package com.dongyang.foodpage.controller;

import com.dongyang.foodpage.dao.FridgeDAO;
import com.dongyang.foodpage.dao.IngredientDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/deductInventory") // JSP 폼의 action 경로와 일치해야 함
public class FridgeDeductServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException 
    {
		// 1. 요청이 들어온 원본 JSP URL을 가져와서 리다이렉트 경로로 설정합니다.
        String redirectPage = request.getHeader("Referer");
        // Referer 헤더가 없을 경우를 대비한 안전 장치 (실제 동작 중에는 거의 발생하지 않음)
        if (redirectPage == null || redirectPage.isEmpty()) {
            redirectPage = request.getContextPath(); // 기본 페이지로 대체
        }

        // ⚠️ 주의: memberId는 세션에서 가져와야 보안상 안전합니다.
        String memberId = (String) request.getSession().getAttribute("id");
        
        // 2. 폼 데이터 받기
        String foodName = request.getParameter("foodName");
        String itemIdStr = request.getParameter("itemId");
        String itemQtyStr = request.getParameter("parsedQuantity");
        String deductQtyStr = request.getParameter("deductQuantity");
        
        // memberId가 없거나 필수 파라미터가 누락된 경우 즉시 리다이렉트
        if (memberId == null || itemIdStr == null || deductQtyStr == null) {
            request.getSession().setAttribute("message", "잘못된 요청 정보입니다. (세션/파라미터 누락)");
            response.sendRedirect(redirectPage);
            return;
        }

        try {
            int itemId = Integer.parseInt(itemIdStr);
            // itemQtyStr: 현재 재고량 (화면 표시용, 검증용)
            // deductQtyStr: 차감할 양
            double itemQty = Double.parseDouble(itemQtyStr);
            double deductQty = Double.parseDouble(deductQtyStr);

            FridgeDAO dao = new FridgeDAO();            
            
            // 단위 변환 로직 제거 (DB 단위 통일됨)
            // 단순히 남은 양 계산: 현재 재고 - 차감량
            double remainingQty = itemQty - deductQty;
            
            if(remainingQty > 0)
            {
            	// updateQuantity는 변화량(delta)을 받으므로 음수로 전달
            	dao.updateQuantity(memberId, itemId, -deductQty);
            }
            else
            	dao.delete(memberId, itemId);

         // 3. 업데이트된 목록을 세션에 다시 저장
            request.getSession().setAttribute("isDeducted_" +  foodName + "_"+ itemId, itemId);
        } catch (NumberFormatException e) {
            request.getSession().setAttribute("deductMessage", "❌ 유효하지 않은 수량 값입니다.");
        } catch (SQLException e) {
            request.getSession().setAttribute("deductMessage", "❌ 데이터베이스 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 3. 원본 JSP 페이지로 리다이렉트 (페이지 전체 새로고침 발생)
        response.sendRedirect(redirectPage); 
    }
}