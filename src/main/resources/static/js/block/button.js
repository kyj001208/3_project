const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

function confirmBlock(currentUserId, targetUserId, event) {
    event.preventDefault(); // 기본 폼 제출 동작을 막음

    console.log(currentUserId);
    console.log(targetUserId);
    
    Swal.fire({
        title: "정말 차단하시겠습니까?",
        text: "차단 후에는 되돌릴 수 없습니다!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#0046FE",
        cancelButtonColor: "#85A5DB",
        confirmButtonText: "예, 차단합니다!",
        cancelButtonText: "취소"
    }).then((result) => {
        if (result.isConfirmed) {
            fetch('/blockUser', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'X-Requested-With': 'XMLHttpRequest',
                    'X-CSRF-TOKEN': csrfToken
                },
                body: new URLSearchParams({
                    'blockerId': currentUserId,
                    'blockedId': targetUserId
                })
            })
            .then(response => response.json())
            .then(data => {
                // 서버에서 반환된 메시지를 사용하여 응답을 처리합니다.
                Swal.fire({
                    title: data.message === "차단 완료!" ? "차단 완료!" : "차단 실패!",
                    text: data.message,
                    icon: data.message === "차단 완료!" ? "success" : "error",
                }).then(() => {
                    if (data.message === "차단 완료!") {
                        location.reload();
                    }
                });
            })
            .catch(error => {
                Swal.fire({
                    title: "차단 실패!",
                    text: "문제가 발생했습니다. 다시 시도해 주세요.",
                    icon: "error",
                });
            });
        }
    });
}
