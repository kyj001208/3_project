let currentPage = 0;
let isLoading = false;
const pageSize = 10;
let selectedCategory = null;

$(document).ready(function() {
    // URL에서 카테고리 파라미터 추출
    const urlParams = new URLSearchParams(window.location.search);
    selectedCategory = urlParams.get('category') || '';

    // 선택된 카테고리 강조 표시
    if (selectedCategory) {
        $(`.category-item a[href='/group-list?category=${selectedCategory}']`).addClass('active-category');
    } else {
        $(".category-item a[href='/group-list']").addClass('active-category');
    }

    // 페이지 로딩 시 데이터 로딩
    loadGroups(true);

    // 스크롤 이벤트 핸들러
    $(window).on('scroll', throttle(() => {
        if ($(window).scrollTop() + $(window).height() > $(document).height() - 100) {
            loadGroups();
        }
    }, 200));

    // 카테고리 클릭 이벤트 핸들러
    $(".category-item a").click(function(e) {
        e.preventDefault();
        const newCategory = $(this).attr('href').split('=')[1] || '';

        if (selectedCategory !== newCategory) {
            selectedCategory = newCategory;
            $(".category-item a").removeClass('active-category');
            $(this).addClass('active-category');

            // URL 업데이트
            const newUrl = `/group-list?category=${selectedCategory}`;
            history.pushState(null, '', newUrl);

            // 카테고리 변경 시 상태 초기화 및 데이터 로딩
            loadGroups(true);
        }
    });
});
//그룹 데이터를 서버에서 로드하고, 페이지에 표시
function loadGroups(resetContainer = false) {
    // 데이터 로딩 중인 경우, 추가 로드를 방지합니다.
    if (isLoading) return;

    // 데이터 로딩 상태를 활성화합니다.
    isLoading = true;

    // 컨테이너 초기화가 필요할 경우, 그룹 컨테이너를 비우고 페이지를 0으로 리셋합니다.
    if (resetContainer) {
        $('#group-container').empty(); // 그룹 컨테이너를 비웁니다.
        currentPage = 0; // 페이지를 0으로 리셋합니다.
    }

    // AJAX 요청을 통해 그룹 데이터를 서버에서 로드합니다.
    $.ajax({
        url: '/api/groups', // 요청을 보낼 URL입니다.
        method: 'GET', // HTTP 메서드는 GET입니다.
        data: {
            page: currentPage, // 현재 페이지 번호를 전송합니다.
            size: pageSize, // 페이지 당 항목 수를 전송합니다.
            category: selectedCategory // 선택된 카테고리를 전송합니다.
        },
        success: function(groups) {
            // 서버에서 받은 데이터가 유효하고, 데이터가 존재하는 경우
            if (groups && groups.length > 0) {
                // 각 그룹에 대해 카드 HTML을 생성하여 컨테이너에 추가합니다.
                groups.forEach(group => {
                    const groupCardHtml = createGroupCard(group);
                    $('#group-container').append(groupCardHtml);
                });
                // 페이지 번호를 증가시킵니다.
                currentPage++;
            } else {
                // 더 이상 데이터가 없는 경우, 스크롤 이벤트 핸들러를 해제하여 불필요한 호출을 방지합니다.
                $(window).off('scroll');
            }
            // 데이터 로딩 상태를 비활성화합니다.
            isLoading = false;
        },
        error: function(jqXHR, textStatus, errorThrown) {
            // AJAX 요청이 실패한 경우, 에러를 콘솔에 기록합니다.
            console.error("AJAX error: " + textStatus + ' : ' + errorThrown);
            // 데이터 로딩 상태를 비활성화합니다.
            isLoading = false;
        }
    });
}

function createGroupCard(group) {
    return `
        <div class="group-card">
            <div class="group-img-container" style="background-image: url(${group.mainImageUrl});">
                <a href="/group-detail/${group.id}" class="group-link"></a>
            </div>
            <div class="text-container">
                <p class="categories">${group.categoryKoName}</p>
                <p class="date">Since</p>
                <p class="date">${group.createdAt}</p>
                <h3>${group.groupName}</h3>
                <i class="intro">${group.greeting}</i>
                <div class="member">
                	<img class="people" src="/images/group.png" alt="Members">
                    <span>${group.memberCount}</span>
                </div>
            </div>
        </div>
    `;
}
//특정 함수가 일정 시간 간격 내에 여러 번 호출되는 것을 방지하는 역할
function throttle(func, limit) {
    let lastFunc; // 마지막으로 호출된 타이머의 참조를 저장
    let lastRan;  // 마지막으로 실행된 시간 저장

    return function() {
        const context = this; // 함수 호출 시의 컨텍스트
        const args = arguments; // 함수 호출 시 전달된 인자들

        if (!lastRan) {
            // 첫 번째 호출인 경우, 즉시 함수 실행
            func.apply(context, args);
            lastRan = Date.now(); // 마지막 실행 시간 기록
        } else {
            // 이전 호출이 있었던 경우
            clearTimeout(lastFunc); // 이전 타이머 제거
            lastFunc = setTimeout(function() {
                // 제한 시간 간격이 지나면 함수 실행
                if ((Date.now() - lastRan) >= limit) {
                    func.apply(context, args); // 함수 실행
                    lastRan = Date.now(); // 마지막 실행 시간 기록
                }
            }, limit - (Date.now() - lastRan)); // 남은 시간에 맞춰 타이머 설정
        }
    };
}
