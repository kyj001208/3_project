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
    if (typeof gsap !== 'undefined' && gsap.registerPlugin) {
        gsap.registerPlugin(ScrollTrigger);
    }

    // small-title 텍스트가 위에서 아래로 빠르게 떨어지는 느낌
    gsap.from(".small-title", {
        duration: 0.8,
        opacity: 0,
        y: -30,
        ease: "bounce.out",
        delay: 0.5
    });

    // main-title 텍스트가 위에서 아래로 떨어지며 약간의 탄성 효과를 줌
    gsap.from(".main-title", {
        duration: 1,
        opacity: 0,
        y: -50,
        ease: "elastic.out(1, 0.3)",
        delay: 0.6
    });

    // CSRF 토큰과 헤더 이름 가져오기
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

    // Delete 버튼에 이벤트 리스너 추가
    const deleteButtons = document.querySelectorAll('.delete-button');
    deleteButtons.forEach(button => {
        button.addEventListener('click', function() {
            const groupId = this.getAttribute('data-group-id');

            // 데이터가 제대로 설정되었는지 확인
            if (groupId && csrfToken && csrfHeader) {
                deleteGroup(groupId, csrfToken, csrfHeader);
            } else {
                console.error('그룹 ID 또는 CSRF 토큰이 설정되지 않았습니다.');
            }
        });
    });

    // 페이지 로드 시 각 프로필 이니셜에 랜덤 배경색을 적용
    const profileInitials = document.querySelectorAll('.profile-initial');
    profileInitials.forEach(function(profile) {
        profile.style.backgroundColor = getRandomColor();
    });
});

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

function openEditGroupForm(groupId) {
    window.location.href = `/edit-group/${groupId}`;
}
