let recaptchaWidget;
let recaptchaSiteKey;

// reCAPTCHA 설정 로드
fetch('/api/recaptcha-config')
  .then(response => response.json())
  .then(config => {
    recaptchaSiteKey = config.siteKey;
    if (typeof grecaptcha !== 'undefined' && grecaptcha.render) {
      initializeRecaptcha();
    } else {
      loadRecaptchaScript();
    }
  })
  .catch(error => {
    console.error('reCAPTCHA 설정을 불러오는 데 실패했습니다:', error);
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
  if (typeof grecaptcha !== 'undefined' && grecaptcha.render && document.getElementById('recaptcha-container')) {
    recaptchaWidget = grecaptcha.render('recaptcha-container', {
      'sitekey': recaptchaSiteKey,
      'callback': onRecaptchaSubmit
    });
    console.log("reCAPTCHA 초기화 완료");
  } else {
    console.error('grecaptcha가 로드되지 않았거나 recaptcha-container를 찾을 수 없습니다.');
    setTimeout(initializeRecaptcha, 100);
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
    body: JSON.stringify({ recaptchaResponse: token })
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
  })
  .catch(error => {
    console.error('reCAPTCHA 검증 중 오류 발생:', error);
    alert('인증 과정에서 오류가 발생했습니다. 나중에 다시 시도해주세요.');
  });
}

document.addEventListener('DOMContentLoaded', function () {
  const mypageLink = document.querySelector('a[href="/mypage"]');
  if (mypageLink) {
    mypageLink.addEventListener('click', function (e) {
      e.preventDefault();
      showRecaptchaModal();
    });
  }

  const submitRecaptchaButton = document.getElementById('submitRecaptcha');
  if (submitRecaptchaButton) {
    submitRecaptchaButton.addEventListener('click', function () {
      if (grecaptcha.getResponse(recaptchaWidget)) {
        onRecaptchaSubmit(grecaptcha.getResponse(recaptchaWidget));
      } else {
        alert('reCAPTCHA를 완료해주세요.');
      }
    });
  }
});