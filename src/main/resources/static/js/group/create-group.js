document.addEventListener('DOMContentLoaded', function() {
	// Quill 초기화 및 이미지 핸들러 설정
	var quillGreeting = new Quill('#group-greeting-editor', {
		theme: 'snow',
		modules: {
			toolbar: {
				container: [
					['bold', 'italic', 'underline'],   // 텍스트 포맷 옵션
					['image']                          // 이미지 버튼 추가
				],
				handlers: {
					'image': function() {
						selectLocalImage();  // 이미지 핸들러 함수 호출
					}
				}
			}
		}
	});

	var quillDescription = new Quill('#group-description-editor', {
		theme: 'snow',
		modules: {
			toolbar: {
				container: [
					['bold', 'italic', 'underline'],
					['image']
				],
				handlers: {
					'image': function() {
						selectLocalImage();  // 이미지 핸들러 함수 호출
					}
				}
			}
		}
	});

	// DOM 변화를 감지하기 위해 MutationObserver 사용
	const targetNode = document.getElementById('group-description-editor');
	const observerOptions = {
		childList: true,
		subtree: true,
		characterData: true
	};

	const observerCallback = function(mutationsList, observer) {
		for (let mutation of mutationsList) {
			if (mutation.type === 'childList') {
				console.log('자식 노드가 추가되거나 제거되었습니다.');
			} else if (mutation.type === 'characterData') {
				console.log('텍스트 내용이 변경되었습니다.');
			}
		}
	};

	const observer = new MutationObserver(observerCallback);
	observer.observe(targetNode, observerOptions);

	// 대표 이미지 미리보기 처리
	document.getElementById('group-image').addEventListener('change', function(event) {
		const file = event.target.files[0];
		if (file && /^image\//.test(file.type)) {
			const reader = new FileReader();
			reader.onload = function(e) {
				const preview = document.getElementById('group-image-preview');
				preview.src = e.target.result;
				preview.style.display = 'block';
			};
			reader.readAsDataURL(file);
		}
	});

	// 이미지 업로드를 위한 로컬 이미지 선택 핸들러
	function selectLocalImage() {
		const input = document.createElement('input');
		input.setAttribute('type', 'file');
		input.setAttribute('accept', 'image/*');
		input.click();

		input.onchange = function() {
			const file = input.files[0];
			if (file && /^image\//.test(file.type)) {
				saveToServer(file);  // 서버로 이미지 업로드
			} else {
				console.warn('이미지 파일만 업로드할 수 있습니다.');
			}
		};
	}

	// 서버에 이미지를 업로드하고 URL 반환
	function saveToServer(file) {
		const formData = new FormData();
		formData.append('image', file);

		const xhr = new XMLHttpRequest();
		xhr.open('POST', '/upload-image', true);
		xhr.onload = function() {
			if (xhr.status === 200) {
				const url = JSON.parse(xhr.responseText).url;
				insertToEditor(url);  // 에디터에 이미지 URL 삽입
			} else {
				console.error('이미지 업로드 실패:', xhr.statusText);
			}
		};
		xhr.onerror = function() {
			console.error('네트워크 오류가 발생했습니다.');
		};
		xhr.send(formData);
	}

	// 에디터에 이미지 URL 삽입
	function insertToEditor(url) {
		const range = quillDescription.getSelection();
		quillDescription.insertEmbed(range.index, 'image', url);
	}

	// 폼 제출 시 Quill 내용을 숨겨진 input에 설정
	document.getElementById('create-group-form').onsubmit = function(e) {
		var greetingHtml = quillGreeting.root.innerHTML;
		var descriptionHtml = quillDescription.root.innerHTML;

		document.getElementById('group-greeting-input').value = greetingHtml;
		document.getElementById('group-description-input').value = descriptionHtml;
	};
});
