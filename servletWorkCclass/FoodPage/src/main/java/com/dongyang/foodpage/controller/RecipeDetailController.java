package com.dongyang.foodpage.controller;

import com.dongyang.foodpage.dto.FoodDTO;
import com.dongyang.foodpage.dao.FridgeDAO;
import com.dongyang.foodpage.dto.FridgeItemDTO;
import com.dongyang.foodpage.dao.HistoryDAO; // HistoryDAO 임포트
import com.dongyang.foodpage.dao.IngredientDAO;
import com.dongyang.foodpage.dto.HistoryDTO; // HistoryDTO 임포트
import com.dongyang.foodpage.dto.IngredientComparisonDTO;
import com.dongyang.foodpage.dto.ParsedIngredientDTO;
import com.dongyang.foodpage.dto.RouteResultDTO; // 추가
import com.dongyang.foodpage.service.OdsayRouteService; // 추가
import com.dongyang.foodpage.util.FoodApiHelper;
import com.dongyang.foodpage.util.RecipeUtil; // RecipeUtil 임포트
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; // HttpSession 임포트
import java.io.IOException;
import java.sql.SQLException;
import java.util.List; // 추가


import java.util.Map; // 추가
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList; // ArrayList 추가

@WebServlet("/recipeDetail.do")
public class RecipeDetailController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {//요리를 클릭해 다음 페이지로 넘어가면 여기로 와서 값을 뿌려줌
        request.setCharacterEncoding("UTF-8"); // 한글 인코딩 처리
        String recipeName = request.getParameter("name"); // 요리 이름
        String startAddress = request.getParameter("start"); // 출발지 파라미터 추가
        String endAddress = request.getParameter("end");     // 도착지 파라미터 추가
        
        System.out.println("[RecipeDetailController] Parameters - Name: " + recipeName + ", Start: " + startAddress + ", End: " + endAddress);

        if (recipeName == null || recipeName.isEmpty()) {
            response.sendRedirect("index.jsp"); // 보여줄게없으면 인덱스로
            return;
        }
        
        FoodDTO food = null;
        
        // 1. 세션 캐시에서 먼저 찾아보기 (API 호출 최소화 및 데이터 불일치 방지)
        HttpSession session = request.getSession();
        List<FoodDTO> cachedList = (List<FoodDTO>) session.getAttribute("cachedRecipeList");
        
        if (cachedList != null) {
            for (FoodDTO cachedFood : cachedList) {
                // 이름이 일치하면 캐시된 데이터 사용 (공백 제거 후 비교 등으로 유연성 확보 가능하나, 우선 정확 일치 시도)
                if (cachedFood.getName().equals(recipeName)) {
                    food = cachedFood;
                    System.out.println("[RecipeDetailController] Found recipe in Session Cache: " + recipeName);
                    break;
                }
            }
        }

        // 2. 캐시에 없으면 API 호출로 상세 정보 가져오기
        if (food == null) {
            System.out.println("[RecipeDetailController] Recipe not in cache. Calling API for: " + recipeName);
            String jsonResult = FoodApiHelper.searchRecipeDetail(recipeName); // 여기서 이름을 검색하는 기능을 사용
            food = RecipeUtil.parseJsonToFoodDTO(jsonResult); // 아래 함수를 호출
        }
        
        // 3. 재료 정보 DB에서 조회 및 갱신 (캐시 사용 여부와 무관하게 항상 실행)
        if (food != null) {
            try {
                com.dongyang.foodpage.dao.RecipeDAO recipeDAO = new com.dongyang.foodpage.dao.RecipeDAO();
                String dbIngredients = recipeDAO.getIngredientsByMenuName(food.getName());
                
                if (dbIngredients != null && !dbIngredients.isBlank()) {
                    System.out.println("[RecipeDetailController] DB에서 재료 정보 발견: " + food.getName());
                    // DB 데이터로 교체
                    food.setIngredients(dbIngredients);
                    // 파싱 정보도 새로고침
                    food.setParsedIngredients(RecipeUtil.parseIngredients(dbIngredients, food.getName()));
                } else {
                    System.out.println("[RecipeDetailController] DB에 재료 정보 없음: " + food.getName());
                    // API 데이터는 RecipeUtil에서 이미 제거되었으므로, DB에도 없으면 재료 정보는 비어있게 됨
                }
            } catch (SQLException e) {
                System.out.println("[RecipeDetailController] DB 재료 조회 실패");
                e.printStackTrace();
            }
        }
        
        if (food == null) {
            System.out.println("[RecipeDetailController] FAILED to find recipe for name: [" + recipeName + "]");
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println("<script>alert('검색된 레시피가 없거나 제한된 레시피입니다.'); location.href='index.jsp';</script>");
            return;
        }

        // 이동 경로 정보 가져오기 및 전달 (마트 경유 로직 적용 - 좌표 기반)
        List<RouteResultDTO> finalRouteResults = new ArrayList<>();
        
        if (startAddress != null && !startAddress.isEmpty() && endAddress != null && !endAddress.isEmpty()) {
            
            // 주소를 좌표로 미리 변환
            double[] startCoords = OdsayRouteService.getGeoCoords(startAddress);
            double[] endCoords = OdsayRouteService.getGeoCoords(endAddress);
            
            if (startCoords[0] != -1 && endCoords[0] != -1) {
                
                // 1. 도착지 주변 가장 가까운 마트 검색
                System.out.println("[RecipeDetailController] Searching nearest mart near: " + endAddress);
                List<Map<String, String>> marts = OdsayRouteService.searchNearbyMarts(endAddress);
                
                boolean martRouteFound = false;

                if (marts != null && !marts.isEmpty()) {
                    Map<String, String> nearestMart = marts.get(0);
                    String martName = nearestMart.get("place_name");
                    String martAddress = nearestMart.get("road_address_name"); // 로그용
                    
                    try {
                        double martLng = Double.parseDouble(nearestMart.get("x"));
                        double martLat = Double.parseDouble(nearestMart.get("y"));
                        
                        System.out.println("[RecipeDetailController] Nearest Mart found: " + martName + " (Lat: " + martLat + ", Lng: " + martLng + ")");
                        request.setAttribute("nearestMart", nearestMart); // 마트 정보 전달

                        // 2. 구간 1: 출발지 -> 마트 (좌표 사용)
                        System.out.println("[RecipeDetailController] Route Segment 1: Start -> Mart");
                        List<RouteResultDTO> route1 = OdsayRouteService.getRouteTime(startCoords[0], startCoords[1], martLat, martLng);
                        
                        // 3. 구간 2: 마트 -> 도착지 (좌표 사용)
                        System.out.println("[RecipeDetailController] Route Segment 2: Mart -> End");
                        List<RouteResultDTO> route2 = OdsayRouteService.getRouteTime(martLat, martLng, endCoords[0], endCoords[1]);
                        
                        if (route1 != null && !route1.isEmpty() && route2 != null && !route2.isEmpty()) {
                            finalRouteResults.add(route1.get(0));
                            finalRouteResults.add(route2.get(0));
                            martRouteFound = true;
                        } else {
                             System.out.println("[RecipeDetailController] Failed to find complete mart route segments.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("[RecipeDetailController] Error parsing mart coordinates: " + e.getMessage());
                    }
                }

                if (!martRouteFound) {
                    // 마트 없음 or 마트 경로 실패: 기존 직통 경로 (좌표 사용)
                    System.out.println("[RecipeDetailController] Using direct route (Start -> End).");
                    List<RouteResultDTO> directRoute = OdsayRouteService.getRouteTime(startCoords[0], startCoords[1], endCoords[0], endCoords[1]);
                    if (directRoute != null && !directRoute.isEmpty()) {
                        finalRouteResults.add(directRoute.get(0));
                    }
                }
                
                // 결과 저장
                if (!finalRouteResults.isEmpty()) {
                    request.setAttribute("routeResults", finalRouteResults);
                    System.out.println("[RecipeDetailController] Total route segments added: " + finalRouteResults.size());
                }
            } else {
                 System.out.println("[RecipeDetailController] Failed to geocode start or end address.");
            }

        } else {
            System.out.println("[RecipeDetailController] Skip route search. Missing start/end address.");
        }

        // 로그인 사용자일 경우 레시피 조회 기록 저장
        HttpSession session1 = request.getSession();
        String memberId = (String) session1.getAttribute("id"); // 세션에서 로그인 아이디를 가져옴
        
        System.out.println("[RecipeDetailController] memberId: " + memberId); // 디버깅 로그

        if (memberId != null && food != null && food.getName() != null) {//로그인을했고 정보도 정상이라면
            System.out.println("[RecipeDetailController] Saving history for: " + food.getName()); // 디버깅 로그
            HistoryDTO history = new HistoryDTO(); // 기록에 추가 하기위해 만들기시작
            history.setMemberId(memberId); // DTO의 정보를 설정함
            history.setRecipeName(food.getName());
            history.setImgUrl(food.getImg()); // 이미지 URL도 저장
            
            HistoryDAO historyDAO = new HistoryDAO(); // 저장한 정보를 보내주기위해 DAO생성
            historyDAO.insertOrUpdateHistory(history); // 생성한 historyDAO에 db에 저장하는 함수를 부르고 history값을 보냄
            
            
            
            List<ParsedIngredientDTO> fridgeList = null;

            try {
                FridgeDAO fridgeDAO = new FridgeDAO();
                // DB에서 냉장고 아이템 가져오기
                List<FridgeItemDTO> itemList = fridgeDAO.getFridgeList(memberId);
                
                fridgeList = new ArrayList<ParsedIngredientDTO>();
                
                // 가져온 아이템을 ParsedIngredientDTO로 변환해서 리스트에 담기
                for(FridgeItemDTO item : itemList) {
                    fridgeList.add(new ParsedIngredientDTO(item));
                }
                
                // --- 알레르기 정보 가져오기 ---
                com.dongyang.foodpage.dao.MemberDAO memberDAO = new com.dongyang.foodpage.dao.MemberDAO();
                com.dongyang.foodpage.dto.MemberDTO member = memberDAO.findById(memberId);
                if (member != null && member.getAllergy() != null && !member.getAllergy().isBlank()) {
                    String[] allergies = member.getAllergy().split(",");
                    List<String> allergyList = new ArrayList<>();
                    for (String s : allergies) {
                        allergyList.add(s.trim());
                    }
                    request.setAttribute("allergyList", allergyList);
                    System.out.println("[RecipeDetailController] User allergies: " + allergyList);
                }
                
            } catch (SQLException e) {
                System.out.println("[RecipeDetailController] Failed to Read from Fridge Items");
                e.printStackTrace(); // 에러 확인을 위해 추가하면 좋습니다
            }

            // JSP로 냉장고 리스트 전달 (디버깅용 또는 기타 용도)
            request.setAttribute("fridgeList", fridgeList);
            
            // [변경] 컨트롤러에서 비교 로직 수행 후 결과 전달
            List<IngredientComparisonDTO> comparisonList = null;
			try {
				comparisonList = compareToFridgeList(memberId, food.getParsedIngredients());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            request.setAttribute("comparisonList", comparisonList);
            
        } else {
            System.out.println("[RecipeDetailController] Skip history saving. conditions met? " + (memberId != null) + ", " + (food != null));
        }
        if (food != null) {
            if (food.getParsedIngredients() == null || food.getParsedIngredients().isEmpty()) {
                // 파싱된 리스트가 없는데 원본 텍스트는 있는 경우
                if (food.getIngredients() != null && !food.getIngredients().isEmpty()) {
                    System.out.println("[RecipeDetailController] 파싱 정보 재설정: " + food.getName());
                    food.setParsedIngredients(RecipeUtil.parseIngredients(food.getIngredients(), food.getName()));
                }
            }
        }

        request.setAttribute("food", food);// 보내는건 food라는이름을 붙여서 보내기위해설정 
        request.getRequestDispatcher("/food/recipe_detail.jsp").forward(request, response);// 위에 주소창에 보일주소와 열 jsp파일 을 설정하고 포워딩함
    }
    private List<IngredientComparisonDTO> compareToFridgeList(String memberId, List<ParsedIngredientDTO> recipeIngredients)
        	throws SQLException
        {
        	List<IngredientComparisonDTO> result = new ArrayList<IngredientComparisonDTO>();
        	
        	if (recipeIngredients == null) return result;
        	
        	FridgeDAO fridgeDAO = new FridgeDAO();    	
        	List<FridgeItemDTO> fridgeItems = fridgeDAO.getFridgeList(memberId);
    		
    		// 레시피 재료별로 순회하며 비교 수행
            for (ParsedIngredientDTO recipeItem : recipeIngredients)
            {
            	String name = recipeItem.getName();
                double reqQty = recipeItem.getQuantity();
                String reqUnit = recipeItem.getUnit();
                
                int itemId = -1;
                double availableQty = 0.0;
                String availableUnit = "";
                boolean isSufficient = false;
                String message = "";
                
                if (name == null) continue;
                
                // 1. 냉장고에서 매칭되는 아이템 찾기 (우선순위 적용)
                FridgeItemDTO fridge = null;
                FridgeItemDTO exactMatch = null;
                FridgeItemDTO partialMatch = null;
                
                for(var f : fridgeItems) {
                    if (f.getItemName() != null) {
                        String fName = f.getItemName().trim();
                        String rName = name.trim();
                        
                        // 1순위: 정확 일치
                        if (fName.equalsIgnoreCase(rName)) {
                            exactMatch = f;
                            break; // 정확히 일치하는 것을 찾으면 즉시 중단
                        }
                        
                        // 2순위: 레시피가 냉장고 아이템을 포함 (예: 레시피 '돼지고기(안심)' -> 냉장고 '돼지고기')
                        // 주의: 반대 경우(냉장고가 레시피를 포함)는 '방울 토마토'가 '토마토'에 매칭되는 문제를 일으키므로 제외
                        if (partialMatch == null && rName.contains(fName)) {
                            partialMatch = f;
                        }
                    }
                }
                
                // 최적의 매칭 선택
                if (exactMatch != null) {
                    fridge = exactMatch;
                } else {
                    fridge = partialMatch;
                }
                
                if(fridge != null) {
                    itemId = fridge.getItemId();
                    availableQty = fridge.getQuantity();
                    availableUnit = fridge.getDefaultUnit();
                    
                    // [변경] 수량이 없는 재료(적당량)인데 냉장고에 있으면 무조건 충분
                    if (reqQty <= 0) {
                        isSufficient = true;
                        message = "충분함 (보유 중)";
                    } else {
                        // 단위 비교 (null 안전 처리)
                        boolean unitMatch = false;
                        String fUnit = (availableUnit != null) ? availableUnit.trim() : "";
                        String rUnit = (reqUnit != null) ? reqUnit.trim() : "";
                        
                        if (fUnit.equalsIgnoreCase(rUnit)) {
                            unitMatch = true;
                        } else if (fUnit.isEmpty() && rUnit.isEmpty()) {
                            unitMatch = true;
                        } else if (("g".equalsIgnoreCase(fUnit) || "ml".equalsIgnoreCase(fUnit)) && 
                                   ("g".equalsIgnoreCase(rUnit) || "ml".equalsIgnoreCase(rUnit))) {
                            // g와 ml는 호환되는 것으로 간주
                            unitMatch = true;
                        }
                        
                        if (unitMatch) {
                            if (availableQty >= reqQty) {
                                isSufficient = true;
                                message = String.format("충분함 (재고: %.1f%s)", availableQty, availableUnit);
                            } else {
                                isSufficient = false;
                                double needed = reqQty - availableQty;
                                message = String.format("부족 (%.1f%s 필요)", needed, availableUnit);
                            }
                        } else {
                            // 단위 불일치 -> 비교 불가 -> 구매 필요로 간주하되 메시지 표시
                            isSufficient = false;
                            message = String.format("단위 불일치 (재고: %s, 요구: %s)", availableUnit, reqUnit);
                        }
                    }
                } else {
                    // 냉장고에 없음
                    itemId = -1;
                    availableQty = 0.0;
                    availableUnit = ""; // 없음
                    isSufficient = false;
                    message = "냉장고에 없음";
                }
                
                // IngredientComparisonDTO 생성 및 리스트 추가
                result.add(new IngredientComparisonDTO(
                		name, reqQty, reqUnit, availableQty, availableUnit, isSufficient, message, itemId));
                		
                // 디버깅 로그
                System.out.println("[Compare] RecipeItem: " + name + " (" + reqQty + reqUnit + ") vs Fridge: " + 
                                   (fridge != null ? fridge.getItemName() : "NULL") + " -> " + message);
            }
    		return result;
        }
    // convertIngredientUnit 메서드 삭제됨
}