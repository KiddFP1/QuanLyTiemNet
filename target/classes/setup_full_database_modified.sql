/*
* Script thiết lập đầy đủ cơ sở dữ liệu cho hệ thống quản lý tiệm net
* Phiên bản sửa đổi để tránh lỗi với cột Email
*/

-- 1. Tạo bảng WorkShift (nếu chưa tồn tại)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'WorkShift')
BEGIN
    CREATE TABLE WorkShift (
        shiftId INT IDENTITY(1,1) PRIMARY KEY,
        shiftName NVARCHAR(50) NOT NULL,
        startTime TIME NOT NULL,
        endTime TIME NOT NULL,
        description NVARCHAR(255)
    );
    
    PRINT 'Đã tạo bảng WorkShift';
    
    -- Thêm dữ liệu mẫu cho WorkShift
    INSERT INTO WorkShift (shiftName, startTime, endTime, description)
    VALUES
        (N'Ca 1', '08:00:00', '12:00:00', N'Ca sáng'),
        (N'Ca 2', '13:00:00', '17:00:00', N'Ca chiều'),
        (N'Ca 3', '18:00:00', '22:00:00', N'Ca tối');
    
    PRINT 'Đã thêm dữ liệu cho bảng WorkShift';
END
ELSE 
BEGIN
    -- Nếu không có dữ liệu, thêm dữ liệu mẫu
    IF NOT EXISTS (SELECT * FROM WorkShift)
    BEGIN
        INSERT INTO WorkShift (shiftName, startTime, endTime, description)
        VALUES
            (N'Ca 1', '08:00:00', '12:00:00', N'Ca sáng'),
            (N'Ca 2', '13:00:00', '17:00:00', N'Ca chiều'),
            (N'Ca 3', '18:00:00', '22:00:00', N'Ca tối');
        
        PRINT 'Đã thêm dữ liệu cho bảng WorkShift';
    END
END

-- 2. Kiểm tra bảng Employee
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Employee')
BEGIN
    CREATE TABLE Employee (
        EmployeeID INT IDENTITY(1,1) PRIMARY KEY,
        Username VARCHAR(50) NOT NULL UNIQUE,
        Password VARCHAR(255) NOT NULL,
        FullName NVARCHAR(100) NOT NULL,
        Role VARCHAR(50) NOT NULL
    );
    
    PRINT 'Đã tạo bảng Employee';
    
    -- Thêm tài khoản admin
    INSERT INTO Employee (Username, Password, FullName, Role)
    VALUES ('admin', 'admin123', N'Quản trị viên', 'ROLE_ADMIN');
    
    -- Thêm tài khoản nhân viên mẫu
    INSERT INTO Employee (Username, Password, FullName, Role)
    VALUES ('nv1', '123456', N'Nhân viên 1', 'ROLE_EMPLOYEE');
    
    PRINT 'Đã thêm dữ liệu cho bảng Employee';
END
ELSE
BEGIN
    -- Cập nhật mật khẩu và vai trò cho tài khoản admin nếu đã tồn tại
    IF EXISTS (SELECT * FROM Employee WHERE Username = 'admin')
    BEGIN
        UPDATE Employee
        SET Password = 'admin123', Role = 'ROLE_ADMIN'
        WHERE Username = 'admin';
        
        PRINT 'Đã cập nhật tài khoản admin';
    END
    ELSE
    BEGIN
        -- Thêm tài khoản admin nếu chưa có
        INSERT INTO Employee (Username, Password, FullName, Role)
        VALUES ('admin', 'admin123', N'Quản trị viên', 'ROLE_ADMIN');
        
        PRINT 'Đã thêm tài khoản admin';
    END
    
    -- Cập nhật hoặc thêm tài khoản nhân viên
    IF EXISTS (SELECT * FROM Employee WHERE Username = 'nv1')
    BEGIN
        UPDATE Employee
        SET Password = '123456', Role = 'ROLE_EMPLOYEE'
        WHERE Username = 'nv1';
        
        PRINT 'Đã cập nhật tài khoản nhân viên';
    END
    ELSE
    BEGIN
        -- Thêm tài khoản nhân viên nếu chưa có
        INSERT INTO Employee (Username, Password, FullName, Role)
        VALUES ('nv1', '123456', N'Nhân viên 1', 'ROLE_EMPLOYEE');
        
        PRINT 'Đã thêm tài khoản nhân viên';
    END
END

-- 3. Tạo bảng EmployeeShift nếu chưa tồn tại
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'EmployeeShift')
BEGIN
    CREATE TABLE EmployeeShift (
        employeeShiftId INT IDENTITY(1,1) PRIMARY KEY,
        EmployeeID INT NOT NULL,
        ShiftID INT NOT NULL,
        dayOfWeek INT NOT NULL, -- 1: Thứ 2, 2: Thứ 3, ..., 7: Chủ nhật
        isActive BIT DEFAULT 1,
        CONSTRAINT FK_EmployeeShift_Employee FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID),
        CONSTRAINT FK_EmployeeShift_WorkShift FOREIGN KEY (ShiftID) REFERENCES WorkShift(shiftId),
        CONSTRAINT UQ_EmployeeShift UNIQUE (EmployeeID, ShiftID, dayOfWeek)
    );
    
    PRINT 'Đã tạo bảng EmployeeShift';
    
    -- Thêm data mẫu cho lịch làm việc (nhân viên 1 làm ca 1 vào thứ 2)
    DECLARE @nv1Id INT;
    SELECT @nv1Id = EmployeeID FROM Employee WHERE Username = 'nv1';
    
    IF @nv1Id IS NOT NULL
    BEGIN
        INSERT INTO EmployeeShift (EmployeeID, ShiftID, dayOfWeek, isActive)
        VALUES (@nv1Id, 1, 1, 1);
        
        PRINT 'Đã thêm mẫu lịch làm việc cho nhân viên';
    END
END
ELSE
BEGIN
    -- Kiểm tra cột isActive
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('EmployeeShift') AND name = 'isActive')
    BEGIN
        ALTER TABLE EmployeeShift ADD isActive BIT DEFAULT 1;
        PRINT 'Đã thêm cột isActive vào bảng EmployeeShift';
    END
    
    -- Kiểm tra ràng buộc UNIQUE
    IF NOT EXISTS (
        SELECT * FROM sys.key_constraints 
        WHERE parent_object_id = OBJECT_ID('EmployeeShift') 
        AND name LIKE 'UQ_EmployeeShift%'
    )
    BEGIN
        -- Xóa dữ liệu trùng lặp nếu có trước khi thêm ràng buộc
        WITH CTE AS (
            SELECT 
                ROW_NUMBER() OVER(PARTITION BY EmployeeID, ShiftID, dayOfWeek ORDER BY employeeShiftId) AS RowNum
            FROM 
                EmployeeShift
        )
        DELETE FROM CTE WHERE RowNum > 1;
        
        ALTER TABLE EmployeeShift 
        ADD CONSTRAINT UQ_EmployeeShift UNIQUE (EmployeeID, ShiftID, dayOfWeek);
        
        PRINT 'Đã thêm ràng buộc UNIQUE cho bảng EmployeeShift';
    END
END

-- 4. Kiểm tra bảng Computer nếu chưa tồn tại
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Computer')
BEGIN
    CREATE TABLE Computer (
        computerId INT IDENTITY(1,1) PRIMARY KEY,
        computerName NVARCHAR(50) NOT NULL,
        location NVARCHAR(50),
        status VARCHAR(20) DEFAULT 'AVAILABLE',
        lastMaintenanceDate DATETIME
    );
    
    PRINT 'Đã tạo bảng Computer';
    
    -- Thêm một số máy tính mẫu
    INSERT INTO Computer (computerName, location, status)
    VALUES
        (N'Máy 01', N'Khu A', 'AVAILABLE'),
        (N'Máy 02', N'Khu A', 'AVAILABLE'),
        (N'Máy 03', N'Khu A', 'AVAILABLE'),
        (N'Máy 04', N'Khu B', 'AVAILABLE'),
        (N'Máy 05', N'Khu B', 'AVAILABLE');
    
    PRINT 'Đã thêm dữ liệu mẫu cho bảng Computer';
END

-- 5. Kiểm tra và cập nhật bảng Member
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Member')
BEGIN
    CREATE TABLE Member (
        MemberID INT IDENTITY(1,1) PRIMARY KEY,
        Username VARCHAR(50) NOT NULL UNIQUE,
        Password VARCHAR(255) NOT NULL,
        FullName NVARCHAR(100) NOT NULL,
        Email VARCHAR(100),
        Phone VARCHAR(20),
        Balance DECIMAL(10, 2) DEFAULT 0.0,
        RemainingTime INT DEFAULT 0,
        Points INT DEFAULT 0,
        JoinDate DATETIME DEFAULT GETDATE()
    );
    
    PRINT 'Đã tạo bảng Member';
    
    -- Thêm tài khoản thành viên mẫu
    INSERT INTO Member (Username, Password, FullName, Email, Phone, Balance)
    VALUES ('member1', '123456', N'Thành viên 1', 'member1@example.com', '0912345678', 100000);
    
    PRINT 'Đã thêm dữ liệu cho bảng Member';
END
ELSE
BEGIN
    -- Kiểm tra và thêm các cột cần thiết nếu thiếu
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Member') AND name = 'Email')
    BEGIN
        ALTER TABLE Member ADD Email VARCHAR(100);
        PRINT 'Đã thêm cột Email vào bảng Member';
    END
    
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Member') AND name = 'Phone')
    BEGIN
        ALTER TABLE Member ADD Phone VARCHAR(20);
        PRINT 'Đã thêm cột Phone vào bảng Member';
    END
    
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Member') AND name = 'Balance')
    BEGIN
        ALTER TABLE Member ADD Balance DECIMAL(10, 2) DEFAULT 0.0;
        PRINT 'Đã thêm cột Balance vào bảng Member';
    END
    
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Member') AND name = 'RemainingTime')
    BEGIN
        ALTER TABLE Member ADD RemainingTime INT DEFAULT 0;
        PRINT 'Đã thêm cột RemainingTime vào bảng Member';
    END
    
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Member') AND name = 'Points')
    BEGIN
        ALTER TABLE Member ADD Points INT DEFAULT 0;
        PRINT 'Đã thêm cột Points vào bảng Member';
    END
    
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Member') AND name = 'JoinDate')
    BEGIN
        ALTER TABLE Member ADD JoinDate DATETIME DEFAULT GETDATE();
        PRINT 'Đã thêm cột JoinDate vào bảng Member';
    END
    
    -- Đảm bảo có ít nhất 1 tài khoản thành viên
    IF NOT EXISTS (SELECT * FROM Member WHERE Username = 'member1')
    BEGIN
        INSERT INTO Member (Username, Password, FullName, Balance)
        VALUES ('member1', '123456', N'Thành viên 1', 100000);
        
        -- Cập nhật thêm thông tin
        UPDATE Member 
        SET Email = 'member1@example.com', 
            Phone = '0912345678'
        WHERE Username = 'member1';
        
        PRINT 'Đã thêm tài khoản thành viên mẫu';
    END
    ELSE
    BEGIN
        -- Nếu đã có thành viên member1, cập nhật số dư và thông tin
        UPDATE Member 
        SET Balance = 100000,
            Email = CASE WHEN Email IS NULL THEN 'member1@example.com' ELSE Email END,
            Phone = CASE WHEN Phone IS NULL THEN '0912345678' ELSE Phone END
        WHERE Username = 'member1';
        
        PRINT 'Đã cập nhật thông tin thành viên';
    END
END

-- 6. Kiểm tra bảng Service
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Service')
BEGIN
    CREATE TABLE Service (
        serviceId INT IDENTITY(1,1) PRIMARY KEY,
        serviceName NVARCHAR(100) NOT NULL,
        price DECIMAL(10,2) NOT NULL,
        category NVARCHAR(50),
        stockQuantity INT DEFAULT 0
    );
    
    PRINT 'Đã tạo bảng Service';
    
    -- Thêm dịch vụ mẫu
    INSERT INTO Service (serviceName, price, category, stockQuantity)
    VALUES 
        (N'Mì tôm', 15000, N'Đồ ăn', 50),
        (N'Nước ngọt', 12000, N'Đồ uống', 100),
        (N'Cà phê', 20000, N'Đồ uống', 50),
        (N'Bánh mì', 15000, N'Đồ ăn', 30);
    
    PRINT 'Đã thêm dữ liệu mẫu cho bảng Service';
END

-- 7. Kiểm tra bảng ServiceOrder
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'ServiceOrder')
BEGIN
    CREATE TABLE ServiceOrder (
        id INT IDENTITY(1,1) PRIMARY KEY,
        MemberID INT NOT NULL,
        ServiceID INT NOT NULL,
        quantity INT NOT NULL,
        totalPrice DECIMAL(10,2) NOT NULL,
        orderDate DATETIME NOT NULL,
        status NVARCHAR(20) NOT NULL, -- PENDING, COMPLETED, CANCELLED
        note NVARCHAR(255),
        ProcessedByEmployeeID INT,
        CONSTRAINT FK_ServiceOrder_Member FOREIGN KEY (MemberID) REFERENCES Member(MemberID),
        CONSTRAINT FK_ServiceOrder_Service FOREIGN KEY (ServiceID) REFERENCES Service(serviceId),
        CONSTRAINT FK_ServiceOrder_Employee FOREIGN KEY (ProcessedByEmployeeID) REFERENCES Employee(EmployeeID)
    );
    
    PRINT 'Đã tạo bảng ServiceOrder';
END

-- 8. Kiểm tra bảng TopUpRequest
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'TopUpRequest')
BEGIN
    CREATE TABLE TopUpRequest (
        id INT IDENTITY(1,1) PRIMARY KEY,
        MemberID INT NOT NULL,
        amount DECIMAL(10,2) NOT NULL,
        requestDate DATETIME NOT NULL,
        status NVARCHAR(20) NOT NULL, -- PENDING, APPROVED, REJECTED
        ProcessedByEmployeeID INT,
        processedDate DATETIME,
        note NVARCHAR(255),
        CONSTRAINT FK_TopUpRequest_Member FOREIGN KEY (MemberID) REFERENCES Member(MemberID),
        CONSTRAINT FK_TopUpRequest_Employee FOREIGN KEY (ProcessedByEmployeeID) REFERENCES Employee(EmployeeID)
    );
    
    PRINT 'Đã tạo bảng TopUpRequest';
END

-- 9. Kiểm tra bảng ServiceTransaction (lưu lịch sử giao dịch dịch vụ)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'ServiceTransaction')
BEGIN
    CREATE TABLE ServiceTransaction (
        transactionId INT IDENTITY(1,1) PRIMARY KEY,
        serviceId INT NOT NULL,
        quantity INT NOT NULL,
        transactionDate DATETIME NOT NULL,
        transactionType NVARCHAR(20) NOT NULL, -- PURCHASE, SALE, ADJUSTMENT
        note NVARCHAR(255),
        CONSTRAINT FK_ServiceTransaction_Service FOREIGN KEY (serviceId) REFERENCES Service(serviceId)
    );
    
    PRINT 'Đã tạo bảng ServiceTransaction';
END

-- 10. Kiểm tra bảng ActivityLog
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'ActivityLog')
BEGIN
    CREATE TABLE ActivityLog (
        logId INT IDENTITY(1,1) PRIMARY KEY,
        activityType NVARCHAR(50) NOT NULL,
        description NVARCHAR(255) NOT NULL,
        userId INT,
        userType NVARCHAR(20),
        timestamp DATETIME DEFAULT GETDATE(),
        details NVARCHAR(MAX)
    );
    
    PRINT 'Đã tạo bảng ActivityLog';
END

PRINT 'Hoàn thành thiết lập cơ sở dữ liệu!'; 