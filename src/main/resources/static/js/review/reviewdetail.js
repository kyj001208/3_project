document.addEventListener('DOMContentLoaded', function() {
    // 이미지 업로드 처리
    const fileInput = document.getElementById('file-upload');
    const imagePreview = document.getElementById('image-preview');

    fileInput.addEventListener('change', function(event) {
        const files = event.target.files;
        if (files.length < 1) {
            console.log("파일이 선택되지 않았어요");
            return;
        }

        const file = files[0];
        const fileType = file.type;
        if (!fileType.startsWith('image/')) {
            alert("이미지 파일이 아닙니다.");
            fileInput.value = '';
            return;
        }

        const fileSize = file.size;
        if (fileSize > 2 * 1024 * 1024) {
            alert("파일용량제한: 2MB이내의 파일을 사용하세요:" + fileSize);
            fileInput.value = '';
            return;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
            imagePreview.src = e.target.result;
            imagePreview.style.display = 'block';
        };
        reader.readAsDataURL(file);

        const formData = new FormData();
        formData.append("file", file);

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

                const bucketKeyInput = document.querySelector('#mainImageBucketKey');
                const orgNameInput = document.querySelector('#mainImageOrgName');

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
    });

    // 퀼 에디터 초기화
    window.quill = new Quill('#editContent', {
        theme: 'snow',
        modules: {
            toolbar: [
                [{ 'header': '1' }, { 'header': '2' }],
                ['bold', 'italic', 'underline'],
                [{ 'list': 'ordered' }, { 'list': 'bullet' }],
                ['link', 'image']
            ]
        }
    });

    // 수정 폼을 열 때 퀼 에디터에 기존 내용 로드
    window.showEditForm = function() {
        document.querySelector('.review-detail').style.display = 'none';
        document.querySelector('.review-edit-form').style.display = 'block';
        document.querySelectorAll('.btn12').forEach(function(button) {
            button.style.display = 'none';
        });

        // 기존 내용은 이미 HTML에서 불러오므로, 퀼 에디터에 직접 설정할 필요 없음
    }

    // 수정 폼에서 저장 버튼 클릭 시
    document.querySelector('#editReviewForm').addEventListener('submit', function(e) {
        e.preventDefault();

        // 퀼 에디터의 내용을 hidden input에 저장
        var updatedContent = quill.root.innerHTML;
        document.querySelector('#quill-content-input').value = updatedContent;

        // 폼을 직접 제출
        this.submit();
    });

    // 취소 버튼 클릭 시 수정 폼 숨기기
    window.cancelEdit = function() {
        document.querySelector('.review-detail').style.display = 'block';
        document.querySelector('.review-edit-form').style.display = 'none';
        document.querySelectorAll('.btn12').forEach(function(button) {
            button.style.display = 'inline-block';
        });
    };

    // 이미지 업로드 함수
    function uploadImage(url, formData) {
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

        return fetch(url, {
            method: 'POST',
            body: formData,
            headers: {
                'X-CSRF-TOKEN': csrfToken
            }
        }).then(response => response.json());
    }

    // 삭제 버튼 클릭 시
    window.deletebtnClicked = function(reId) {
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        fetch(`/mem/detail/${reId}`, {
            method: 'DELETE',
            headers: {
                [csrfHeader]: csrfToken
            }
        }).then(response => {
            if (response.ok) {
                console.log("삭제되었습니다");
                window.location.href = '/mem/review';
            } else {
                return response.json().then(error => {
                    if (response.status === 403) {
                        Swal.fire({
                            title: "삭제 권한이 없습니다!",
                            text: "본인 글만 삭제할 수 있습니다",
                            icon: "question"
                        });
                    } else {
                        alert('삭제 실패: ' + error.message);
                    }
                });
            }
        }).catch(error => {
            console.error("삭제 실패:", error);
            alert('삭제 실패: ' + error.message);
        });
    };
});
