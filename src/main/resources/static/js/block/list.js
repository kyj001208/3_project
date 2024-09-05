document.addEventListener('DOMContentLoaded', function() {
	const openModalBtn = document.getElementById('openModal');
	const modal = document.getElementById('myModal');
	const modalBody = document.getElementById('modal-body');
	const closeBtn = document.querySelector('.close');

	// 모달을 여는 버튼 클릭 이벤트
	openModalBtn.addEventListener('click', function(event) {
		event.preventDefault();

		fetch('/block')
			.then(response => response.text())
			.then(data => {
				modalBody.innerHTML = data;
				modal.style.display = 'flex';
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


document.addEventListener('DOMContentLoaded', function() {
	document.querySelectorAll('.unblock-btn').forEach(button => {
		button.addEventListener('click', function() {
			const blockedUserId = this.getAttribute('data-id');

			if (confirm('정말로 이 유저의 차단을 해제하시겠습니까?')) {
				fetch('/unblockUser', {
					method: 'POST',
					headers: {
						'Content-Type': 'application/json',
						'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')
					},
					body: JSON.stringify({ id: blockedUserId })
				})
					.then(response => response.json())
					.then(data => {
						if (data.success) {
							alert('차단 해제 완료');
							// 페이지를 새로 고쳐서 변경된 내용을 반영합니다.
							window.location.reload();
						} else {
							alert('차단 해제에 실패했습니다. 다시 시도해 주세요.');
						}
					})
					.catch(error => {
						console.error('Error:', error);
						alert('서버 오류가 발생했습니다. 나중에 다시 시도해 주세요.');
					});
			}
		});
	});
});