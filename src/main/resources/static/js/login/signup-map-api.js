function sample6_execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function(data) {
            var addr = '';

            if (data.userSelectedType === 'R') {
                addr = data.roadAddress;
            } else {
                addr = data.jibunAddress;
            }

            // 전체 주소를 조합하여 하나의 필드에 설정합니다.
            var combinedAddress = data.zonecode + ' ' + addr;

            document.getElementById('combined_address').value = combinedAddress;

            // 필요하다면, 상세 주소 입력 필드를 포커스 처리할 수 있습니다.
            // document.getElementById('sample6_detailAddress').focus();
        }
    }).open();
}
