# 🍽️ FoodPage (푸드페이지)

사용자 맞춤형 레시피 추천, 스마트 냉장고 재료 관리, 그리고 마트 및 경로 탐색 기능을 아우르는 종합 푸드 플랫폼 프로젝트입니다.
## 👨‍💻 팀 구성원 (Team Members)

| 이름 (Name) | 역할 (Role) | 담당 업무 (Contributions) | GitHub 프로필 |
| :---: | :---: | :--- | :---: |
| 윤훈 | 팀장 / Back-End,Front-End,DB | - 메인페이지 구현, 기획, 페이지별 기능구현, 식품안전나라 api 정보 가공 및 필터링 | [![GitHub](https://img.shields.io/badge/GitHub-Profile-181717?logo=github)](https://github.com/아이디) |
| 한재승 | 팀원 / Back-End,Front-End | - api를 사용한 길찾기 기능 구현, 경로 안내 기능 구현,   | [![GitHub](https://img.shields.io/badge/GitHub-Profile-181717?logo=github)](https://github.com/아이디) |
| 권순혁 | 팀원 /Back-End,Front-End,DB | DB제작, 냉장고 와 DB간의 연결 기눙 구현, 냉장고 페이지 제작   | [![GitHub](https://img.shields.io/badge/GitHub-Profile-181717?logo=github)](https://github.com/아이디) |


## 📖 프로젝트 개요

**FoodPage**는 사용자가 손쉽게 요리 레시피를 찾고, 냉장고 속 재료를 효율적으로 관리할 수 있도록 돕는 웹 애플리케이션입니다. 외부 API에서 제공하는 레시피 정보를 기반으로 자체 알고리즘을 적용하여 조리 시간과 필요한 재료를 직관적으로 제공합니다. 

또한, 레시피에 필요한 재료 중 냉장고에 부족한 것을 계산해 주고, 해당 재료를 사기 위한 주변 대형 마트 검색 및 대중교통 경로 안내 서비스까지 원스톱으로 제공합니다.

### ⚙️ 아키텍처 및 기술 스택
이 프로젝트는 **MVC (Model-View-Controller)** 디자인 패턴을 기반으로 한 Java 웹 애플리케이션입니다.

*   **Backend:** Java Servlet, JSP
*   **Architecture:** MVC Pattern (Controller - Service - DAO/DTO - View)
*   **Frontend:** HTML/CSS/JavaScript, AJAX
*   **External APIs:**
    *   **식품안전나라 API**: 레시피 데이터 수집
    *   **Kakao Local API**: 주소 좌표 변환(Geocoding) 및 반경 내 대형마트(MT1) 검색
    *   **ODsay API**: 대중교통 길찾기 및 소요 시간 안내

---

## ✨ 핵심 기능 (Core Features)

### 1. 레시피 상세 조회 및 조리 시간 추정 알고리즘 (`RecipeUtil`, `RecipeDetailController`)
*   식품안전나라 API에서 불러온 텍스트 위주의 비정형 조리 과정 데이터를 **자체 파싱 알고리즘**을 통해 구조화합니다.
*   **조리 시간 추정:** 
    *   각 조리 단계별 기본 시간(1분) 부여.
    *   "X분", "시간" 등의 키워드 추출.
    *   "데친다", "숙성" 등 특정 조리법 키워드에 따른 가중치 시간(2~20분) 추가 합산.
*   **재료 구조화:** 정규표현식을 활용하여 재료 텍스트를 `이름`, `수량`, `단위`로 세분화하여 객체(DTO)로 관리합니다.

### 2. 스마트 냉장고 관리 (`FridgeAddController`, `FridgeDAO`)
*   사용자가 특정 레시피를 요리하고자 할 때, **필요한 재료와 현재 냉장고에 있는 재료를 비교**하여 부족한 양만 계산합니다.
*   `필요량 - 보유량 = 구매량` 공식을 적용하여, 구매량이 양수(부족함)일 때만 자동으로 장바구니/냉장고 DB에 반영합니다.
*   기본적인 단위 호환성(g, ml)을 체크하여 정밀하게 계산합니다.

### 3. 주변 마트 탐색 및 경로 안내 (`PathFindingServlet`, `OdsayRouteService`)
*   사용자의 위치(집)를 기반으로 Kakao Local API를 통해 1km 이내의 대형마트를 검색합니다.
*   ODsay API를 연동하여 출발지에서 마트까지의 최적 대중교통 경로와 예상 소요 시간을 제공합니다.
*   AJAX를 이용한 비동기 로딩으로 클라이언트 지도 상에 즉시 경로를 렌더링(Raw JSON 반환)하며, 서버단에서는 소요 시간을 가공(DTO 변환)하여 추천 서비스 등에 활용합니다.

---

## 📂 프로젝트 구조 (Directory Structure)

```text
FoodPage/
├── src/main/java/
│   ├── controller/      # Servlet 컨트롤러 (요청 분기 및 View 선택)
│   ├── service/         # 비즈니스 로직 처리 (예: OdsayRouteService)
│   ├── model/           # 데이터 객체 (DTO) 및 DB 접근 (DAO)
│   └── util/            # 공통 유틸리티 및 API 파싱 알고리즘 (예: RecipeUtil)
└── src/main/webapp/
    ├── css/, js/        # 정적 자원 (스타일시트 및 스크립트)
    └── WEB-INF/views/   # JSP 뷰 페이지 (사용자 인터페이스)
```

---

## 🔄 데이터 흐름 예시: 냉장고 재료 자동 추가

1.  **[View]** 사용자가 레시피 상세 페이지(`recipe_detail.jsp`)에서 [재료 담기] 클릭.
2.  **[Controller]** `FridgeAddController`가 요청 수신.
3.  **[Util]** `RecipeUtil`이 레시피를 분석하여 전체 필요 재료 목록 산출.
4.  **[DAO]** `FridgeDAO`를 통해 사용자의 현재 냉장고 재료 데이터 조회.
5.  **[Service/Logic]** 필요 재료와 보유 재료의 양과 단위를 비교하여 부족분 도출.
6.  **[DAO]** `FridgeDAO.addOrUpdate()`를 호출하여 부족한 재료만 DB에 병합(MERGE) 저장.
7.  **[View]** 결과 알림 및 페이지 리다이렉트 처리.
