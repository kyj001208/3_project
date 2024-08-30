function confirmBlock() {
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
			Swal.fire({
				title: "차단 완료!",
				text: "사용자가 차단되었습니다.",
				icon: "success",
			});
		}
	});
}