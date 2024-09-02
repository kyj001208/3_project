function formatInput(target) {
    let value = target.value.replace(/[^0-9]/g, ''); // 숫자만 입력되도록 필터링

    if (target.id === 'phone-input') {
        // 전화번호 형식 (000-0000-0000)
        value = value
            .replace(/^(\d{0,3})(\d{0,4})(\d{0,4})$/g, "$1-$2-$3")
            .replace(/(\-{1,2})$/g, ""); // 잘못된 하이픈 제거
        target.value = value.slice(0, 13); // 최대 13자리로 제한

    } else if (target.id === 'rrn-input') {
        // 주민등록번호 형식 (000000-0000000)
        value = value
            .replace(/^(\d{6})(\d{0,7})$/g, "$1-$2"); // 6자리 뒤에 하이픈 추가
        target.value = value.slice(0, 14); // 최대 14자리로 제한
    }
}
