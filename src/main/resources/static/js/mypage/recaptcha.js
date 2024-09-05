function verifyRecaptcha(token) {
    console.log("Verifying reCAPTCHA with token:", token);
    const csrfToken = document.querySelector('input[name="_csrf"]').value;
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch('/verify-recaptcha', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            [csrfHeader]: csrfToken
        },
        body: 'recaptchaToken=' + encodeURIComponent(token)
    })
    .then(response => {
        console.log("Raw response:", response);
        return response.text();
    })
    .then(text => {
        console.log("Response text:", text);
        try {
            const data = JSON.parse(text);
            console.log("Parsed data:", data);
            if (data.success) {
                console.log("reCAPTCHA 검증 성공!");
                document.getElementById('recaptchaStatus').textContent = 'reCAPTCHA 검증 성공!';
                // 여기서 추가 작업 수행 (예: 폼 활성화 또는 다른 동작)
            } else {
                console.log("reCAPTCHA 검증 실패");
                document.getElementById('recaptchaStatus').textContent = 'reCAPTCHA 검증 실패';
            }
        } catch (e) {
            console.error("JSON 파싱 오류:", e);
        }
    })
    .catch(error => {
        console.error('reCAPTCHA 검증 중 오류 발생:', error);
        document.getElementById('recaptchaStatus').textContent = 'reCAPTCHA 검증 오류';
    });
}

document.addEventListener('DOMContentLoaded', function() {
    console.log("DOM fully loaded");
    grecaptcha.ready(function() {
        console.log("reCAPTCHA ready");
        grecaptcha.execute('6LedOzYqAAAAAIkfmP_W-qCzUgUag3__KaDaOKdU', {action: 'homepage'})
            .then(function(token) {
                verifyRecaptcha(token);
            });
    });

    // 메시지 입력 버튼 이벤트 리스너 (선택적)
    document.getElementById('askButton').addEventListener('click', function(e) {
        e.preventDefault();
        // 메시지 입력 처리 로직
    });
});