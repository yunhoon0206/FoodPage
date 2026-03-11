package com.dongyang.foodpage.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class FoodApiHelper {
    // API 키
    private static final String API_KEY = "a79a7790e07c4e55820d"; 
    // 서비스 ID (조리식품의 레시피 DB)
    private static final String SERVICE_ID = "COOKRCP01";
    // 데이터 타입
    private static final String TYPE = "json";

    /*
     * 식품안전나라 API를 호출하여 레시피 정보를 검색.
     * @param keyword 검색어 (예: "김치", "마늘")
     * @param searchTarget 검색 대상 ("name": 요리명, "ingredient": 재료명)
     * @param startIdx 시작 인덱스 (1부터 시작)
     * @param endIdx 종료 인덱스(20까지하면 20개를 받아옴)
     * @return API 응답 JSON 문자열
     */
    public static String searchRecipes(String keyword, String searchTarget, int startIdx, int endIdx) {
        HttpURLConnection conn = null; // 인터넷연결
        BufferedReader rd = null; // 데이터를 읽기위함
        StringBuilder sb = new StringBuilder(); // 읽은데이터를 쌓아둘 문자열 

        try {
            // URL 빌더 생성
            StringBuilder urlBuilder = new StringBuilder("https://openapi.foodsafetykorea.go.kr/api") ;// api에 요청할 부분 고정된부분은 두고 변동이있는부분은 변수처리
            urlBuilder.append("/" + API_KEY);
            urlBuilder.append("/" + SERVICE_ID);
            urlBuilder.append("/" + TYPE);
            urlBuilder.append("/" + startIdx);
            urlBuilder.append("/" + endIdx);
            
            // 키워드가 유효한 경우 검색 조건 추가
            if (keyword != null && !keyword.trim().isEmpty()) {
                String encodedKeyword = URLEncoder.encode(keyword.trim(), "UTF-8"); //한글처리 
                
                if ("ingredient".equals(searchTarget)) {
                    // 재료명으로 검색 (RCP_PARTS_DTLS)
                    urlBuilder.append("/RCP_PARTS_DTLS=" + encodedKeyword);
                } else if ("category".equals(searchTarget)) {
                    // 요리 종류별 검색 (RCP_PAT2)
                    urlBuilder.append("/RCP_PAT2=" + encodedKeyword);
                } else {
                    // 요리명으로 검색 (RCP_NM) - 기본값
                    urlBuilder.append("/RCP_NM=" + encodedKeyword);
                }
            }

            // URL 객체 생성 및 연결 설정
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();//conn 은 통로의 역할을한다
            conn.setRequestMethod("GET"); // 정보를 주라고 요청
            conn.setConnectTimeout(20000); // 20초 연결 타임아웃 응답이없으면 끊음 
            conn.setReadTimeout(20000);    // 20초 읽기 타임아웃
            
            System.out.println("API Request URL: " + urlBuilder.toString());

            // 응답 코드 확인 (200~300 사이면 정상)
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); // 응답이 정상이라면 stream을 준비
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8")); // 에러가나면 에러메세지를 읽어옴
            }

            // 라인 단위로 읽어서 StringBuilder에 추가
            String line;
            while ((line = rd.readLine()) != null) { // 한줄씩읽어서 저장 모든줄이저장되면 끝남
                sb.append(line);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // 에러 발생 시 JSON 형태의 에러 메시지 반환
            return "{\"error\": \"" + e.getMessage() + "\"}";
        } finally {
            // 리소스 해제
            try {
                if (rd != null) rd.close();
                if (conn != null) conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return sb.toString(); //리턴값 완성된 데이터를 돌려줌
    }

    /*
     * 식품안전나라 API를 호출하여 단일 레시피의 상세 정보를 검색합니다.
     * @param rcpSeq 레시피 고유 번호
     * @return API 응답 JSON 문자열
     */
    public static String searchRecipeDetail(String recipeName) {
        // "이름(name)으로 검색할 건데, 1번부터 1번까지(1개만) 찾아줘"라고 요청
        return searchRecipes(recipeName, "name", 1, 1);
    }
}