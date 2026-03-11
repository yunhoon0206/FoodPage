// kakao postcode api 로딩
(function loadPostcodeScript() {
    const script = document.createElement('script');
    script.src = "//t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js";
    document.head.appendChild(script);
})();

// 주소검색 팝업 열기
function openAddrPopup(element) {
    new daum.Postcode({
        oncomplete: function(data) {
            const addr = data.roadAddress || data.jibunAddress;
            element.value = addr; // ★ HTMLElement에 직접 값 넣기
        }
    }).open();
}