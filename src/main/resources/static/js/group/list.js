// list.js
let currentPage = 1;
let isLoading = false;
let currentCategory = "all"; // 현재 선택된 카테고리 저장

$(".category-item a").click(function(e) {
  e.preventDefault();
  currentCategory = $(this).text().toLowerCase(); // 클릭한 카테고리 저장
  loadMoreGroups(); // 새로운 카테고리로 데이터 로드
});

function loadMoreGroups() {
  if (isLoading) return;
  isLoading = true;

  $.ajax({
    url: "/search/places",
    data: { page: currentPage, category: currentCategory },
    success: function(data) {
      $(".group-cards").html(data); // 이전 데이터 초기화
      currentPage = 1; // 페이지 초기화
      isLoading = false;

      if (data.trim() === "") {
        $("#loading-indicator").hide();
      } else {
        $("#loading-indicator").show();
      }
    }
  });
}

// 초기 로딩 표시
$("#loading-indicator").show();