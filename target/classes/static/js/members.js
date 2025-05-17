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
        fetch(`/admin/members/edit/${id}`)
            .then(response => response.json())
            .then(member => {
                // Populate form fields
                document.querySelector('#addMemberForm input[name="username"]').value = member.username;
                document.querySelector('#addMemberForm input[name="fullName"]').value = member.fullName;
                document.querySelector('#addMemberForm input[name="phone"]').value = member.phone;

                // Update form action
                const form = document.getElementById('addMemberForm');
                form.action = `/admin/members/update/${id}`;

                // Show modal
                new bootstrap.Modal(document.getElementById('addMemberModal')).show();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Không thể tải thông tin thành viên');
            });
    };

    // Hàm xóa thành viên
    const deleteButtons = document.querySelectorAll('.btn-delete');
    if (deleteButtons) {
        deleteButtons.forEach(button => {
            button.addEventListener('click', function () {
                if (confirm('Bạn có chắc chắn muốn xóa thành viên này?')) {
                    const memberId = this.getAttribute('data-id');
                    const headers = {};
                    headers[getCsrfHeader()] = getCsrfToken();

                    fetch(`/admin/members/delete/${memberId}`, {
                        method: 'POST',
                        headers: headers
                    })
                        .then(response => {
                            if (response.ok) {
                                window.location.reload();
                            } else {
                                alert('Có lỗi xảy ra khi xóa thành viên');
                            }
                        });
                }
            });
        });
    }

    // Hàm mở modal nạp tiền
    window.showTopUpModal = function (memberId) {
        document.getElementById('memberId').value = memberId;
        new bootstrap.Modal(document.getElementById('topUpModal')).show();
    };

    // Hàm nạp tiền
    window.topUp = function () {
        const memberId = document.getElementById('memberId').value;
        const amount = document.querySelector('#topUpForm input[name="amount"]').value;

        if (!amount || isNaN(amount) || amount <= 0) {
            alert('Vui lòng nhập số tiền hợp lệ');
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
                    alert(data.message);
                    window.location.reload();
                } else {
                    alert(data.message || 'Có lỗi xảy ra khi nạp tiền');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Có lỗi xảy ra khi nạp tiền');
            });
    };

    // Clean up forms when modals are hidden
    document.getElementById('addMemberModal').addEventListener('hidden.bs.modal', function () {
        document.getElementById('addMemberForm').reset();
    });

    document.getElementById('topUpModal').addEventListener('hidden.bs.modal', function () {
        document.getElementById('topUpForm').reset();
    });
});