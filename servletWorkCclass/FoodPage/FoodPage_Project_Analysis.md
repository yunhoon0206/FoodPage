# FoodPage 프로젝트 분석 보고서

## 1. 프로젝트 개요 및 아키텍처
이 프로젝트는 **MVC (Model-View-Controller) 패턴**을 따르는 Java 웹 애플리케이션입니다.
*   **목적**: 사용자 맞춤형 레시피 추천, 냉장고 재료 관리, 그리고 마트/경로 탐색 기능을 제공하는 종합 푸드 플랫폼.
*   **구조**:
    *   **Controller (Servlet)**: 사용자의 요청(`*.do`)을 받아 비즈니스 로직을 호출하고 결과 페이지(View)를 결정합니다. (예: `MainController`, `FridgeAddController`)
    *   **Service**: 복잡한 비즈니스 로직을 처리합니다. (예: `MemberService`, `OdsayRouteService`)
    *   **Model (DAO/DTO)**: DB 접근 및 데이터 객체입니다. (예: `FridgeDAO`, `FoodDTO`)
    *   **View (JSP)**: 사용자에게 보여지는 화면입니다. (예: `recipe_detail.jsp`, `fridge.jsp`)
    *   **Util**: 공통 로직 및 API 처리를 담당합니다. (예: `RecipeUtil`, `FoodApiHelper`)

---

## 2. 핵심 기능 및 로직 상세 분석

### A. 레시피 상세 및 조리 시간 계산 (`RecipeUtil`, `RecipeDetailController`)
*   **기능**: 외부 API(식품안전나라)에서 레시피를 가져와 보여줍니다. API가 제공하지 않는 정보(조리 시간, 구조화된 재료)를 **자체 알고리즘**으로 보완했습니다.
*   **로직 (Heuristic Parsing)**:
    *   **조리 시간 추정**: API의 조리 과정 텍스트를 분석합니다.
        *   기본적으로 단계당 1분을 부여합니다.
        *   텍스트 내 "X분", "시간" 등의 키워드를 추출하여 더합니다.
        *   조리법 키워드("데친다", "푹", "숙성")에 따라 가중치 시간(2분~20분)을 추가합니다.
    *   **재료 파싱**: 텍스트 뭉치로 된 재료 정보를 정규표현식을 사용해 `이름`, `수량`, `단위`로 분리하여 객체화(`ParsedIngredientDTO`)합니다.

### B. 스마트 냉장고 관리 (`FridgeAddController`, `FridgeDAO`)
*   **기능**: 레시피를 보고 부족한 재료만 자동으로 계산하여 냉장고 구매 목록에 추가합니다.
*   **로직 흐름**:
    1.  사용자가 특정 레시피에서 "재료 추가"를 요청.
    2.  `RecipeUtil`이 레시피의 필요 재료를 분석.
    3.  `FridgeDAO`를 통해 사용자의 **현재 냉장고 재료**를 조회.
    4.  **단위 호환성 체크**: 보유 재료와 필요 재료의 단위(g, ml 등)가 호환되는지 확인.
    5.  **부족분 계산**: `필요량 - 보유량 = 구매량`. 구매량이 양수일 때만 DB에 추가/업데이트(`MERGE` 로직).

### C. 경로 및 마트 탐색 (`OdsayRouteService`, `PathFindingServlet`)
*   **기능**: 사용자 위치 기반으로 주변 마트를 찾고, 대중교통 경로를 안내합니다.
*   **사용 API**:
    *   **Kakao Local API**: 주소를 좌표(위도/경도)로 변환(Geocoding)하고, 반경 1km 내 대형마트(MT1)를 검색.
    *   **ODsay API**: 출발지(집)에서 목적지(마트)까지의 대중교통 경로 및 소요 시간 탐색.
*   **구현 특이사항**:
    *   `PathFindingServlet`: 클라이언트(AJAX) 요청을 받아 ODsay API의 Raw JSON 데이터를 그대로 반환하는 프록시 역할.
    *   `OdsayRouteService`: 서버단에서 경로 정보를 가공하여 DTO(`RouteResultDTO`)로 변환하는 비즈니스 로직 담당.

---

## 3. 데이터 흐름 (Data Flow)
**예시: 레시피 재료를 냉장고에 추가할 때**
1.  **View**: `recipe_detail.jsp`에서 [재료 담기] 버튼 클릭 (`fridgeAdd.do` 요청).
2.  **Controller**: `FridgeAddController`가 요청 수신.
3.  **Util**: `RecipeUtil`이 레시피 정보를 파싱하여 필요 재료 목록 생성.
4.  **DAO**: `FridgeDAO`가 DB(`fridge` 테이블)에서 내 냉장고 조회.
5.  **Logic**: Controller 내에서 필요량 vs 보유량 비교 로직 수행.
6.  **DAO**: `FridgeDAO.addOrUpdate()`로 부족한 재료 DB 저장.
7.  **View**: 결과 메시지와 함께 다시 레시피 페이지로 리다이렉트.

---

## 4. 발표 대비 예상 질문 (Q&A)

**Q1. 외부 API(식품안전나라)의 데이터가 불친절할 텐데 어떻게 처리했나요?**
> **A:** 맞습니다. API가 단순 텍스트로만 조리 과정을 제공해서, `RecipeUtil` 클래스에서 정규표현식과 키워드 매칭을 이용한 자체 파싱 알고리즘을 구현했습니다. 이를 통해 비정형 텍스트에서 조리 시간과 재료 양을 구조화된 데이터로 변환했습니다.

**Q2. 냉장고 재료 추가 시 단위가 다르면 어떻게 하나요? (예: g과 kg)**
> **A:** 현재 로직(`FridgeAddController`)에서는 기본적인 문자열 매칭과, `g`와 `ml` 간의 호환성만 체크하고 있습니다. 더 정밀한 변환을 위해선 단위 변환 테이블을 별도로 두는 고도화가 필요합니다.

**Q3. 경로 탐색은 왜 두 가지 방식(Servlet vs Service)이 있나요?**
> **A:** `PathFindingServlet`은 프론트엔드에서 비동기(AJAX)로 지도에 경로를 즉시 그리기 위해 Raw 데이터를 전달하는 용도이고, `OdsayRouteService`는 서버단에서 소요 시간을 계산하여 추천 알고리즘 등에 활용하기 위해 데이터를 객체(DTO)로 가공하는 용도로 분리되어 있습니다.

**Q4. DB 트랜잭션 처리는 어떻게 되나요?**
> **A:** 현재 `FridgeDAO` 등에서 단일 쿼리 단위로 실행되고 있습니다. 재료 일괄 추가와 같은 작업은 서비스 계층을 도입하여 `@Transactional` 처리를 하거나, `Connection` 객체를 공유하여 원자성을 보장하도록 개선할 수 있습니다.
