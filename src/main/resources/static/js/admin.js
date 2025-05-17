// Add CSRF token functions
function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]').getAttribute('content');
}
function getCsrfHeader() {
    return document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
}

function viewDetails(computerId) {
    $.get('/admin/computers/' + computerId, function (data) {
        $('#computerModal .modal-body').html(data);
        $('#computerModal').modal('show');
    });
}

function maintenance(computerId) {
    let headers = {};
    headers[getCsrfHeader()] = getCsrfToken();

    $.ajax({
        url: '/admin/computers/status/' + computerId,
        type: 'POST',
        headers: headers,
        data: JSON.stringify({ status: 'Maintenance' }),
        contentType: 'application/json',
        success: function () {
            location.reload();
        }
    });
}

function editComputer(id) {
    fetch(`/admin/computers/edit/${id}`)
        .then(response => response.json())
        .then(computer => {
            // Populate form with computer data
            document.getElementById('computerName').value = computer.computerName;
            document.getElementById('location').value = computer.location;
            document.getElementById('status').value = computer.status;

            // Update form action for edit
            const form = document.getElementById('addComputerForm');
            form.action = `/admin/computers/update/${id}`;

            // Show modal
            new bootstrap.Modal(document.getElementById('addComputerModal')).show();
        });
}

function deleteComputer(id) {
    if (confirm('Bạn có chắc chắn muốn xóa máy này?')) {
        let headers = {
            'Content-Type': 'application/json'
        };
        headers[getCsrfHeader()] = getCsrfToken();

        fetch(`/admin/computers/delete/${id}`, {
            method: 'POST',
            headers: headers
        }).then(() => {
            window.location.reload();
        }).catch(error => {
            console.error('Error:', error);
            alert('Có lỗi xảy ra khi xóa máy');
        });
    }
}