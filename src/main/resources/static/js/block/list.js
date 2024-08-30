document.addEventListener('DOMContentLoaded', function() {
	const openModalBtn = document.getElementById('openModalBtn');
	const modal = document.getElementById('myModal');
	const modalBody = document.getElementById('modal-body');

	openModalBtn.addEventListener('click', function(event) {
		event.preventDefault();

		fetch('/modal')
			.then(response => response.text())
			.then(data => {
				modalBody.innerHTML = data;
				modal.style.display = 'flex';

				const closeBtn = document.querySelector('.close');
				closeBtn.addEventListener('click', function() {
					modal.style.display = 'none'; // 모달을 숨깁니다.
				});
			})
			.catch(error => console.error('Error loading content:', error));
	});

	window.addEventListener('click', function(event) {
		if (event.target === modal) {
			modal.style.display = 'none';
		}
	});
});