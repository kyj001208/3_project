document.addEventListener('DOMContentLoaded', function () {

    function showSignupForm() {
        // 애니메이션 시작: container의 width를 늘리고 flex 비율을 변경
        document.querySelector('.container').style.width = '1000px';
        document.querySelector('.image-container').style.flex = '0.8';
        document.querySelector('.login-container').style.flex = '1.2';

        // 비동기로 로그인 컨테이너의 내용을 회원가입 폼으로 변경
        setTimeout(function () {
            document.querySelector('.login-box').innerHTML = `
                <img src="images/logo.png" alt="memmem 로고" class="logo">
                <form action="#" method="post">
                    <input type="text" name="username" placeholder="   아이디를 입력하세요." required>
                    <input type="password" name="password" placeholder="   비밀번호를 입력하세요." required>
                    <input type="email" name="email" placeholder="   이메일을 입력하세요." required>
                    <input type="text" name="phone" oninput="autoHyphen2(this)" maxlength="13" placeholder="   전화번호를 입력하세요." required>
                    <input type="date" name="birthdate" placeholder="   생년월일을 입력하세요." required>
                    <input type="text" name="address" placeholder="   주소를 입력하세요." required>
                    <button type="submit">회원가입</button>
                </form>
                <div class="links">
                    <a href="#" id="login-link">로그인</a>
                </div>
            `;

            // 로그인 링크 클릭 시 다시 로그인 폼으로 변경
            document.getElementById('login-link').addEventListener('click', function (event) {
                event.preventDefault();
                showLoginForm();
            });
        }, 500);
    }

    function showLoginForm() {
        // 애니메이션 시작: container의 width를 줄이고 flex 비율을 원래대로 복구
        document.querySelector('.container').style.width = '1000px';
        document.querySelector('.image-container').style.flex = '1';
        document.querySelector('.login-container').style.flex = '1';

        // 비동기로 회원가입 컨테이너의 내용을 로그인 폼으로 변경
        setTimeout(function () {
            document.querySelector('.login-box').innerHTML = `
                <img src="images/logo.png" alt="memmem 로고" class="logo">
                <form action="#" method="post">
                    <div class="wrap">
                        <input type="text" name="username" placeholder="   아이디를 입력하세요." required>
                        <input type="password" name="password" placeholder="   비밀번호를 입력하세요." required>
                    </div>
                    <label class="remember-me">
                        <input type="checkbox" name="remember"> 아이디 저장
                    </label>
                    <button type="submit">로그인</button>
                    <div class="links">
                        <a href="#" id="signup-link">회원가입하기</a>
                        <a href="#">비밀번호 찾기</a>
                    </div>
                </form>
            `;

            // 회원가입 링크 클릭 시 다시 회원가입 폼으로 변경
            document.getElementById('signup-link').addEventListener('click', function (event) {
                event.preventDefault();
                showSignupForm();
            });
        }, 500);
    }

    // 초기 로그인 폼 설정
    document.getElementById('signup-link').addEventListener('click', function (event) {
        event.preventDefault();
        showSignupForm();
    });
});

// 전화번호에 자동으로 하이픈 추가하는 함수
const autoHyphen2 = (target) => {
    target.value = target.value
        .replace(/[^0-9]/g, '')
        .replace(/^(\d{0,3})(\d{0,4})(\d{0,4})$/g, "$1-$2-$3")
        .replace(/(\-{1,2})$/g, "");
}
