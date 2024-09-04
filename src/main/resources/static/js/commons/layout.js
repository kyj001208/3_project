// DOM이 완전히 로드된 후에 실행되는 이벤트 리스너
document.addEventListener('DOMContentLoaded', function() {
  // 토글 버튼과 플로팅 메뉴 요소를 선택
  const toggleBtn = document.querySelector('.toggle-btn');
  const floatingMenu = document.querySelector('.floating-menu');

  // 플로팅 메뉴 내의 모든 메뉴 아이템 선택
  const menuItems = floatingMenu.querySelectorAll('.menu-items');

  // 토글 버튼 클릭 시 동작하는 이벤트 핸들러
  toggleBtn.addEventListener('click', function() {
    // 플로팅 메뉴가 숨겨져 있으면
    if (floatingMenu.classList.contains('hidden')) {
      // 숨겨진 클래스 제거 및 show 클래스 추가 후, 메뉴 아이템을 순차적으로 표시
      floatingMenu.classList.remove('hidden');
      setTimeout(() => {
        floatingMenu.classList.add('show');
        showMenuItemsSequentially();
      }, 10); // 잠깐의 지연 후 show 클래스 추가
      // 토글 버튼에 active 클래스 추가 및 텍스트 비우기
      toggleBtn.classList.add('active');
      toggleBtn.textContent = '';
    } else {
      // 플로팅 메뉴가 표시된 상태에서
      hideMenuItemsSequentially(() => {
        floatingMenu.classList.remove('show');
        // 숨김 애니메이션 후 숨겨진 클래스 추가
        setTimeout(() => {
          floatingMenu.classList.add('hidden');
        }, 300); // transition duration과 일치
      });
      // 토글 버튼에서 active 클래스 제거 및 원래 텍스트로 복원
      toggleBtn.classList.remove('active');
      toggleBtn.textContent = '메뉴';
    }
  });

  function showMenuItemsSequentially() { //메뉴 아이템을 순차적으로 표시하는 함수
    menuItems.forEach((item, index) => {
      setTimeout(() => {
        item.classList.add('show');
      }, (menuItems.length - 1 - index) * 100); // 아이템을 역순으로 나타나게 하며 100ms 간격으로 표시
    });
  }

  function hideMenuItemsSequentially(callback) { // 메뉴 아이템을 순차적으로 숨기는 함수
    let itemsHidden = 0;
    menuItems.forEach((item, index) => {
      setTimeout(() => {
        item.classList.remove('show');
        itemsHidden++;
        // 모든 아이템이 숨겨졌을 때 콜백 호출
        if (itemsHidden === menuItems.length) {
          callback();
        }
      }, index * 100); // 각 아이템을 100ms 간격으로 숨김
    });
  }
});
