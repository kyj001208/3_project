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

    const setupUnblockButtons = () => {
        const unblockForms = document.querySelectorAll('.block-item form');
        unblockForms.forEach(form => {
            form.addEventListener('submit', function(event) {
                event.preventDefault(); // 기본 폼 제출 동작을 막습니다

                Swal.fire({
                    title: "정말 차단 해제하시겠습니까?",
                    text: "해제를 진행하겠습니다!",
                    icon: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#0046FE",
                    cancelButtonColor: "#85A5DB",
                    confirmButtonText: "예, 차단 해제합니다!",
                    cancelButtonText: "취소"
                }).then((result) => {
                    if (result.isConfirmed) {
                        form.submit(); // 사용자가 '예'를 클릭했을 때 폼을 실제로 제출합니다
                    }
                });
            });
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
