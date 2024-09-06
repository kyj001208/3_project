// 랜덤 색상을 생성하는 함수
function getRandomColor() {
	const letters = '0123456789ABCDEF';
	let color = '#';
	for (let i = 0; i < 6; i++) {
		color += letters[Math.floor(Math.random() * 16)];
	}
	return color;
}

document.addEventListener('DOMContentLoaded', function() {
	// GSAP ScrollTrigger 플러그인 등록
	initializeGSAP();

	// 프로필 이니셜에 랜덤 배경색 적용
	applyRandomColorsToProfileInitials();

	// 공지사항 관련 기능
	manageAnnouncements();

	// 그룹 삭제 관련 기능
	manageGroupDeletion();

	// 그룹 수정 폼 열기 이벤트 관리
	manageGroupEditing();
	
	// 공지사항 삭제 기능 관리
	manageNoticeDeletion();
});

// GSAP 플러그인 초기화 함수
function initializeGSAP() {
	if (typeof gsap !== 'undefined' && gsap.registerPlugin) {
		gsap.registerPlugin(ScrollTrigger);
	}

	// small-title 텍스트 애니메이션
	gsap.from(".small-title", {
		duration: 0.8,
		opacity: 0,
		y: -30,
		ease: "bounce.out",
		delay: 0.5
	});

	// main-title 텍스트 애니메이션
	gsap.from(".main-title", {
		duration: 1,
		opacity: 0,
		y: -50,
		ease: "elastic.out(1, 0.3)",
		delay: 0.6
	});
}

// 프로필 이니셜에 랜덤 색상 적용하는 함수
function applyRandomColorsToProfileInitials() {
	const profileInitials = document.querySelectorAll('.profile-initial');
	profileInitials.forEach(function(profile) {
		profile.style.backgroundColor = getRandomColor();
	});
}

// 공지사항 관리 함수
function manageAnnouncements() {
	const addNoticeBtn = document.getElementById('create-announcement-btn');
	if (addNoticeBtn) {
		addNoticeBtn.addEventListener('click', function() {
			const announcementText = document.getElementById('announcement-text').value;
			const groupId = this.getAttribute('data-group-id');

			if (!announcementText.trim()) {
				alert("공지사항 내용을 입력해주세요.");
				return;
			}

			// 공지사항 등록 요청
			fetch(`/group/${groupId}/notice`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
					'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')  // CSRF 토큰 추가
				},
				body: JSON.stringify({ notice: announcementText })
			})
			.then(response => {
				if (response.ok) {
					return response.json();  // 성공 응답이 JSON이면 처리
				} else {
					throw new Error('공지사항 등록에 실패했습니다.');
				}
			})
			.then(data => {
				// 공지사항 추가 후 서버로부터 받은 공지사항 ID를 사용하여 삭제 버튼 연결
				const announcementList = document.querySelector('.announcement-list');
				const newAnnouncement = document.createElement('li');
				newAnnouncement.innerHTML = `
					<span>${announcementText}</span>
					<button class="delete-notice-btn" data-notice-id="${data.noticeId}">삭제</button>
				`;

				// 리스트의 첫 번째 항목 앞에 새로운 공지사항 삽입
				if (announcementList.firstChild) {
					announcementList.insertBefore(newAnnouncement, announcementList.firstChild);
				} else {
					announcementList.appendChild(newAnnouncement);  // 리스트가 비어 있으면 그냥 추가
				}

				document.getElementById('announcement-text').value = '';  // 입력 필드 초기화
				alert("공지사항이 성공적으로 추가되었습니다.");

				// 추가된 공지사항 삭제 기능 활성화
				attachDeleteEvent(newAnnouncement);
			})
			.catch(error => {
				console.error('Error:', error);
				alert('공지사항 등록 중 문제가 발생했습니다.');
			});
		});
	}
}

// 공지사항 삭제 관리 함수
function attachDeleteEvent(announcementItem) {
	const deleteButton = announcementItem.querySelector('.delete-notice-btn');
	deleteButton.addEventListener('click', function() {
		const noticeId = this.getAttribute('data-notice-id');
		const groupId = document.getElementById('create-announcement-btn').getAttribute('data-group-id');

		// 삭제 요청
		fetch(`/group/${groupId}/notice/${noticeId}`, {
			method: 'DELETE',
			headers: {
				'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').getAttribute('content')  // CSRF 토큰 추가
			}
		})
		.then(response => {
			if (response.ok) {
				alert('공지사항이 삭제되었습니다.');
				announcementItem.remove();  // 해당 공지사항 리스트에서 삭제
			} else {
				throw new Error('공지사항 삭제에 실패했습니다.');
			}
		})
		.catch(error => {
			console.error('Error:', error);
			alert('공지사항 삭제 중 문제가 발생했습니다.');
		});
	});
}

// 초기 공지사항 삭제 버튼에 이벤트 리스너 추가 (페이지 로드 시 호출)
function manageNoticeDeletion() {
	const deleteButtons = document.querySelectorAll('.delete-notice-btn');
	deleteButtons.forEach(button => {
		const announcementItem = button.parentElement;
		attachDeleteEvent(announcementItem);  // 삭제 이벤트 리스너 연결
	});
}


// 그룹 삭제 관리 함수
function manageGroupDeletion() {
	// Delete 버튼에 이벤트 리스너 추가
	const deleteButtons = document.querySelectorAll('.delete-button');
	deleteButtons.forEach(button => {
		button.addEventListener('click', function() {
			const groupId = this.getAttribute('data-group-id');
			const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
			const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

			// 데이터가 제대로 설정되었는지 확인
			if (groupId && csrfToken && csrfHeader) {
				deleteGroup(groupId, csrfToken, csrfHeader);
			} else {
				console.error('그룹 ID 또는 CSRF 토큰이 설정되지 않았습니다.');
			}
		});
	});
}

// 그룹 삭제 함수
function deleteGroup(id, csrfToken, csrfHeader) {
	if (confirm('정말로 이 그룹을 삭제하시겠습니까?')) {
		fetch(`/delete/${id}`, {
			method: 'DELETE',
			headers: {
				[csrfHeader]: csrfToken,  // CSRF 토큰 설정
				'Content-Type': 'application/json'
			}
		})
			.then(response => {
				if (response.ok) {
					alert('그룹이 삭제되었습니다.');
					window.location.href = '/';  // 홈으로 리디렉션
				} else if (response.status === 403) {
					alert('삭제 권한이 없습니다.');
				} else {
					alert('삭제에 실패했습니다.');
				}
			})
			.catch(error => {
				console.error('Error:', error);
				alert('삭제 중 오류가 발생했습니다.');
			});
	}
}

// 그룹 수정 폼 열기 관리 함수
function manageGroupEditing() {
	const editButtons = document.querySelectorAll('.edit-group-button');
	editButtons.forEach(button => {
		button.addEventListener('click', function() {
			const groupId = this.getAttribute('data-group-id');
			openEditGroupForm(groupId);
		});
	});
}

// 그룹 수정 폼 열기 함수
function openEditGroupForm(groupId) {
	window.location.href = `/edit-group/${groupId}`;
}
