document.addEventListener('DOMContentLoaded', function() {
	const observer = new IntersectionObserver((entries) => {
		entries.forEach(entry => {
			const target = entry.target;

			if (entry.isIntersecting) {
				// 요소가 화면에 보일 때
				target.classList.remove('animate__fadeOut'); // fadeOut 클래스 제거
				target.classList.add('animate__fadeIn'); // fadeIn 클래스 추가
			} else {
				// 요소가 화면에서 벗어날 때
				target.classList.remove('animate__fadeIn'); // fadeIn 클래스 제거
				target.classList.add('animate__fadeOut'); // fadeOut 클래스 추가
			}
		});
	}, {
		threshold: 0.1 // 요소가 10%만 보이기 시작해도 콜백 호출
	});

	// 감시할 요소 선택
	const targets = document.querySelectorAll('.fade-in-out-element');

	// 요소들에 observer 연결
	targets.forEach(target => {
		observer.observe(target);
	});
});