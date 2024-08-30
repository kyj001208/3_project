let uploadedImageKey = null;

document.addEventListener('DOMContentLoaded', function() {
    let uploadedImageKey = null;  // 업로드된 이미지 키를 저장할 변수
    
    // Quill 에디터 초기화
    var quillGreeting = new Quill('#quill-editor', {
        theme: 'snow',
        modules: {
            toolbar: {
                container: [
                    ['bold', 'italic', 'underline'],
                    ['image']
                ],
                handlers: {
                    'image': function() {
                        selectLocalImage();
                    }
                }
            }
        }
    });

    // 이미지 업로드 핸들러
    function selectLocalImage() {
        const input = document.createElement('input');
        input.setAttribute('type', 'file');
        input.setAttribute('accept', 'image/*');
        input.click();

        input.onchange = function() {
            const file = input.files[0];
            if (file && /^image\//.test(file.type)) {
                saveToServer(file);
            } else {
                console.warn('이미지 파일만 업로드할 수 있습니다.');
            }
        };
    }

    // 서버에 이미지를 업로드하고 URL 반환
    function saveToServer(file) {
        const formData = new FormData();
        formData.append('file', file);

        uploadImage('/uploadImage', formData)
            .then(result => {
                const url = result.url;
                insertToEditor(url);
            })
            .catch(error => {
                console.error("이미지 업로드 실패:", error);
                alert("이미지 업로드에 실패했습니다. 자세한 내용은 콘솔을 확인하세요.");
            });
    }

    // 에디터에 이미지 URL 삽입
    function insertToEditor(url) {
        const range = quillGreeting.getSelection();
        quillGreeting.insertEmbed(range.index, 'image', url);
    }

    // 대표 이미지 미리보기 처리
    var fileUploadInput = document.getElementById('file-upload');
    if (fileUploadInput) {
        fileUploadInput.addEventListener('change', function(event) {
            const file = event.target.files[0];
            if (file && /^image\//.test(file.type)) {
                fileupload(this);
            }
        });
    } else {
        console.error('file-upload 요소가 DOM에 존재하지 않습니다.');
    }

    // 폼 제출 이벤트 리스너
    document.getElementById('create-review-form').addEventListener('submit', function(e) {
        e.preventDefault();

        var greetingHtml = quillGreeting.root.innerHTML;
        document.getElementById('quill-content-input').value = greetingHtml;

        const titleInput = document.querySelector('input[name="title"]').value;
        const fileInput = document.getElementById('file-upload');
        
        if (fileInput.files.length > 0) {
            fileupload(fileInput).then((imageKey) => {
                if (imageKey) {
                    let hiddenInput = document.getElementById('mainImageBucketKey');
                    hiddenInput.value = imageKey;
                    console.log("Set hidden input value:", hiddenInput.value);

                    console.log("Form data before submission:");
                    console.log("Title:", titleInput);
                    console.log("Content:", greetingHtml);
                    console.log("MainImageBucketKey:", imageKey);

                    if (titleInput && greetingHtml && imageKey) {
                        e.target.submit();
                    } else {
                        alert("모든 필드를 입력하세요.");
                    }
                } else {
                    alert("이미지 업로드에 실패했습니다. 이미지 키가 설정되지 않았습니다.");
                }
            }).catch(error => {
                console.error("파일업로드 실패:", error);
                alert("파일 업로드에 실패했습니다. 자세한 내용은 콘솔을 확인하세요.");
            });
        } else {
            alert("파일을 업로드하세요.");
        }
    });

});

// 서버로 이미지 업로드 요청
function uploadImage(url, formData) {
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;

    return fetch(url, {
        method: "POST",
        body: formData,
        headers: {
            'X-CSRF-TOKEN': csrfToken
        }
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

function fileupload(input) {
    return new Promise((resolve, reject) => {
        const files = input.files;
        if (files.length < 1) {
            console.log("파일이 선택되지 않았어요");
            return resolve();  // 빈 Promise 반환
        }

        const fileType = files[0].type;
        if (!fileType.startsWith('image/')) {
            alert("이미지 파일이 아닙니다.");
            input.value = '';
            return resolve();  // 빈 Promise 반환
        }

        const fileSize = files[0].size;
        if (fileSize > 2 * 1024 * 1024) {
            alert("파일용량제한: 2MB이내의 파일을 사용하세요:" + fileSize);
            input.value = '';
            return resolve();  // 빈 Promise 반환
        }

        // 파일 미리보기
        const reader = new FileReader();
        reader.onload = function(e) {
            let previewDiv = document.getElementById('image-preview');
            previewDiv.src = e.target.result;
            previewDiv.style.display = 'block';  // 이미지 보이도록 설정
        };
        reader.readAsDataURL(files[0]);

        var formData = new FormData();
        formData.append("file", files[0]);

         uploadImage("/uploadImage", formData)
            .then(result => {
                console.log("Server response:", result);
                uploadedImageKey = result.tempKey;
                console.log("Uploaded image key:", uploadedImageKey);
                resolve(uploadedImageKey);  // uploadedImageKey를 반환
            })
            .catch(error => {
                console.error("파일업로드 실패:", error);
                alert("파일 업로드에 실패했습니다. 자세한 내용은 콘솔을 확인하세요.");
                reject(error);
            });
    });
}
