// Add CSRF token functions
function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]').getAttribute('content');
}

function getCsrfHeader() {
    return document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
}

document.addEventListener('DOMContentLoaded', function () {
    // Hàm sửa thành viên
    window.editMember = function (id) {
        const headers = {};
        headers[getCsrfHeader()] = getCsrfToken();

        fetch(`/admin/members/edit/${id}`, {
            method: 'GET',
            headers: headers
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(member => {
                console.log('Received member data:', member); // Debug log

                if (!member || !member.id) {
                    throw new Error('Không nhận được dữ liệu thành viên hợp lệ');
                }

                // Populate form fields
                document.getElementById('editMemberId').value = member.id;
                document.getElementById('editUsername').value = member.username;
                document.getElementById('editFullName').value = member.fullName;
                document.getElementById('editPhone').value = member.phone || '';

                // Show modal
                const editModal = new bootstrap.Modal(document.getElementById('editMemberModal'));
                editModal.show();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Không thể tải thông tin thành viên: ' + error.message);
            });
    };

    // Handle edit form submission
    document.getElementById('editMemberForm')?.addEventListener('submit', function (e) {
        e.preventDefault();

        const formData = new FormData(this);
        const headers = {};
        headers[getCsrfHeader()] = getCsrfToken();

        fetch('/admin/members/update', {
            method: 'POST',
            headers: headers,
            body: new URLSearchParams(formData)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                window.location.reload();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Có lỗi xảy ra khi cập nhật thành viên: ' + error.message);
            });
    });

    // Hàm xóa thành viên
    window.deleteMember = function (id) {
        if (confirm('Bạn có chắc chắn muốn xóa thành viên này?')) {
            const headers = {};
            headers[getCsrfHeader()] = getCsrfToken();

            fetch(`/admin/members/delete/${id}`, {
                method: 'POST',
                headers: headers
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    window.location.reload();
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Có lỗi xảy ra khi xóa thành viên: ' + error.message);
                });
        }
    };

    // Hàm mở modal nạp tiền
    window.showTopUpModal = function (memberId) {
        document.getElementById('memberId').value = memberId;
        document.getElementById('amount').value = ''; // Reset amount field
        new bootstrap.Modal(document.getElementById('topUpModal')).show();
    };

    // Handle top-up form submission
    document.getElementById('topUpForm')?.addEventListener('submit', function (e) {
        e.preventDefault();

        const memberId = document.getElementById('memberId').value;
        const amount = document.getElementById('amount').value;

        if (!amount || isNaN(amount) || amount < 1000) {
            alert('Vui lòng nhập số tiền hợp lệ (tối thiểu 1,000 đ)');
            return;
        }

        const headers = {
            'Content-Type': 'application/json'
        };
        headers[getCsrfHeader()] = getCsrfToken();

        fetch(`/admin/members/topup/${memberId}`, {
            method: 'POST',
            headers: headers,
            body: JSON.stringify({ amount: amount })
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const modal = bootstrap.Modal.getInstance(document.getElementById('topUpModal'));
                    modal.hide();
                    window.location.reload();
                } else {
                    throw new Error(data.message || 'Có lỗi xảy ra khi nạp tiền');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Có lỗi xảy ra khi nạp tiền: ' + error.message);
            });
    });

    // Reset forms when modals are hidden
    document.getElementById('addMemberModal')?.addEventListener('hidden.bs.modal', function () {
        document.getElementById('addMemberForm').reset();
    });

    document.getElementById('editMemberModal')?.addEventListener('hidden.bs.modal', function () {
        document.getElementById('editMemberForm').reset();
    });

    document.getElementById('topUpModal')?.addEventListener('hidden.bs.modal', function () {
        document.getElementById('topUpForm').reset();
    });
});