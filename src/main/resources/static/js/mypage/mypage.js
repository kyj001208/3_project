document.addEventListener('DOMContentLoaded', () => {
	const content = document.getElementById('content');
	const menuItems = document.querySelectorAll('.menu-item');
	const sectionTitle = document.getElementById('sectionTitle');
	const initialElement = document.querySelector('.profile-initial');

	if (initialElement) {
		const colors = ['#FF6B6B', '#4ECDC4', '#45B7D1', '#FFA07A', '#20B2AA', '#87CEFA', '#778899'];
		const randomColor = colors[Math.floor(Math.random() * colors.length)];
		initialElement.style.backgroundColor = randomColor;
	}

	async function loadContent(section, title) {
		try {
			console.log(`Attempting to load content for section: ${section}`);
			const response = await fetch(`/mypage/${section}`);
			console.log(`Fetch response status: ${response.status}`);
			if (!response.ok) {
				throw new Error(`HTTP error! status: ${response.status}`);
			}
			const html = await response.text();
			console.log(`Received HTML content for section ${section}: ${html}`);
			content.innerHTML = html;

			// 섹션 제목 업데이트
			sectionTitle.textContent = title || '프로필';

			// 프로필 수정 버튼에 대한 이벤트 리스너 추가
			const editProfileBtn = document.getElementById('edit-profile');
			if (editProfileBtn) {
				editProfileBtn.addEventListener('click', function() {
					loadContent('edit');
				});
			}
		} catch (error) {
			console.error('Detailed error:', error);
			content.innerHTML = `<h1>오류가 발생했습니다</h1><p>콘텐츠를 불러오는 데 실패했습니다.</p><p>오류 상세: ${error.message}</p>`;
		}
	}

	menuItems.forEach(item => {
		item.addEventListener('click', () => {
			const section = item.dataset.section;
			const title = item.dataset.title;
			console.log(`Menu item clicked: ${section}`);
			loadContent(section, title);
		});
	});

	// 초기 페이지 로드 시 프로필 수정 버튼에 대한 이벤트 리스너 추가
	const initialEditProfileBtn = document.getElementById('edit-profile');
	if (initialEditProfileBtn) {
		initialEditProfileBtn.addEventListener('click', function() {
			loadContent('edit');
		});
	}

	// 초기 페이지 로드 시 기본 섹션 (프로필) 로드
	loadContent('profile');
	
	// 이벤트 위임을 사용하여 주소 검색 버튼 클릭 처리
    document.body.addEventListener('click', function(event) {
        if (event.target && event.target.id === 'addressSearchBtn') {
            execDaumPostcode();
        }
    });
});

function formatPhoneNumber(input) {
	// 숫자만 남기고 다른 문자는 제거
	var value = input.value.replace(/[^0-9]/g, '');

	// 전화번호 패턴에 맞춰 하이픈 추가
	if (value.length < 4) {
		input.value = value;
	} else if (value.length < 8) {
		input.value = value.substr(0, 3) + '-' + value.substr(3);
	} else {
		input.value = value.substr(0, 3) + '-' + value.substr(3, 4) + '-' + value.substr(7);
	}
}

// Daum 우편번호 검색 함수
function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            var addr = '';
            if (data.userSelectedType === 'R') {
                addr = data.roadAddress;
            } else {
                addr = data.jibunAddress;
            }
            document.getElementById("address").value = addr;
        }
    }).open({
        left: (window.screen.width / 2) - 300, // 창의 너비에 따라 조정 (기본 너비: 300px)
        top: (window.screen.height / 2) - 400  // 창의 높이에 따라 조정 (기본 높이: 400px)
    });
}