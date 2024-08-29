document.addEventListener('DOMContentLoaded', () => {
    const content = document.getElementById('content');
    const menuItems = document.querySelectorAll('.menu-item');
    const previewContent = document.getElementById('preview-content');

    async function loadContent(section) {
        try {
            console.log(`Attempting to load content for section: ${section}`);
            const response = await fetch(`/mypage/${section}`);
            console.log(`Fetch response status: ${response.status}`);
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            const html = await response.text();
            content.innerHTML = html;
        } catch (error) {
            console.error('Detailed error:', error);
            content.innerHTML = `<h1>오류가 발생했습니다</h1><p>콘텐츠를 불러오는 데 실패했습니다.</p><p>오류 상세: ${error.message}</p>`;
        }
    }

    menuItems.forEach(item => {
        item.addEventListener('mouseenter', function() {
            const section = this.getAttribute('data-section');
            console.log(`Mouse entered menu item for section: ${section}`);
            loadContent(section);
        });
    });

    menuItems.forEach(item => {
        item.addEventListener('click', () => {
            const section = item.dataset.section;
            console.log(`Menu item clicked: ${section}`);
            loadContent(section);
        });
    });
});
