document.addEventListener('DOMContentLoaded', () => {
  const content = document.getElementById('content');
  const menuItems = document.querySelectorAll('.menu-item');
  const sectionTitle = document.getElementById('sectionTitle');

  async function loadContent(section, title) {
    try {
      console.log(`Attempting to load content for section: ${section}`);
      const response = await fetch(`/mypage/${section}`);
      console.log(`Fetch response status: ${response.status}`);
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      const html = await response.text();
      console.log(`Received HTML content for section ${section}: ${html}`);
      content.innerHTML = html;
      
      // 섹션 제목 업데이트
      sectionTitle.textContent = title || '프로필';

      // 프로필 수정 버튼에 대한 이벤트 리스너 추가
      const editProfileBtn = document.getElementById('edit-profile');
      if (editProfileBtn) {
        editProfileBtn.addEventListener('click', function() {
          loadContent('edit');
        });
      }
    } catch (error) {
      console.error('Detailed error:', error);
      content.innerHTML = `<h1>오류가 발생했습니다</h1><p>콘텐츠를 불러오는 데 실패했습니다.</p><p>오류 상세: ${error.message}</p>`;
    }
  }

  menuItems.forEach(item => {
    item.addEventListener('click', () => {
      const section = item.dataset.section;
      const title = item.dataset.title;
      console.log(`Menu item clicked: ${section}`);
      loadContent(section, title);
    });
  });

  // 초기 페이지 로드 시 프로필 수정 버튼에 대한 이벤트 리스너 추가
  const initialEditProfileBtn = document.getElementById('edit-profile');
  if (initialEditProfileBtn) {
    initialEditProfileBtn.addEventListener('click', function() {
      loadContent('edit');
    });
  }

  // 초기 페이지 로드 시 기본 섹션 (프로필) 로드
  loadContent('profile');
});