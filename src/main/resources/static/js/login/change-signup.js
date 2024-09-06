document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('form').addEventListener('submit', function(event) {
        if (!validateForm()) {
            event.preventDefault();
        } else {
            event.preventDefault(); // 기본 제출 동작 방지
            submitAndRedirect();
        }
    });

    document.getElementById('phone-input').addEventListener('input', function() {
        formatInput(this);
    });
});

function validateForm() {
    var emailInput = document.getElementById("email-input");
    var emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (emailInput.value !== "" && !emailPattern.test(emailInput.value)) {
        alert("이메일 형식이 올바르지 않습니다.");
        emailInput.focus();
        return false;
    }

    var pwInput = document.getElementById("password-input");
    var pwPattern = /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*(),.?:{}|<>]).{8,16}$/;
    if (!pwPattern.test(pwInput.value)) {
        alert("비밀번호는 8~16자리 영문, 숫자, 특수문자를 포함해야 합니다.");
        pwInput.focus();
        return false;
    }

    var pwConfirmInput = document.getElementById("password-confirm-input");
    if (pwInput.value !== pwConfirmInput.value) {
        document.getElementById('error-message').style.display = 'inline'; // 오류 메시지 표시
        pwConfirmInput.focus();
        return false;
    } else {
        document.getElementById('error-message').style.display = 'none'; // 오류 메시지 숨기기
    }

    var nameInput = document.getElementById("name-input");
    var namePattern = /^[가-힣]{2,5}$/;
    if (!namePattern.test(nameInput.value)) {
        alert("이름은 2~5자리 한글만 사용할 수 있습니다.");
        nameInput.focus();
        return false;
    }

    var birthInput = document.getElementById("birth-input");
    var birthPattern = /^(19|20)\d\d-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/;
    if (!birthPattern.test(birthInput.value)) {
        alert("생년월일 형식이 올바르지 않습니다.");
        birthInput.focus();
        return false;
    }

    var phoneInput = document.getElementById("phone-input");
    var phonePattern = /^01[0-9]-[0-9]{4}-[0-9]{4}$/;
    if (!phonePattern.test(phoneInput.value)) {
        alert("휴대전화 번호 형식이 올바르지 않습니다.");
        phoneInput.focus();
        return false;
    }

    return true;
}

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
            var response = JSON.parse(xhr.responseText);
            if (response.status === "success") {
                window.location.href = '/';
            } else {
                alert(response.message || "회원가입에 실패했습니다.");
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

function formatInput(target) {
    let value = target.value.replace(/[^0-9]/g, '');
    if (target.id === 'phone-input') {
        value = value
            .replace(/^(\d{0,3})(\d{0,4})(\d{0,4})$/, "$1-$2-$3")
            .replace(/(\-{1,2})$/, "");
        target.value = value.slice(0, 13);
    }
}