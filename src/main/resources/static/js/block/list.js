document.addEventListener('DOMContentLoaded', function() {
	const openModalBtn = document.getElementById('openModal');
	const modal = document.getElementById('myModal');
	const modalBody = document.getElementById('modal-body');
	const closeBtn = document.querySelector('.close');

	const getRandomColor = () => {
		const letters = '0123456789ABCDEF';
		let color = '#';
		for (let i = 0; i < 6; i++) {
			color += letters[Math.floor(Math.random() * 16)];
		}
		return color;
	};



	const applyRandomColors = () => {
		const profileInitials = document.querySelectorAll('.profile-initial');
		profileInitials.forEach(initial => {
			initial.style.backgroundColor = getRandomColor();
		});
	};

	// 모달을 여는 버튼 클릭 이벤트
	openModalBtn.addEventListener('click', function(event) {
		event.preventDefault();

		fetch('/block')
			.then(response => response.text())
			.then(data => {
				modalBody.innerHTML = data;
				modal.style.display = 'flex';
				applyRandomColors();
				setupUnblockButtons();
			})
			.catch(error => console.error('Error loading content:', error));
	});

	// 모달 닫기 버튼 클릭 이벤트
	closeBtn.addEventListener('click', function() {
		modal.style.display = 'none';
	});

	// 모달 바깥 클릭 시 모달 닫기
	window.addEventListener('click', function(event) {
		if (event.target === modal) {
			modal.style.display = 'none';
		}
	});
});