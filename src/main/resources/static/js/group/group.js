document.addEventListener('DOMContentLoaded', function() {
    // GSAP ScrollTrigger 플러그인 등록
    gsap.registerPlugin(ScrollTrigger);

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
});

// 함수를 전역 범위로 이동
function openEditGroupModal(groupId) {
    $.ajax({
        url: '/edit-group/' + groupId,
        type: 'GET',
        success: function(data) {
            $('#editGroupFormContainer').html(data); // 서버에서 가져온 HTML을 모달 내용으로 설정
            $('#editGroupModal').show(); // 모달 표시
        },
        error: function() {
            alert('수정 페이지를 로드하는 데 실패했습니다.');
        }
    });
}

function closeEditGroupModal() {
    $('#editGroupModal').hide(); // 모달 숨기기
}
