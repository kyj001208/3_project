document.addEventListener('DOMContentLoaded', function() {
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
});

function showEditForm() {
    document.querySelector('.review-detail').style.display = 'none';
    document.querySelector('.review-edit-form').style.display = 'block';

    document.querySelectorAll('.btn12').forEach(function(button) {
        button.style.display = 'none';
    });
}

function cancelEdit() {
    document.querySelector('.review-detail').style.display = 'block';
    document.querySelector('.review-edit-form').style.display = 'none';

    document.querySelectorAll('.btn12').forEach(function(button) {
        button.style.display = 'inline-block';
    });
}

function uploadImage(url, formData) {
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');

    return fetch(url, {
        method: 'POST', // PUT 대신 POST 사용
        body: formData,
        headers: {
            'X-CSRF-TOKEN': csrfToken
        }
    }).then(response => response.json());
}

function deletebtnClicked(reId) {
    const csrfToken = $("meta[name='_csrf']").attr("content");
    const csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        url: `/mem/detail/${reId}`,
        type: "DELETE",
        headers: {
            [csrfHeader]: csrfToken
        },
        success: function(response) {
            console.log("삭제되었습니다", response);
            window.location.href = '/mem/review';
        },
        error: function(xhr, status, error) {
            if (xhr.status === 403) {
                Swal.fire({
                    title: "삭제 권한이 없습니다!",
                    text: "본인 글만 삭제할 수 있습니다",
                    icon: "question"
                });
            } else {
                alert('삭제 실패: ' + error);
            }
        }
    });
}
