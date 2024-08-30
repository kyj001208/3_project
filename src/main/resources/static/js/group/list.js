let page = 1; // 현재 페이지
const size = 10; // 한 번에 가져올 그룹 수

// 그룹 목록 데이터를 가져와서 페이지에 추가하는 함수
function loadGroups() {
	$.ajax({
		url: '/api/group-list',
		method: 'GET',
		data: { page: page, size: size },
		success: function(groups) {
			groups.forEach(group => {
				const groupCard = `
                        <div class="group-card">
                            <div class="group-img-container">
                                <a href="/place/detail/${group.id}"> 
                                    <img src="/images/puppy.jpg" alt="${group.title}">
                                </a>
                            </div>
                            <div class="text-container">
                                <h3>${group.title}</h3>
                                <span class="location">${group.location}</span>
                            </div>
                        </div>`;
				$('#group-cards').append(groupCard); // 그룹 카드를 컨테이너에 추가
			});
			page++; // 다음 페이지로 증가
		}
	});
}

// 스크롤이 페이지 하단에 도달할 때 그룹 목록을 추가로 로드하는 이벤트 리스너
$(window).scroll(function() {
	if ($(window).scrollTop() + $(window).height() >= $(document).height()) {
		loadGroups();
	}
});

// 페이지 로드 시 첫 번째 페이지 로드
$(document).ready(function() {
	loadGroups();
});