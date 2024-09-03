document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('form').addEventListener('submit', function(event) {
        // 폼의 유효성을 검사합니다.
        if (!validateForm()) {
            event.preventDefault(); // 유효성 검사 실패 시 폼 제출을 막습니다.
        } else {
            // 유효성 검사 통과 시 AJAX 요청을 보냅니다.
            submitAndRedirect();
            event.preventDefault(); // 폼의 기본 제출 동작을 막습니다.
        }
    });

    // 입력 필드의 입력을 포맷하는 이벤트 리스너 추가
    document.getElementById('phone-input').addEventListener('input', function() {
        formatInput(this);
    });

});

// 폼 유효성 검사 함수
function validateForm() {
    // 이메일 유효성 검사
    var emailInput = document.getElementById("email-input");
    var emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (emailInput.value !== "" && !emailPattern.test(emailInput.value)) {
        alert("이메일 형식이 올바르지 않습니다.");
        emailInput.focus();
        return false;
    }

    // 비밀번호 유효성 검사
    var pwInput = document.getElementById("password-input");
    var pwPattern = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*(),.?:{}|<>]).{8,16}$/;
    if (!pwPattern.test(pwInput.value)) {
        alert("비밀번호는 8~16자리 영문, 숫자, 특수문자를 포함해야 합니다.");
        pwInput.focus();
        return false;
    }

    // 이름 유효성 검사
    var nameInput = document.getElementById("name-input");
    var namePattern = /^[가-힣]{2,5}$/;
    if (!namePattern.test(nameInput.value)) {
        alert("이름은 2~5자리 한글만 사용할 수 있습니다.");
        nameInput.focus();
        return false;
    }

    // 생년월일 유효성 검사
    var birthInput = document.getElementById("birth-input");
    var birthPattern = /^(19|20)\d\d-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/;
    if (!birthPattern.test(birthInput.value)) {
        alert("생년월일 형식이 올바르지 않습니다.");
        birthInput.focus();
        return false;
    }


    // 휴대전화 유효성 검사
    var phoneInput = document.getElementById("phone-input");
    var phonePattern = /^01[0-9]-[0-9]{4}-[0-9]{4}$/;
    if (!phonePattern.test(phoneInput.value)) {
        alert("휴대전화 번호 형식이 올바르지 않습니다.");
        phoneInput.focus();
        return false;
    }

    // 모든 유효성 검사를 통과하면 true를 반환
    return true;
}

// 폼을 AJAX 요청으로 제출하는 함수
function submitAndRedirect() {
    var form = document.querySelector('form');
    var csrfToken = document.querySelector("input[name='_csrf']").value;
    var csrfHeader = document.querySelector("input[name='_csrf']").name;

    var xhr = new XMLHttpRequest();
    xhr.open('POST', form.action, true);
    xhr.setRequestHeader(csrfHeader, csrfToken);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');

    xhr.onload = function() {
        if (xhr.status === 200) {
            if (xhr.responseText === "success") {
                window.location.href = '/';
            } else {
                alert("회원가입에 실패했습니다.");
            }
        } else {
            alert("서버 오류 발생.");
        }
    };

    xhr.onerror = function() {
        alert("서버와의 연결이 실패했습니다.");
    };

    var formData = new URLSearchParams(new FormData(form)).toString();
    xhr.send(formData);
}

// 입력 필드를 포맷하는 함수
function formatInput(target) {
    let value = target.value.replace(/[^0-9]/g, ''); // 숫자만 입력되도록 필터링

    if (target.id === 'phone-input') {
        // 전화번호 형식 (000-0000-0000)
        value = value
            .replace(/^(\d{0,3})(\d{0,4})(\d{0,4})$/g, "$1-$2-$3")
            .replace(/(\-{1,2})$/g, ""); // 잘못된 하이픈 제거
        target.value = value.slice(0, 13); // 최대 13자리로 제한

    } 
}
