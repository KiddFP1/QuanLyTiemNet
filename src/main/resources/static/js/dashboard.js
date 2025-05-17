document.addEventListener('DOMContentLoaded', function () {
    console.log('Dashboard script running');

    // Kiểm tra thư viện
    console.log('Recharts:', Recharts);
    console.log('React:', React);
    console.log('ReactDOM:', ReactDOM);

    // Dữ liệu mẫu (để test)
    const revenueData = [
        { date: '31/01', revenue: 1000 },
        { date: '01/02', revenue: 1500 },
        { date: '02/02', revenue: 1200 },
        { date: '03/02', revenue: 1800 },
        { date: '04/02', revenue: 2000 },
        { date: '05/02', revenue: 1600 },
        { date: '06/02', revenue: 2200 }
    ];

    const computerUsageData = [
        { name: 'Đang sử dụng', value: 0 },
        { name: 'Trống', value: 2 },
        { name: 'Bảo trì', value: 0 }
    ];

    // Destructure Recharts components
    const {
        ResponsiveContainer,
        LineChart,
        Line,
        XAxis,
        YAxis,
        Tooltip,
        Legend,
        PieChart,
        Pie
    } = Recharts;

    // Render biểu đồ doanh thu
    try {
        ReactDOM.render(
            React.createElement(ResponsiveContainer, { width: '100%', height: '100%' },
                React.createElement(LineChart, { data: revenueData },
                    React.createElement(XAxis, { dataKey: 'date' }),
                    React.createElement(YAxis),
                    React.createElement(Tooltip),
                    React.createElement(Legend),
                    React.createElement(Line, {
                        type: 'monotone',
                        dataKey: 'revenue',
                        stroke: '#8884d8',
                        name: 'Doanh thu'
                    })
                )
            ),
            document.getElementById('revenue-chart')
        );
    } catch (error) {
        console.error('Lỗi render biểu đồ doanh thu:', error);
    }

    // Render biểu đồ sử dụng máy
    try {
        ReactDOM.render(
            React.createElement(ResponsiveContainer, { width: '100%', height: '100%' },
                React.createElement(PieChart, {},
                    React.createElement(Pie, {
                        data: computerUsageData,
                        dataKey: 'value',
                        nameKey: 'name',
                        fill: '#8884d8'
                    }),
                    React.createElement(Tooltip),
                    React.createElement(Legend)
                )
            ),
            document.getElementById('usage-chart')
        );
    } catch (error) {
        console.error('Lỗi render biểu đồ sử dụng máy:', error);
    }
});

function getCsrfToken() {
    return document.querySelector('meta[name=\"_csrf\"]').getAttribute('content');
}
function getCsrfHeader() {
    return document.querySelector('meta[name=\"_csrf_header\"]').getAttribute('content');
}

let headers = {
    'Content-Type': 'application/json'
};
headers[getCsrfHeader()] = getCsrfToken();

window.deleteEmployee = function (button) {
    const id = button.getAttribute('data-id');
    if (confirm('Bạn có chắc chắn muốn xóa nhân viên này?')) {
        // Tạo headers kèm CSRF
        let headers = {
            'Content-Type': 'application/json'
        };
        headers[getCsrfHeader()] = getCsrfToken();

        // Xóa ca làm việc trước
        fetch(`/admin/employees/shifts/${id}`, {
            method: 'DELETE',
            headers: headers
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể xóa ca làm việc');
                }
                // Sau đó xóa nhân viên
                let headers2 = {
                    'Content-Type': 'application/json'
                };
                headers2[getCsrfHeader()] = getCsrfToken();
                return fetch(`/admin/employees/delete/${id}`, {
                    method: 'POST',
                    headers: headers2
                });
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Không thể xóa nhân viên');
                }
                window.location.reload();
            })
            .catch(error => {
                console.error('Error:', error);
                alert(error.message);
            });
    }
};