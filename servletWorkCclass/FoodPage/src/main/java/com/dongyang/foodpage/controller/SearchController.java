package com.dongyang.foodpage.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.dongyang.foodpage.dto.FoodDTO;
import com.dongyang.foodpage.dto.RouteResultDTO;
import com.dongyang.foodpage.service.OdsayRouteService;
import com.dongyang.foodpage.util.FoodApiHelper;
import com.dongyang.foodpage.util.RecipeUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/search.do")
public class SearchController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        String type = req.getParameter("type"); // 검색유형을 가져옴 jsp에서는 숨겨져있어서 자동으로 가져옴
        String keyword = req.getParameter("keyword"); // 일반 검색용 or 한상 주재료
        String start = req.getParameter("start");
        String end = req.getParameter("end");
        String depTime = req.getParameter("depTime");
        String eatTime = req.getParameter("eatTime");
        
        if (keyword == null && "simple".equals(type)) {
            keyword = req.getParameter("mainIngredient"); //한상차림만 다르게 사용
        }
        
        // 한상 차림용 추가 파라미터
        String soupKeyword = req.getParameter("soupIngredient"); //국과 주음식(일품)각 재료를 사용하기위함

        if ("simple".equals(type)) {
            handleHanSangSearch(keyword, soupKeyword, req, resp); // 한상검색일경우 호출
        } else {
            // 일반 검색 등에서는 keyword 파라미터 하나만 사용
             if (keyword == null || keyword.isEmpty()) { // 상세검색일경우에
                keyword = req.getParameter("ingredient"); // 기존 간편 검색 호환성 (혹시 모를)
            }
            handleNormalSearch(keyword, type, req, resp, start, end, depTime, eatTime);// 상세검색이거나 간편검색일경우
        }
    }

  

    private void handleNormalSearch(String keyword, String type, HttpServletRequest req, HttpServletResponse resp, String start, String end, String depTime, String eatTime) throws ServletException, IOException {
        List<FoodDTO> resultList = new ArrayList<>();
        
        if (keyword != null && !keyword.isEmpty()) {// 키워드가 정상일경에만
            // API 호출 (후보군을 늘리기 위해 20개까지 조회)
            String jsonResult = FoodApiHelper.searchRecipes(keyword, "name", 1, 20);
            resultList = parseJsonToFoodList(jsonResult);
            
            // 결과가 없으면 재료로 재검색
            if (resultList.isEmpty()) {
                 jsonResult = FoodApiHelper.searchRecipes(keyword, "ingredient", 1, 20);
                 resultList = parseJsonToFoodList(jsonResult);
            }
        } else {
            // 키워드가 없을 경우 (오늘의 추천 메뉴 등): 랜덤 추천 로직 실행
            Random random = new Random();
            // 범위를 1~40으로 줄여 데이터 존재 확률을 높임
            int randomStart = random.nextInt(40) + 1; 
            
            // '일품' 카테고리에서 랜덤하게 가져옴
            String jsonResult = FoodApiHelper.searchRecipes("일품", "category", randomStart, randomStart + 19);
            resultList = parseJsonToFoodList(jsonResult);
            
            // [안전장치] 만약 랜덤 결과가 없으면, 확실한 '1페이지' 데이터를 가져옴
            if (resultList.isEmpty()) {
                System.out.println("[SearchController] Random search failed (idx: " + randomStart + "). Retrying with index 1.");
                jsonResult = FoodApiHelper.searchRecipes("일품", "category", 1, 20);
                resultList = parseJsonToFoodList(jsonResult);
            }
            
            // 그래도 없으면 '반찬' 1페이지로 시도 (최후의 수단)
            if (resultList.isEmpty()) {
                jsonResult = FoodApiHelper.searchRecipes("반찬", "category", 1, 20);
                resultList = parseJsonToFoodList(jsonResult);
            }
        }


        // 결과 리스트를 섞고 상위 3개만 선택 (필터링 전에는 섞지 않음, 필터링 후 섞거나, 여기서 섞고 필터링 후 개수 제한)
        // 로직 변경: 필터링을 위해 일단 모든 결과를 유지하고, 필터링 후 셔플 및 제한을 수행하는 것이 좋으나, 
        // 기존 로직 유지하되 필터링이 적용되면 개수가 줄어들 수 있음.
        
        // 시간 계산을 위한 준비
        int availableMinutes = -1;
        if (depTime != null && !depTime.isEmpty() && eatTime != null && !eatTime.isEmpty()) {
            try {
                String[] depParts = depTime.split(":");
                String[] eatParts = eatTime.split(":");
                int depMin = Integer.parseInt(depParts[0]) * 60 + Integer.parseInt(depParts[1]);
                int eatMin = Integer.parseInt(eatParts[0]) * 60 + Integer.parseInt(eatParts[1]);
                
                availableMinutes = eatMin - depMin;
                if (availableMinutes < 0) {
                    availableMinutes += 24 * 60; // 다음날로 간주
                }
                System.out.println("[SearchController] Available time: " + availableMinutes + " minutes");
            } catch (NumberFormatException e) {
                System.out.println("[SearchController] Time parsing error: " + e.getMessage());
            }
        }

        if ("detail".equals(type) && start != null && !start.isEmpty() && end != null && !end.isEmpty()) {
            int movetime = 0;
            boolean routeFound = false;
            
            // 좌표 변환
            double[] startCoords = OdsayRouteService.getGeoCoords(start);
            double[] endCoords = OdsayRouteService.getGeoCoords(end);

            if (startCoords[0] != -1 && endCoords[0] != -1) {
                // 마트 경유 시간 계산 시도
                List<Map<String, String>> marts = OdsayRouteService.searchNearbyMarts(end);
                if (marts != null && !marts.isEmpty()) {
                    try {
                        Map<String, String> nearestMart = marts.get(0);
                        double martLng = Double.parseDouble(nearestMart.get("x"));
                        double martLat = Double.parseDouble(nearestMart.get("y"));

                        List<RouteResultDTO> route1 = OdsayRouteService.getRouteTime(startCoords[0], startCoords[1], martLat, martLng);
                        List<RouteResultDTO> route2 = OdsayRouteService.getRouteTime(martLat, martLng, endCoords[0], endCoords[1]);

                        if (route1 != null && !route1.isEmpty() && route2 != null && !route2.isEmpty()) {
                            movetime = route1.get(0).getTotalTime() + route2.get(0).getTotalTime();
                            routeFound = true;
                        }
                    } catch (Exception e) {
                        System.out.println("Mart route calculation failed: " + e.getMessage());
                    }
                }
                
                // 마트 경유 실패 시 직통 경로 계산
                if (!routeFound) {
                    List<RouteResultDTO> routeResults = OdsayRouteService.getRouteTime(startCoords[0], startCoords[1], endCoords[0], endCoords[1]);
                    if (routeResults != null && !routeResults.isEmpty()) {
                        movetime = routeResults.get(0).getTotalTime();
                        routeFound = true;
                    }
                }
            }

            if (routeFound) {
                req.setAttribute("movetime", movetime);
                
                // 시간 필터링 적용
                java.util.Iterator<FoodDTO> iterator = resultList.iterator();
                while (iterator.hasNext()) {
                    FoodDTO food = iterator.next();
                    int cookingTime = RecipeUtil.parseTimeInMinutes(food.getTime());
                    int totalTime = cookingTime + movetime;

                    // 가용 시간이 설정되어 있고, 총 소요 시간이 가용 시간보다 크다면 제거
                    if (availableMinutes != -1 && totalTime > availableMinutes) {
                        iterator.remove();
                        continue;
                    }

                    String formattedTime;
                    if (totalTime >= 60) {
                        int h = totalTime / 60;
                        int m = totalTime % 60;
                        formattedTime = (m > 0 ? h + "시간 " + m + "분" : h + "시간");
                    } else {
                        formattedTime = totalTime + "분";
                    }
                    food.setTime(formattedTime + " (이동 " + movetime + "분 포함)");
                }
            }
        }
        
        // 필터링 후 섞고 자르기
        if (!resultList.isEmpty()) {
            Collections.shuffle(resultList);
            if (resultList.size() > 3) {
                resultList = resultList.subList(0, 3);
            }
        }

        boolean searchFailed = resultList.isEmpty(); // 검색결과가비엇는지 확인용
        
        // [중요] 상세 조회를 위한 캐싱: 검색 결과를 세션에 저장하여 상세 페이지에서 재사용
        // 이를 통해 상세 조회 시 불필요한 API 호출과 검색 불일치 문제를 해결함
        req.getSession().setAttribute("cachedRecipeList", resultList);
        
        req.setAttribute("searchResult", resultList); // jsp에 보내기위해 값 담기
        req.setAttribute("searchKeyword", keyword);
        req.setAttribute("searchFailed", searchFailed);
        
        
        // 검색 타입에 따라 모드 설정 (상세 검색: 3, 초대 요리: 4, 일반 검색: 1)
        String modeParam = req.getParameter("mode");
        if (modeParam != null && !modeParam.isEmpty()) {
            req.setAttribute("mode", modeParam);
        } else {
            if ("detail".equals(type)) { 
                req.setAttribute("mode", "3");
            } else if ("simple".equals(type)) { // 한상차림
                req.setAttribute("mode", "2");
            } else {
                req.setAttribute("mode", "1");
            }
        }
        
        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }

    private void handleHanSangSearch(String mainKeyword, String soupKeyword, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Map<String, Object>> hanSangSets = new ArrayList<>();
        boolean searchFailed = false;
        Random random = new Random(); // 랜덤 객체 미리 생성

        // 1. 주재료 (일품 요리) 검색
        List<FoodDTO> ilpumCandidates;
        if (mainKeyword != null && !mainKeyword.trim().isEmpty()) {
            ilpumCandidates = parseJsonToFoodList(FoodApiHelper.searchRecipes(mainKeyword, "ingredient", 1, 30));
        } else {
            int start = random.nextInt(100) + 1; // 1~100 랜덤 시작
            ilpumCandidates = parseJsonToFoodList(FoodApiHelper.searchRecipes("일품", "category", start, start + 29));
        }

        // 2. 국&찌개 재료 검색
        List<FoodDTO> soupCandidates;
        if (soupKeyword != null && !soupKeyword.trim().isEmpty()) {
            soupCandidates = parseJsonToFoodList(FoodApiHelper.searchRecipes(soupKeyword, "ingredient", 1, 30));
        } else {
            int startSoup = random.nextInt(50) + 1;
            soupCandidates = parseJsonToFoodList(FoodApiHelper.searchRecipes("국", "category", startSoup, startSoup + 14));
            
            int startStew = random.nextInt(50) + 1;
            soupCandidates.addAll(parseJsonToFoodList(FoodApiHelper.searchRecipes("찌개", "category", startStew, startStew + 14)));
        }

        // 카테고리 필터링
        List<FoodDTO> ilpumList = new ArrayList<>();
        List<FoodDTO> soupList = new ArrayList<>();

        for (FoodDTO food : ilpumCandidates) {
            String cat = food.getCategory();
            if (cat != null && (cat.contains("일품") || cat.contains("찜") || cat.contains("조림") || cat.contains("구이") || cat.contains("볶음") || cat.contains("튀김") || "일품".equals(cat))) {
                ilpumList.add(food);
            }
        }
        if (ilpumList.isEmpty() && !ilpumCandidates.isEmpty()) ilpumList.addAll(ilpumCandidates);

        for (FoodDTO food : soupCandidates) {
            String cat = food.getCategory();
            if (cat != null && (cat.contains("국") || cat.contains("찌개") || cat.contains("전골"))) {
                soupList.add(food);
            }
        }
        if (soupList.isEmpty() && !soupCandidates.isEmpty()) soupList.addAll(soupCandidates);

        if (ilpumList.isEmpty() && soupList.isEmpty()) {
            searchFailed = true;
        } else {
            // 3. 밥, 반찬, 후식 검색 - 랜덤 범위 적용
            int riceStart = random.nextInt(50) + 1;
            List<FoodDTO> riceList = parseJsonToFoodList(FoodApiHelper.searchRecipes("밥", "name", riceStart, riceStart + 19));
            
            int sideStart = random.nextInt(100) + 1;
            List<FoodDTO> sideList = parseJsonToFoodList(FoodApiHelper.searchRecipes("반찬", "category", sideStart, sideStart + 29));
            
            int dessertStart = random.nextInt(50) + 1;
            List<FoodDTO> dessertList = parseJsonToFoodList(FoodApiHelper.searchRecipes("후식", "category", dessertStart, dessertStart + 19));

            // 기본값 대비 혹시 값이 없을경우로
            if (riceList.isEmpty()) riceList.add(new FoodDTO("흰밥", "10분", ""));
            if (sideList.isEmpty()) sideList.add(new FoodDTO("배추김치", "0분", ""));
            if (dessertList.isEmpty()) dessertList.add(new FoodDTO("제철 과일", "0분", ""));
            if (ilpumList.isEmpty()) ilpumList.add(new FoodDTO("추천 일품 요리 없음", "0분", ""));
            if (soupList.isEmpty()) soupList.add(new FoodDTO("추천 국 요리 없음", "0분", ""));

            // 세션 준비
            HttpSession session = req.getSession();
            Map<String, List<FoodDTO>> sessionCache = (Map<String, List<FoodDTO>>) session.getAttribute("hanSangCache"); // 우선 가져운 값들을 모두 저장
            if (sessionCache == null) {
                sessionCache = new HashMap<>();
            }

            // 4. 3개의 세트 구성
            // Random random = new Random(); // 상단에서 이미 생성함
            for (int i = 0; i < 3; i++) {
                Map<String, Object> set = new HashMap<>();
                List<FoodDTO> menuList = new ArrayList<>();
                
                FoodDTO ilpum = ilpumList.get(random.nextInt(ilpumList.size()));//무작위로 뽑기위함
                FoodDTO soup = soupList.get(random.nextInt(soupList.size()));
                FoodDTO rice = riceList.get(random.nextInt(riceList.size()));
                FoodDTO side = sideList.get(random.nextInt(sideList.size()));
                FoodDTO dessert = dessertList.get(random.nextInt(dessertList.size()));

                menuList.add(rice);
                menuList.add(soup);
                menuList.add(side);
                menuList.add(ilpum);
                menuList.add(dessert);
                
                // 정렬: 조리 시간 내림차순
                menuList.sort((f1, f2) -> RecipeUtil.parseTimeInMinutes(f2.getTime()) - RecipeUtil.parseTimeInMinutes(f1.getTime()));
                
                // 최대 시간 계산
                int maxMinutes = 0;
                if (!menuList.isEmpty()) {
                    maxMinutes = RecipeUtil.parseTimeInMinutes(menuList.get(0).getTime());
                }

                // 세션 캐시에 저장 (UUID 생성)
                String setId = UUID.randomUUID().toString();// 문자열로 랜덤한 uuid값을 가지게함
                sessionCache.put(setId, menuList); // 해당 uuid를 가진 id 값이 메뉴의 리스트(5가지음식)을 가지게함

                set.put("menuList", menuList); 
                set.put("totalTime", maxMinutes + "분");
                set.put("setId", setId); // JSP 전달용 ID
                hanSangSets.add(set);
            }
            // 세션 업데이트
            session.setAttribute("hanSangCache", sessionCache);
        }

        req.setAttribute("hanSangResult", hanSangSets); // 결과전달 
        req.setAttribute("searchKeyword", (mainKeyword != null ? mainKeyword : "랜덤") + " & " + (soupKeyword != null ? soupKeyword : "랜덤"));
        req.setAttribute("searchFailed", searchFailed);
        req.setAttribute("mode", "2"); 

        req.getRequestDispatcher("index.jsp").forward(req, resp);
    }

    private List<FoodDTO> parseJsonToFoodList(String json) {// json 변환 용 foodutil로 합칠지 고민중
        List<FoodDTO> list = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            return list;
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(json);
            
            // "COOKRCP01" 객체 가져오기
            JSONObject cookRcp = (JSONObject) jsonObj.get("COOKRCP01");
            if (cookRcp == null) return list;

            // "row" 배열 가져오기
            JSONArray row = (JSONArray) cookRcp.get("row");
            if (row == null) return list;

            for (int i = 0; i < row.size(); i++) {
                JSONObject item = (JSONObject) row.get(i);
                
                String name = (String) item.get("RCP_NM"); // 요리명
                String img = (String) item.get("ATT_FILE_NO_MAIN"); // 이미지 URL
                String rcpSeq = (String) item.get("RCP_SEQ"); // 레시피 고유 번호 파싱
                String category = (String) item.get("RCP_PAT2"); // 요리 종류 파싱
                String ingredients = (String) item.get("RCP_PARTS_DTLS"); // 재료 정보 파싱

                // 조리 과정 파싱
                List<String> manuals = RecipeUtil.parseManualsFromJson(item);
                
                // 시간 계산
                String calculatedTime = RecipeUtil.calculateTotalCookingTime(manuals);
                if (name == null) name = "이름 없음";
                if (RecipeUtil.isBanned(name)) continue; // 벤 리스트 필터링

                if (img == null || img.isEmpty()) img = "images/default.jpg";
                if (rcpSeq == null) rcpSeq = ""; // null 체크
                if (ingredients == null) ingredients = "재료 정보 없음";

                FoodDTO foodDTO = new FoodDTO(name, calculatedTime, img, rcpSeq);
                foodDTO.setManuals(manuals); // manuals 설정
                foodDTO.setCategory(category); // 카테고리 설정
                foodDTO.setIngredients(ingredients); // 재료 설정
                list.add(foodDTO);
            }

        } catch (ParseException e) {
            e.printStackTrace();
            System.out.println("JSON Parsing Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}