function getCsrfToken() {
    return document.querySelector('meta[name="_csrf"]').getAttribute('content');
}
function getCsrfHeader() {
    return document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
}

// Hàm chuyển đổi từ tên ngày sang số
function getDayNumberFromAbbr(abbr) {
    const days = {
        'mon': 1,
        'tue': 2,
        'wed': 3,
        'thu': 4,
        'fri': 5,
        'sat': 6,
        'sun': 7
    };
    return days[abbr.toLowerCase()];
}

// Lấy viết tắt từ số ngày 
function getDayAbbr(day) {
    const days = ['mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun'];
    return days[day - 1];
}

// Function to load employee shifts
function loadEmployeeShifts(employeeId) {
    console.log('Loading shifts for employee:', employeeId);
    // Clear all checkboxes first
    clearAllShifts();

    fetch(`/admin/employees/shifts/${employeeId}`, {
        headers: {
            ...getCsrfHeader()
        }
    })
        .then(response => response.json())
        .then(data => {
            if (data.success && data.data) {
                console.log('Received shift data:', data.data);
                // For each shift in the response
                data.data.forEach(shift => {
                    // Convert dayOfWeek number to abbreviation (1 -> 'mon', 2 -> 'tue', etc.)
                    const dayAbbr = getDayAbbr(shift.dayOfWeek);
                    if (dayAbbr) {
                        // Construct checkbox name: shift_[shiftId]_[dayAbbr]
                        const checkboxName = `shift_${shift.shiftId}_${dayAbbr}`;
                        console.log('Looking for checkbox:', checkboxName);
                        // Find and check the corresponding checkbox
                        const checkbox = document.querySelector(`input[name="${checkboxName}"]`);
                        if (checkbox) {
                            checkbox.checked = true;
                            console.log('Checked checkbox:', checkboxName);
                        } else {
                            console.warn('Checkbox not found:', checkboxName);
                        }
                    }
                });
            } else {
                console.error('Failed to load shifts:', data.message);
            }
        })
        .catch(error => {
            console.error('Error loading shifts:', error);
        });
}

// Hàm lưu ca làm việc
function saveShifts() {
    const employeeId = document.getElementById('editEmployeeId').value;
    if (!employeeId) {
        console.error('Employee ID is missing');
        alert('Không tìm thấy ID nhân viên');
        return Promise.reject('Missing employee ID');
    }

    const shifts = [];
    document.querySelectorAll('.shift-checkbox:checked').forEach(checkbox => {
        const [_, shiftId, day] = checkbox.name.split('_');
        if (shiftId && day) {
            const dayNumber = getDayNumberFromAbbr(day);
            if (dayNumber) {
                shifts.push({
                    shiftId: parseInt(shiftId),
                    dayOfWeek: dayNumber,
                    isActive: true
                });
            }
        }
    });

    console.log("Shifts data to be sent:", JSON.stringify(shifts, null, 2));

    // Lấy CSRF token và header
    const token = getCsrfToken();
    const header = getCsrfHeader();

    // Tạo headers với CSRF token
    const headers = {
        'Content-Type': 'application/json'
    };
    headers[header] = token;

    return fetch(`/admin/employees/shifts/${employeeId}`, {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(shifts)
    })
        .then(response => response.json())
        .then(data => {
            if (!data.success) {
                throw new Error(data.message || 'Có lỗi xảy ra khi lưu ca làm việc');
            }
            alert('Lưu ca làm việc thành công');
            loadEmployeeShifts(employeeId);
            return true;
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Có lỗi xảy ra khi lưu ca làm việc: ' + error.message);
            return false;
        });
}

// Make saveShifts available globally
window.saveShifts = saveShifts;

// Password validation function
function validatePassword(password) {
    if (!password) return true; // Empty password is valid for edit form
    const minLength = 8;
    const hasUpperCase = /[A-Z]/.test(password);
    const hasLowerCase = /[a-z]/.test(password);
    const hasNumbers = /\d/.test(password);
    return password.length >= minLength && hasUpperCase && hasLowerCase && hasNumbers;
}

// Function to edit employee
function editEmployee(id) {
    console.log('Đang tải thông tin nhân viên:', id);
    if (!id) {
        console.error('ID nhân viên không hợp lệ');
        alert('ID nhân viên không hợp lệ');
        return;
    }

    // Clear all shifts first before loading new employee data
    clearAllShifts();

    const headers = {
        'Content-Type': 'application/json'
    };
    headers[getCsrfHeader()] = getCsrfToken();

    // Tải thông tin cơ bản của nhân viên
    fetch(`/admin/employees/edit/${id}`, {
        method: 'GET',
        headers: headers
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {
                    throw new Error(errorData.message || 'Không thể tải thông tin nhân viên');
                });
            }
            return response.json();
        })
        .then(result => {
            if (!result.success) {
                throw new Error(result.message || 'Không thể tải thông tin nhân viên');
            }

            const employee = result.data;
            if (!employee) {
                throw new Error('Dữ liệu nhân viên không hợp lệ');
            }

            // Populate form fields
            document.getElementById('editEmployeeId').value = employee.id;
            document.getElementById('editUsername').value = employee.username || '';
            document.getElementById('editFullName').value = employee.fullName || '';
            document.getElementById('editRole').value = employee.role || 'ROLE_EMPLOYEE';

            // Clear password field
            document.getElementById('newPassword').value = '';

            // Show the edit modal
            const editModal = new bootstrap.Modal(document.getElementById('editEmployeeModal'));
            editModal.show();

            // Load shifts immediately after showing modal
            loadEmployeeShifts(employee.id);

            // Remove the old event listener if it exists
            $('#editEmployeeModal').off('shown.bs.modal');
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Có lỗi xảy ra khi tải thông tin nhân viên: ' + error.message);
        });
}

// Function to delete employee
function deleteEmployee(id) {
    if (confirm('Bạn có chắc chắn muốn xóa nhân viên này?')) {
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        fetch(`/admin/employees/delete/${id}`, {
            method: 'POST',
            headers: {
                [csrfHeader]: csrfToken
            }
        })
            .then(response => {
                if (response.ok) {
                    window.location.reload();
                } else {
                    throw new Error('Không thể xóa nhân viên');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert(error.message);
            });
    }
}

// Function to handle shift management
function selectAllShifts() {
    document.querySelectorAll('.shift-checkbox').forEach(checkbox => {
        checkbox.checked = true;
    });
}

function clearAllShifts() {
    document.querySelectorAll('.shift-checkbox').forEach(checkbox => {
        checkbox.checked = false;
    });
}

function cancelAllShifts() {
    const employeeId = document.getElementById('editEmployeeId').value;
    if (!employeeId) return;

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch(`/admin/employees/shifts/${employeeId}`, {
        method: 'DELETE',
        headers: {
            [csrfHeader]: csrfToken
        }
    })
        .then(response => response.json())
        .then(result => {
            if (result.success) {
                clearAllShifts();
                alert('Đã hủy tất cả ca làm việc');
            } else {
                alert(result.message || 'Không thể hủy ca làm việc');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Có lỗi xảy ra khi hủy ca làm việc');
        });
}

// Add event listeners when document is loaded
document.addEventListener('DOMContentLoaded', function () {
    // Add form submit handlers if forms exist
    const addForm = document.getElementById('addEmployeeForm');
    if (addForm) {
        addForm.addEventListener('submit', function (e) {
            // Form will submit normally, no need for custom handling
        });
    }

    const editForm = document.getElementById('editEmployeeForm');
    if (editForm) {
        editForm.addEventListener('submit', function (e) {
            e.preventDefault();
            const formData = new FormData(this);

            // Lấy CSRF token
            const token = getCsrfToken();
            const header = getCsrfHeader();
            const headers = {};
            headers[header] = token;

            // Lưu thông tin cơ bản của nhân viên trước
            fetch('/admin/employees/update', {
                method: 'POST',
                headers: headers,
                body: formData
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Có lỗi xảy ra khi cập nhật nhân viên');
                    }
                    return response.ok;
                })
                .then(() => {
                    // Sau khi lưu thông tin cơ bản, lưu ca làm việc
                    const employeeId = document.getElementById('editEmployeeId').value;
                    const shifts = [];

                    // Lấy tất cả các checkbox đã chọn
                    document.querySelectorAll('.shift-checkbox:checked').forEach(checkbox => {
                        const [_, shiftId, day] = checkbox.name.split('_');
                        if (shiftId && day) {
                            const dayNumber = getDayNumberFromAbbr(day);
                            shifts.push({
                                shiftId: parseInt(shiftId),
                                dayOfWeek: dayNumber,
                                isActive: true
                            });
                        }
                    });

                    // Thêm CSRF token vào header khi gửi dữ liệu shifts
                    const jsonHeaders = {
                        'Content-Type': 'application/json'
                    };
                    jsonHeaders[header] = token;

                    // Gửi dữ liệu ca làm việc lên server
                    return fetch(`/admin/employees/shifts/${employeeId}`, {
                        method: 'POST',
                        headers: jsonHeaders,
                        body: JSON.stringify(shifts)
                    });
                })
                .then(response => response.json())
                .then(data => {
                    if (!data.success) {
                        throw new Error(data.message || 'Có lỗi xảy ra khi cập nhật ca làm việc');
                    }
                    alert('Cập nhật thông tin và ca làm việc thành công');
                    window.location.reload();
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Có lỗi xảy ra: ' + error.message);
                });
        });
    }

    // Hàm sửa nhân viên
    window.editEmployee = editEmployee;

    // Hàm xóa nhân viên
    window.deleteEmployee = deleteEmployee;

    // Validate password on add form
    document.getElementById('addEmployeeForm').addEventListener('submit', function (e) {
        const password = this.querySelector('input[name="password"]').value;
        if (!validatePassword(password)) {
            e.preventDefault();
            alert('Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và số');
        }
    });

    // Validate password on edit form
    document.getElementById('editEmployeeForm').addEventListener('submit', function (e) {
        const newPassword = this.querySelector('input[name="newPassword"]').value;
        if (newPassword && !validatePassword(newPassword)) {
            e.preventDefault();
            alert('Mật khẩu mới phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và số');
        }
    });

    // Reset forms when modals are hidden
    document.getElementById('addEmployeeModal').addEventListener('hidden.bs.modal', function () {
        document.getElementById('addEmployeeForm').reset();
    });

    document.getElementById('editEmployeeModal').addEventListener('hidden.bs.modal', function () {
        document.getElementById('editEmployeeForm').reset();
        // Reset shift form if exists
        const shiftForm = document.getElementById('shiftForm');
        if (shiftForm) {
            shiftForm.reset();
        }
    });

    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Tab change handler
    document.querySelectorAll('a[data-bs-toggle="tab"]').forEach(tab => {
        tab.addEventListener('shown.bs.tab', function (e) {
            if (e.target.getAttribute('href') === '#shifts') {
                const employeeId = document.getElementById('editEmployeeId').value;
                loadEmployeeShifts(employeeId);
            }
        });
    });

    // Function to select all shifts
    window.selectAllShifts = selectAllShifts;

    // Function to clear all shifts
    window.clearAllShifts = clearAllShifts;

    // Hàm hủy ca làm việc cho nhân viên
    window.cancelEmployeeShift = function (employeeId, shiftId, dayOfWeek) {
        if (confirm('Bạn có chắc chắn muốn hủy ca làm việc này?')) {
            // Lấy CSRF token và header
            const token = getCsrfToken();
            const header = getCsrfHeader();

            // Tạo headers với CSRF token
            const headers = {
                'Content-Type': 'application/json'
            };
            headers[header] = token;

            fetch(`/admin/employees/shifts/cancel`, {
                method: 'POST',
                headers: headers,
                body: JSON.stringify({
                    employeeId: employeeId,
                    shiftId: shiftId,
                    dayOfWeek: dayOfWeek
                })
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Không thể hủy ca làm việc');
                    }
                    return response.json();
                })
                .then(result => {
                    if (result.success) {
                        const checkbox = document.querySelector(
                            `input[name="shift_${shiftId}_${getDayAbbr(dayOfWeek)}"]`
                        );
                        if (checkbox) {
                            checkbox.checked = false;
                        }

                        alert('Hủy ca làm việc thành công');
                        loadEmployeeShifts(employeeId);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert(error.message);
                });
        }
    };

    // Hàm hủy tất cả ca làm việc cho nhân viên
    window.cancelAllShifts = cancelAllShifts;
});


document.addEventListener('DOMContentLoaded', function () {
    const scheduleRoot = document.getElementById('employee-schedule-root');
    if (!scheduleRoot) return; // Exit if element not found

    // Lấy CSRF token và header
    const token = getCsrfToken();
    const header = getCsrfHeader();

    // Tạo headers với CSRF token
    const headers = {};
    headers[header] = token;

    // Gọi API để lấy dữ liệu lịch làm việc của tất cả nhân viên
    fetch('/admin/employees/shifts/all', {
        headers: headers
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            if (!Array.isArray(data)) {
                throw new Error('Invalid data format received');
            }

            let tableHtml = `
                <div class="table-responsive">
                    <table class="table table-bordered">
                        <thead>
                            <tr>
                                <th>Ca làm việc</th>
                                <th>Thứ 2</th>
                                <th>Thứ 3</th>
                                <th>Thứ 4</th>
                                <th>Thứ 5</th>
                                <th>Thứ 6</th>
                                <th>Thứ 7</th>
                                <th>Chủ nhật</th>
                            </tr>
                        </thead>
                        <tbody>`;

            // Tạo map cho dữ liệu
            const shiftMap = {
                'Ca 1': {},
                'Ca 2': {},
                'Ca 3': {}
            };

            // Khởi tạo mảng trống cho mỗi ngày trong tuần
            Object.keys(shiftMap).forEach(shift => {
                for (let i = 1; i <= 7; i++) {
                    shiftMap[shift][i] = [];
                }
            });

            // Phân loại dữ liệu
            data.forEach(shift => {
                console.log('Processing shift:', shift); // Debug log
                const { shiftName, employeeName, dayOfWeek } = shift;

                // Kiểm tra dữ liệu hợp lệ
                if (!shiftName || !employeeName || !dayOfWeek) {
                    console.warn('Invalid shift data:', shift);
                    return;
                }

                // Đảm bảo shiftName khớp chính xác với key trong shiftMap
                if (shiftMap.hasOwnProperty(shiftName) && shiftMap[shiftName][dayOfWeek]) {
                    if (!shiftMap[shiftName][dayOfWeek].includes(employeeName)) {
                        shiftMap[shiftName][dayOfWeek].push(employeeName);
                    }
                } else {
                    console.warn(`Invalid shift name or day: ${shiftName}, ${dayOfWeek}`);
                }
            });

            // Tạo các dòng trong bảng
            Object.keys(shiftMap).forEach(shiftName => {
                tableHtml += `<tr><td>${shiftName}</td>`;
                for (let day = 1; day <= 7; day++) {
                    const employees = shiftMap[shiftName][day];
                    tableHtml += `<td>${employees && employees.length ? employees.join(', ') : '-'}</td>`;
                }
                tableHtml += '</tr>';
            });

            tableHtml += `</tbody></table></div>`;
            scheduleRoot.innerHTML = tableHtml;
        })
        .catch(error => {
            console.error('Error loading shift schedule:', error);
            scheduleRoot.innerHTML = `
                <div class="alert alert-danger">
                    Lỗi khi tải dữ liệu lịch làm việc: ${error.message}
                    <br>
                    <button class="btn btn-primary mt-2" onclick="window.location.reload()">Thử lại</button>
                </div>`;
        });
});

