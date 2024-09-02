document.addEventListener('DOMContentLoaded', function () {
    // CSRF 토큰과 헤더 값을 메타 태그에서 가져오기
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    function showSignupForm() {
        document.querySelector('.container').style.width = '1000px';
        document.querySelector('.image-container').style.flex = '0.8';
        document.querySelector('.login-container').style.flex = '1.2';

        document.querySelector('.login-box').innerHTML = `
            <img src="/images/logo.png" alt="memmem 로고" class="logo">
            <form action="/signup" method="post">
                <input type="text" name="name" placeholder="이름을 입력하세요." required>
                <input type="date" name="birthDate" placeholder="생년월일을 입력하세요." required>
                <input type="text" name="RRN" placeholder="주민등록 번호를 입력하세요." required>
                <input type="text" name="address" placeholder="주소를 입력하세요." required>
                <input type="text" name="number" id="phone-input" maxlength="13" placeholder="전화번호를 입력하세요." required>
                <input type="text" name="email" id="domain-txt" placeholder="이메일을 입력하세요." required>
                <select name="domain" id="domain-list">
                    <option value="naver.com">@naver.com</option>
                    <option value="google.com">@google.com</option>
                    <option value="hanmail.net">@hanmail.net</option>
                    <option value="nate.com">@nate.com</option>
                    <option value="kakao.com">@kakao.com</option>
                </select>
                <input type="password" name="password" placeholder="비밀번호를 입력하세요." required>
                <input type="text" name="nickName" placeholder="사용할 닉네임을 입력하세요." required>
                <button type="submit">회원가입</button>
                <input type="hidden" name="_csrf" value="${token}">
            </form>
            <div class="links">
                <a href="#" id="login-link">로그인</a>
            </div>
        `;

        // 이벤트 리스너 추가
        document.getElementById('login-link').addEventListener('click', function (event) {
            event.preventDefault();
            showLoginForm();
        });

        document.getElementById('phone-input').addEventListener('input', function () {
            autoHyphen(this);
        });
    }

    function showLoginForm() {
        document.querySelector('.container').style.width = '1000px';
        document.querySelector('.image-container').style.flex = '1';
        document.querySelector('.login-container').style.flex = '1';

        document.querySelector('.login-box').innerHTML = `
            <img src="/images/logo.png" alt="memmem 로고" class="logo">
            <form action="/login" method="post">
                <div class="wrap">
                    <input type="text" name="email" placeholder="아이디를 입력하세요." required>
                    <input type="password" name="password" placeholder="비밀번호를 입력하세요." required>
                </div>
                <label class="remember-me">
                    <input type="checkbox" name="remember"> 아이디 저장
                </label>
                <button type="submit">로그인</button>
                <div class="links">
                    <a href="#" id="signup-link">회원가입하기</a>
                    <a href="#">비밀번호 찾기</a>
                </div>
                <input type="hidden" name="_csrf" value="${token}">
            </form>
        `;

        // 이벤트 리스너 추가
        document.getElementById('signup-link').addEventListener('click', function (event) {
            event.preventDefault();
            showSignupForm();
        });
    }

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
