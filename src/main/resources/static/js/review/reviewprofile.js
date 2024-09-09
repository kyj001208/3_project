document.addEventListener('DOMContentLoaded', function() {
    // 프로필 초기 문자를 랜덤 색상으로 설정하는 함수
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

    applyRandomColors();
});
