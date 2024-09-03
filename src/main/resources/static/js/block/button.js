const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

function confirmBlock(currentUserId, targetUserId) {
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
			// AJAX 요청을 보내어 차단 작업을 수행합니다.
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
					if (data.success) {
						Swal.fire({
							title: "차단 완료!",
							text: "사용자가 차단되었습니다.",
							icon: "success",
						}).then(() => {
							location.reload(); // 페이지를 새로고침하여 변경 사항을 반영합니다.
						});
					} else {
						Swal.fire({
							title: "차단 실패!",
							text: "문제가 발생했습니다. 다시 시도해 주세요.",
							icon: "error",
						});
					}
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