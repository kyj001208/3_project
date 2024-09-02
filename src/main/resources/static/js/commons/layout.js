document.addEventListener('DOMContentLoaded', function() {
  const toggleBtn = document.querySelector('.toggle-btn');
  const floatingMenu = document.querySelector('.floating-menu');

  const menuItems = floatingMenu.querySelectorAll('.menu-item');


  toggleBtn.addEventListener('click', function() {
    if (floatingMenu.classList.contains('hidden')) {
      floatingMenu.classList.remove('hidden');
      setTimeout(() => {
        floatingMenu.classList.add('show');
        showMenuItemsSequentially();
      }, 10);
      toggleBtn.classList.add('active');
      toggleBtn.textContent = '';
    } else {
      hideMenuItemsSequentially(() => {
        floatingMenu.classList.remove('show');
        setTimeout(() => {
          floatingMenu.classList.add('hidden');
        }, 300); // transition duration과 일치
      });
      toggleBtn.classList.remove('active');
      toggleBtn.textContent = '메뉴';
    }
  });


  function showMenuItemsSequentially() {
    menuItems.forEach((item, index) => {
      setTimeout(() => {
        item.classList.add('show');
      },(menuItems.length - 1 - index) * 100); // 각 아이템마다 100ms 간격으로 나타남 역순으로 나타나도록 함
    });
  }

  function hideMenuItemsSequentially(callback) {
    let itemsHidden = 0;
    menuItems.forEach((item, index) => {
      setTimeout(() => {
        item.classList.remove('show');
        itemsHidden++;
        if (itemsHidden === menuItems.length) {
          callback();
        }
      },  index * 100); 
    });
  }
});

});
