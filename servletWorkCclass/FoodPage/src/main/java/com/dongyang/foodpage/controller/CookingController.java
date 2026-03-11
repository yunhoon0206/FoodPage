package com.dongyang.foodpage.controller;

import com.dongyang.foodpage.dao.FridgeDAO;
import com.dongyang.foodpage.dto.FoodDTO;
import com.dongyang.foodpage.dto.FridgeItemDTO;
import com.dongyang.foodpage.dto.ParsedIngredientDTO;
import com.dongyang.foodpage.util.FoodApiHelper;
import com.dongyang.foodpage.util.RecipeUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/cooking.do")
public class CookingController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        
        HttpSession session = req.getSession();
        String memberId = (String) session.getAttribute("id");
        String recipeName = req.getParameter("recipeName");

        // 비로그인 처리
        if (memberId == null) {
            resp.sendRedirect(req.getContextPath() + "/loginForm.do");
            return;
        }

        // 레시피 이름이 안 넘어왔을 때
        if (recipeName == null || recipeName.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        try {
            // 1. 레시피 존재 여부 확인 (API 호출은 최소화하거나 생략 가능하나, 우선 유지)
            // String jsonResult = FoodApiHelper.searchRecipeDetail(recipeName); 
            // FoodDTO food = RecipeUtil.parseJsonToFoodDTO(jsonResult); 
            
            // 핵심: 재료 정보는 이제 DB에서만 가져옵니다.
            com.dongyang.foodpage.dao.RecipeDAO recipeDAO = new com.dongyang.foodpage.dao.RecipeDAO();
            String dbIngredients = recipeDAO.getIngredientsByMenuName(recipeName);
            
            int successCount = 0;

            if (dbIngredients != null && !dbIngredients.isBlank()) {
                // 재료 파싱 (텍스트 -> 객체 리스트)
                List<ParsedIngredientDTO> recipeIngredients = RecipeUtil.parseIngredients(dbIngredients, recipeName);
                
                // 2. 내 냉장고 목록 조회
                FridgeDAO fridgeDAO = new FridgeDAO();
                List<FridgeItemDTO> myFridge = fridgeDAO.getFridgeList(memberId);

                // 3. [비교 및 차감 로직]
                for (ParsedIngredientDTO recipeItem : recipeIngredients) {
                    double requiredQty = recipeItem.getQuantity(); // 레시피 필요량
                    String requiredUnit = recipeItem.getUnit();    // 레시피 단위
                    
                    if (requiredQty <= 0) continue; // 수량 정보 없는 건 패스

                    // 내 냉장고에서 같은 이름의 재료 찾기
                    for (FridgeItemDTO myItem : myFridge) {
                        if (myItem.getItemName().equals(recipeItem.getName())) {   // 이름 매칭 (정확 일치 또는 포함)
                            boolean unitMatch = false;                            // 단위 호환성 체크 (단순 문자열 비교)
                            String myUnit = myItem.getDefaultUnit();
                            double deductionAmount = 0.0; // 실제 차감할 수량 (내 냉장고 단위 기준)
                            // 1. 정확 일치, 포함 관계, 또는 g/ml 호환 확인
                            boolean isCompatible = false;
                            
                            if (myUnit != null && requiredUnit != null) {
                                if (myUnit.equals(requiredUnit) || recipeItem.getAmount().contains(myUnit)) {
                                    isCompatible = true;
                                } else if (("g".equalsIgnoreCase(myUnit) || "ml".equalsIgnoreCase(myUnit)) && 
                                           ("g".equalsIgnoreCase(requiredUnit) || "ml".equalsIgnoreCase(requiredUnit))) {
                                    isCompatible = true;
                                }
                            }
                            
                            if (isCompatible) {
                                unitMatch = true;
                                deductionAmount = requiredQty;
                            }
                            // 단위 변환 로직 제거됨 (DB 단위 통일)
                            
                            if (unitMatch) {
                                double currentQty = myItem.getQuantity();
                                if (currentQty <= deductionAmount + 0.001) {
                                    fridgeDAO.delete(memberId, myItem.getItemId());
                                } else {
                                    fridgeDAO.updateQuantity(memberId, myItem.getItemId(), -deductionAmount);
                                }
                                
                                successCount++;
                                break; // 해당 재료는 처리했으니 다음 레시피 재료로 넘어감
                            }
                        }
                    }
                }
                
                // 결과 메시지 설정
                if (successCount > 0) {
                    session.setAttribute("cookingMsg", successCount + "개의 재료가 냉장고에서 차감되었습니다.");
                } else {
                    session.setAttribute("cookingMsg", "차감할 수 있는 일치하는 재료가 없거나, 단위가 맞지 않습니다.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("cookingMsg", "DB 처리 중 오류가 발생했습니다: " + e.getMessage());
        }

        // 4. 원래 상세 페이지로 리다이렉트
        resp.sendRedirect("recipeDetail.do?name=" + URLEncoder.encode(recipeName, "UTF-8"));
    }
}