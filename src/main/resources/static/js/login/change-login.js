document.addEventListener('DOMContentLoaded', function () {
    // CSRF 토큰과 헤더 값을 메타 태그에서 가져오기
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    

    
    // 초기 로그인 폼 설정
    showLoginForm();

    // 전화번호 입력에 하이픈 자동 추가 함수
    function autoHyphen(target) {
        target.value = target.value
            .replace(/[^0-9]/g, '')
            .replace(/^(\d{0,3})(\d{0,4})(\d{0,4})$/g, "$1-$2-$3")
            .replace(/(\-{1,2})$/g, "");
    }
});
