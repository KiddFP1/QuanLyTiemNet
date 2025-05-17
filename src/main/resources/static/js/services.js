// Add CSRF token functions
function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]').getAttribute('content');
}
function getCsrfHeader() {
    return document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
}

document.addEventListener('DOMContentLoaded', function () {
    // Hàm xóa dịch vụ
    const deleteButtons = document.querySelectorAll('.btn-delete');
    if (deleteButtons) {
        deleteButtons.forEach(button => {
            button.addEventListener('click', function () {
                if (confirm('Bạn có chắc chắn muốn xóa dịch vụ này?')) {
                    const serviceId = this.getAttribute('data-id');
                    const headers = {};
                    headers[getCsrfHeader()] = getCsrfToken();

                    fetch(`/admin/services/delete/${serviceId}`, {
                        method: 'POST',
                        headers: headers
                    })
                        .then(response => {
                            if (response.ok) {
                                window.location.reload();
                            } else {
                                alert('Có lỗi xảy ra khi xóa dịch vụ');
                            }
                        });
                }
            });
        });
    }

    // Hàm sửa dịch vụ
    const editButtons = document.querySelectorAll('.btn-edit');
    if (editButtons) {
        editButtons.forEach(button => {
            button.addEventListener('click', function () {
                const serviceId = this.getAttribute('data-id');
                fetch(`/admin/services/edit/${serviceId}`)
                    .then(response => response.json())
                    .then(service => {
                        document.getElementById('editServiceId').value = service.id;
                        document.getElementById('editServiceName').value = service.serviceName;
                        document.getElementById('editPrice').value = service.price;
                        document.getElementById('editCategory').value = service.category;

                        // Show edit modal
                        const editModal = new bootstrap.Modal(document.getElementById('editServiceModal'));
                        editModal.show();
                    });
            });
        });
    }
});