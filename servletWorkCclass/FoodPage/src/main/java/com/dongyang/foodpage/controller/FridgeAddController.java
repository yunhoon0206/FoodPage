package com.dongyang.foodpage.controller;

import com.dongyang.foodpage.dao.FridgeDAO;
import com.dongyang.foodpage.dao.IngredientDAO;
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

@WebServlet("/fridgeAdd.do")
public class FridgeAddController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        
        HttpSession session = req.getSession();
        String memberId = (String) session.getAttribute("id");
        String recipeName = req.getParameter("recipeName");

        if (memberId == null) {
            resp.sendRedirect(req.getContextPath() + "/loginForm.do");
            return;
        }

        try {
            // 1. 레시피 및 재료 정보 파싱
            // API 호출은 레시피 존재 여부 확인용으로만 사용 (선택 사항)
            // String jsonResult = FoodApiHelper.searchRecipeDetail(recipeName);
            // FoodDTO food = RecipeUtil.parseJsonToFoodDTO(jsonResult);
            
            com.dongyang.foodpage.dao.RecipeDAO recipeDAO = new com.dongyang.foodpage.dao.RecipeDAO();
            String dbIngredients = recipeDAO.getIngredientsByMenuName(recipeName);
            
            int addedCount = 0;

            if (dbIngredients != null && !dbIngredients.isBlank()) {
                List<ParsedIngredientDTO> recipeIngredients = RecipeUtil.parseIngredients(dbIngredients, recipeName);
                
                FridgeDAO fridgeDAO = new FridgeDAO();
                IngredientDAO ingredientDAO = new IngredientDAO();
                
                // 내 냉장고 목록 조회 (중복 체크용)
                List<FridgeItemDTO> myFridge = fridgeDAO.getFridgeList(memberId);

                for (ParsedIngredientDTO recipeItem : recipeIngredients) {
                    try {
                        double need = recipeItem.getQuantity();
                        String unit = recipeItem.getUnit();
                        
                        // [수정] 수량이 없으면 1로 설정하여 DB에 저장 (그래야 나중에 '보유 중'으로 인식됨)
                        if (need <= 0) {
                            need = 1.0;
                            if (unit == null || unit.isEmpty()) {
                                unit = "적당량"; // 단위가 없으면 임의 설정
                            }
                        }
    
                        String name = recipeItem.getName();
                        // String unit = recipeItem.getUnit(); // 위에서 처리함
                        
                        String category = "식재료"; 
                        if(recipeItem.getGroup() != null && !recipeItem.getGroup().isEmpty()) {
                            category = recipeItem.getGroup();
                        }
    
                        int targetItemId = -1;
                        
                        // [변경] 1. 내 냉장고에 호환되는 단위(g/ml)의 같은 재료가 있는지 확인
                        for (FridgeItemDTO myItem : myFridge) {
                            // 이름이 같고 (공백/대소문자 유연하게 처리 추천하나 일단 기존 유지)
                            if (myItem.getItemName() != null && myItem.getItemName().trim().equalsIgnoreCase(name.trim())) {
                                String myUnit = myItem.getDefaultUnit();
                                boolean isCompatible = false;
                                
                                // 단위 비교 (null 안전 처리)
                                String u1 = (myUnit != null) ? myUnit.trim() : "";
                                String u2 = (unit != null) ? unit.trim() : "";
                                
                                if (u1.equalsIgnoreCase(u2)) {
                                    isCompatible = true;
                                } else if (("g".equalsIgnoreCase(u1) || "ml".equalsIgnoreCase(u1)) && 
                                           ("g".equalsIgnoreCase(u2) || "ml".equalsIgnoreCase(u2))) {
                                    isCompatible = true; // g와 ml 호환
                                }
                                
                                if (isCompatible) {
                                    targetItemId = myItem.getItemId(); // 기존 아이템 ID 사용
                                    break;
                                }
                            }
                        }
    
                        // 2. 호환되는 기존 재료가 없으면, 레시피 단위 그대로 DB에서 찾거나 생성
                        if (targetItemId == -1) {
                            targetItemId = ingredientDAO.findOrInsert(name, category, unit);
                        }
    
                        if (targetItemId != -1) {
                            // 내 냉장고 보유량 확인 (위에서 찾은 targetItemId로 다시 확인)
                            double have = 0;
                            for (FridgeItemDTO myItem : myFridge) {
                                if (myItem.getItemId() == targetItemId) {
                                    have = myItem.getQuantity();
                                    break;
                                }
                            }
    
                            // 부족한 만큼 추가
                            if (have < need) {
                                double buyAmount = need - have;
                                // DB에 추가
                                fridgeDAO.addOrUpdate(memberId, targetItemId, buyAmount, null, "레시피 일괄 구매 (" + recipeName + ")");
                                addedCount++;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("재료 추가 실패 (" + recipeItem.getName() + "): " + e.getMessage());
                        // 실패해도 계속 진행
                    }
                }
                
                if (addedCount > 0) {
                    session.setAttribute("addMsg", addedCount + "종류의 재료를 냉장고에 추가했습니다.");
                } else {
                    session.setAttribute("addMsg", "추가할 재료가 없거나 이미 충분합니다.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            session.setAttribute("addMsg", "DB 오류로 추가에 실패했습니다: " + e.getMessage());
        }

        resp.sendRedirect("recipeDetail.do?name=" + URLEncoder.encode(recipeName, "UTF-8"));
    }
}