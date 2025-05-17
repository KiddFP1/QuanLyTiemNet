// Add CSRF token functions
function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]').getAttribute('content');
}
function getCsrfHeader() {
    return document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
}

document.addEventListener('DOMContentLoaded', function () {
    // Xử lý nút sửa
    const editButtons = document.querySelectorAll('.btn-edit');
    if (editButtons) {
        editButtons.forEach(button => {
            button.addEventListener('click', function () {
                const serviceId = this.getAttribute('data-id');
                console.log('Editing service with ID:', serviceId); // Debug log

                fetch(`/admin/services/edit/${serviceId}`)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error('Network response was not ok');
                        }
                        return response.json();
                    })
                    .then(service => {
                        console.log('Received service data:', service); // Debug log

                        if (!service) {
                            throw new Error('No service data received');
                        }

                        // Populate form fields
                        const fields = {
                            'editServiceId': service.serviceId,
                            'editServiceName': service.serviceName,
                            'editPrice': service.price,
                            'editCategory': service.category,
                            'editStockQuantity': service.stockQuantity
                        };

                        // Set form field values and log any missing fields
                        Object.entries(fields).forEach(([id, value]) => {
                            const element = document.getElementById(id);
                            if (element) {
                                console.log(`Setting ${id} to:`, value); // Debug log
                                element.value = value;
                            } else {
                                console.error(`Element not found: ${id}`);
                            }
                        });

                        // Show modal
                        const editModal = new bootstrap.Modal(document.getElementById('editServiceModal'));
                        editModal.show();
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        alert('Có lỗi xảy ra khi tải thông tin dịch vụ: ' + error.message);
                    });
            });
        });
    }

    // Xử lý nút xóa
    const deleteButtons = document.querySelectorAll('.btn-delete');
    if (deleteButtons) {
        deleteButtons.forEach(button => {
            button.addEventListener('click', function () {
                if (confirm('Bạn có chắc chắn muốn xóa dịch vụ này?')) {
                    const serviceId = this.getAttribute('data-id');
                    const headers = {};
                    headers[document.querySelector('meta[name="_csrf_header"]').content] = document.querySelector('meta[name="_csrf"]').content;

                    fetch(`/admin/services/delete/${serviceId}`, {
                        method: 'POST',
                        headers: headers
                    })
                        .then(response => {
                            if (response.ok) {
                                window.location.reload();
                            } else {
                                throw new Error('Network response was not ok');
                            }
                        })
                        .catch(error => {
                            console.error('Error:', error);
                            alert('Có lỗi xảy ra khi xóa dịch vụ: ' + error.message);
                        });
                }
            });
        });
    }
});