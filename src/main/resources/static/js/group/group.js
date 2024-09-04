// 그룹 수정 폼 열기 및 취소 기능
function openEditGroupForm() {
    var groupInfoSection = document.getElementById('groupInfoSection');
    var editGroupSection = document.getElementById('editGroupSection');

    if (groupInfoSection && editGroupSection) {
        groupInfoSection.style.display = 'none';
        editGroupSection.style.display = 'block';
    } else {
        console.error('groupInfoSection or editGroupSection element not found.');
    }
}

function cancelEditGroup() {
    var groupInfoSection = document.getElementById('groupInfoSection');
    var editGroupSection = document.getElementById('editGroupSection');

    if (groupInfoSection && editGroupSection) {
        editGroupSection.style.display = 'none';
        groupInfoSection.style.display = 'block';
    } else {
        console.error('groupInfoSection or editGroupSection element not found.');
    }
}

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

    // Quill 에디터 초기화
    var quillGreeting = new Quill('#group-greeting-editor', {
        theme: 'snow'
    });

    var quillDescription = new Quill('#group-description-editor', {
        theme: 'snow'
    });

    // 폼 전송 시 Quill 에디터 내용을 숨겨진 input에 저장
    var form = document.getElementById('edit-group-form');
    if (form) {
        form.onsubmit = function() {
            document.getElementById('group-greeting-input').value = quillGreeting.root.innerHTML;
            document.getElementById('group-description-input').value = quillDescription.root.innerHTML;
        };
    }
});
