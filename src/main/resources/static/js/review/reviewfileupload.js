// 전역 스코프에 Quill 에디터 변수 선언
let quill;

// 전역 스코프에 selectCategory 함수를 정의
function selectCategory(button) {
    // 모든 버튼의 활성화 상태를 초기화
    document.querySelectorAll('.category-button').forEach(btn => {
        btn.classList.remove('active');
    });

    // 클릭한 버튼을 활성화 상태로 설정
    button.classList.add('active');

    // 숨겨진 input 필드에 선택된 카테고리 값 설정
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
    quill = new Quill('#quill-editor', {
        theme: 'snow',
        modules: {
            toolbar: {
                container: [
                    ['bold', 'italic', 'underline'],
                   
                ],
                handlers: {
                    'image': selectLocalImage  // 이미지 핸들러 함수로 selectLocalImage 지정
                }
            }
        }
    });

    // 폼 제출 시 Quill 내용을 숨겨진 input에 설정
    document.getElementById('create-review-form').onsubmit = function(e) {
        // Quill 에디터에서 HTML 내용을 가져와 숨겨진 input에 설정
        document.getElementById('quill-content-input').value = quill.root.innerHTML.trim();

        // Quill 내용이 비어 있는지 확인
        if (!document.getElementById('quill-content-input').value) {
            console.warn('후기 내용이 비어 있습니다.');
        }
    };

    // 파일 업로드 input 요소에 change 이벤트 핸들러 추가
    document.getElementById('file-upload').addEventListener('change', function() {
        fileupload(this);
    });
});

// 서버로 이미지를 업로드하고 URL 반환
function saveToServer(file) {
    const formData = new FormData();
    formData.append('file', file);

    uploadImage('/upload-temp', formData)  // `/uploadImg` 엔드포인트로 업로드
        .then(result => {
			console.log("result",result);
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
    const range = quill.getSelection();
    quill.insertEmbed(range.index, 'image', url);
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

    // 파일 미리보기
    const reader = new FileReader();
    reader.onload = function(e) {
        let previewDiv = document.getElementById('image-preview');
        previewDiv.src = e.target.result;
        previewDiv.style.display = 'block';
    };
    reader.readAsDataURL(files[0]);

    // 파일 서버로 업로드
    var formData = new FormData();
    formData.append("file", files[0]);

    uploadImage("/upload-temp", formData)
        .then(result => {
            console.log("Server response:", result);
            const url = result.url;
            const bucketKey = result.bucketKey;
            const orgName = result.orgName;

            if (!bucketKey || !orgName) {
                console.error("Missing data in server response");
                return;
            }

            let bucketKeyInput = document.querySelector('#mainImageBucketKey');
            let orgNameInput = document.querySelector('#mainImageOrgName');

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
