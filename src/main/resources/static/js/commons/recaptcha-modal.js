let recaptchaWidget;
let recaptchaSiteKey;

// reCAPTCHA 설정 로드
fetch('/api/recaptcha-config')
    .then(response => response.json())
    .then(config => {
        recaptchaSiteKey = config.siteKey;
        loadRecaptchaScript();
    });

function loadRecaptchaScript() {
    const script = document.createElement('script');
    script.src = `https://www.google.com/recaptcha/api.js?onload=onRecaptchaLoaded&render=explicit`;
    script.async = true;
    script.defer = true;
    document.head.appendChild(script);
}

function onRecaptchaLoaded() {
    initializeRecaptcha();
}

function initializeRecaptcha() {
    console.log("Initializing reCAPTCHA...");
    console.log("Site Key:", recaptchaSiteKey);
    console.log("grecaptcha:", typeof grecaptcha !== 'undefined' ? "Loaded" : "Not Loaded");

    if (typeof grecaptcha !== 'undefined' && grecaptcha.render && document.getElementById('recaptcha-container')) {
        // reCAPTCHA가 이미 렌더링되었는지 확인
        if (recaptchaWidget === undefined) {
            // reCAPTCHA가 아직 렌더링되지 않은 경우에만 렌더링
            recaptchaWidget = grecaptcha.render('recaptcha-container', {
                'sitekey': recaptchaSiteKey,
                'callback': onRecaptchaSubmit
            });
            console.log("reCAPTCHA rendered successfully.");
        } else {
            console.log("reCAPTCHA is already rendered.");
        }
    } else {
        console.error('grecaptcha not loaded or recaptcha-container not found.');
        setTimeout(initializeRecaptcha, 100); // 100ms 후 재시도
    }
}


function showRecaptchaModal() {
    document.getElementById('recaptchaModal').style.display = 'block';
    if (recaptchaWidget) {
        grecaptcha.reset(recaptchaWidget);
    }
}

function onRecaptchaSubmit(token) {
    fetch('/verify-recaptcha', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ recaptchaToken: token })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            document.getElementById('recaptchaModal').style.display = 'none';
            window.location.href = '/mypage';
        } else {
            alert('인증에 실패했습니다. 다시 시도해주세요.');
            grecaptcha.reset(recaptchaWidget);
        }
    });
}

// 마이페이지 링크에 이벤트 리스너 추가
document.addEventListener('DOMContentLoaded', function () {
    fetch('/api/recaptcha-config')
        .then(response => response.json())
        .then(config => {
            recaptchaSiteKey = config.siteKey;
            if (typeof grecaptcha !== 'undefined' && grecaptcha.render) {
                initializeRecaptcha();
            } else {
                loadRecaptchaScript();
            }
        });

    const mypageLink = document.querySelector('a[href="/mypage"]');
    if (mypageLink) {
        mypageLink.addEventListener('click', function (e) {
            e.preventDefault();
            showRecaptchaModal();
        });
    }

    document.getElementById('submitRecaptcha').addEventListener('click', function () {
        if (recaptchaWidget) {
            grecaptcha.execute(recaptchaWidget);
        }
    });
});
