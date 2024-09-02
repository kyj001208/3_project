document.addEventListener('DOMContentLoaded', function() {
  const toggleBtn = document.querySelector('.toggle-btn');
  const floatingMenu = document.querySelector('.floating-menu');
  
  toggleBtn.addEventListener('click', function() {
    if (floatingMenu.style.display === 'block') {
      floatingMenu.style.display = 'none';
      toggleBtn.classList.remove('active');
      toggleBtn.textContent = '메뉴';
    } else {
      floatingMenu.style.display = 'block';
      toggleBtn.classList.add('active');
      toggleBtn.textContent = '';
    }
  });
});
