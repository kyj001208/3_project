// 전역 스코프에 Quill 에디터 변수 선언
let quillGreeting;
let quillDescription;

// 전역 스코프에 selectCategory 함수를 정의
function selectCategory(button) {
	document.querySelectorAll('.category-button').forEach(btn => {
		btn.classList.remove('active');
	});

	button.classList.add('active');
	document.getElementById('selected-category').value = button.getAttribute('data-value');
}

// selectLocalImage 함수 정의
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

// DOMContentLoaded 이벤트 리스너
document.addEventListener('DOMContentLoaded', function() {
	// Quill 에디터 초기화
	quillGreeting = new Quill('#group-greeting-editor', {
		theme: 'snow',
		modules: {
			toolbar: [['bold', 'italic', 'underline']]
		}
	});

	quillDescription = new Quill('#group-description-editor', {
		theme: 'snow',
		modules: {
			toolbar: {
				container: [['bold', 'italic', 'underline'], ['image']], // 툴바 옵션 설정
				handlers: {
					'image': selectLocalImage  // 이미지 핸들러 함수로 selectLocalImage 지정
				}
			}
		}
	});

	// 폼 초기화
	if (document.getElementById('create-group-form')) {
		initCreateGroupForm();
	} else if (document.getElementById('edit-group-form')) {
		initEditGroupForm();
	}

	// 그룹 생성 폼 초기화
	function initCreateGroupForm() {
		document.getElementById('create-group-form').onsubmit = function(e) {
			handleFormSubmit(e);
		};
	}

	// 그룹 수정 폼 초기화
	function initEditGroupForm() {
		document.getElementById('edit-group-form').onsubmit = function(e) {
			handleFormSubmit(e);
		};
	}

	// 폼 제출 핸들러
	function handleFormSubmit(e) {
		// Quill 에디터의 내용을 가져와 숨겨진 input에 설정
		document.getElementById('group-greeting-input').value = quillGreeting.root.innerHTML.trim();
		document.getElementById('group-description-input').value = quillDescription.root.innerHTML.trim();

		// 내용 검증
		if (!document.getElementById('group-greeting-input').value) {
			console.warn('그룹 인삿말 내용이 비어 있습니다.');
			e.preventDefault();
		}

		if (!document.getElementById('group-description-input').value) {
			console.warn('그룹 설명 내용이 비어 있습니다.');
			e.preventDefault();
		}

		if (!document.getElementById('selected-category').value) {
			console.warn('카테고리가 선택되지 않았습니다.');
			e.preventDefault();
		}
	}

	// 썸네일 이미지 파일 업로드 및 미리보기
	document.getElementById('group-image').addEventListener('change', function(event) {
		fileupload(event.target);
	});
});

// 서버로 이미지를 업로드하고 URL 반환
function saveToServer(file) {
	const formData = new FormData();
	formData.append('file', file);

	uploadImage('/uploadImage', formData)
		.then(result => {
			const url = result.url;  // 서버에서 반환된 이미지 URL
			insertToEditor(url);  // 에디터에 이미지 URL 삽입
		})
		.catch(error => {
			console.error("이미지 업로드 실패:", error);
			alert("이미지 업로드에 실패했습니다. 자세한 내용은 콘솔을 확인하세요.");
		});
}

// uploadImage 함수 정의
function uploadImage(url, formData) {
	const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

	let headers = {
		'X-CSRF-TOKEN': csrfToken, // CSRF 토큰을 헤더에 추가
	};

	return fetch(url, {
		method: "POST",
		body: formData,
		headers: headers
	})
	.then(response => {
		if (!response.ok) {
			throw new Error('Server responded with status: ' + response.status);
		}
		return response.json();
	})
	.catch(error => {
		console.error('Error:', error);
		throw error;
	});
}

// 에디터에 이미지 URL 삽입
function insertToEditor(url) {
	const range = quillDescription.getSelection();
	if (range) {
		quillDescription.insertEmbed(range.index, 'image', url);
	} else {
		console.warn('No range selected in Quill editor. Inserting image at the end.');
		quillDescription.insertEmbed(quillDescription.getLength(), 'image', url);
	}
}

// 파일 업로드 및 미리보기
function fileupload(input) {
	const files = input.files;
	if (files.length < 1) {
		console.log("파일이 선택되지 않았어요");
		return;
	}

	const fileType = files[0].type;
	if (!fileType.startsWith('image/')) {
		alert("이미지 파일이 아닙니다.");
		input.value = '';
		return;
	}

	const fileSize = files[0].size;
	if (fileSize > 2 * 1024 * 1024) {
		alert("파일용량제한: 2MB이내의 파일을 사용하세요:" + fileSize);
		input.value = '';
		return;
	}

	const reader = new FileReader();
	reader.onload = function(e) {
		let previewDiv = document.getElementById('group-image-preview');
		previewDiv.innerHTML = `<img src="${e.target.result}" alt="Image preview" style="max-width: 100%; max-height: 200px;">`;
		previewDiv.style.display = 'block';

		document.querySelector('.header-image').style.backgroundImage = `url(${e.target.result})`;
		document.querySelector('.header-image').style.backgroundSize = 'cover';
		document.querySelector('.header-image').style.backgroundPosition = 'center';
	};
	reader.readAsDataURL(files[0]);

	var formData = new FormData();
	formData.append("file", files[0]);

	uploadImage("/uploadImage", formData)
		.then(result => {
			console.log("Server response:", result);
			const url = result.url;
			const bucketKey = result.bucketKey;
			const orgName = result.orgName;

			if (!bucketKey || !orgName) {
				console.error("Missing data in server response");
				return;
			}

			let bucketKeyInput = document.querySelector('input[name="mainImageBucketKey"]');
			let orgNameInput = document.querySelector('input[name="mainImageOrgName"]');

			if (bucketKeyInput) {
				bucketKeyInput.value = bucketKey;
			} else {
				console.error('mainImageBucketKey input field not found!');
			}

			if (orgNameInput) {
				orgNameInput.value = orgName;
			} else {
				console.error('mainImageOrgName input field not found!');
			}

			console.log("Updated bucketKeys:", bucketKeyInput ? bucketKeyInput.value : 'N/A');
			console.log("Updated orgNames:", orgNameInput ? orgNameInput.value : 'N/A');
		})
		.catch(error => {
			console.error("파일업로드 실패:", error);
			alert("파일 업로드에 실패했습니다. 자세한 내용은 콘솔을 확인하세요.");
		});
}
