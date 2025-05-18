-- Kiểm tra và tạo bảng WorkShift nếu chưa tồn tại
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'WorkShift')
BEGIN
    CREATE TABLE WorkShift (
        shiftId INT IDENTITY(1,1) PRIMARY KEY,
        shiftName NVARCHAR(50) NOT NULL,
        startTime TIME NOT NULL,
        endTime TIME NOT NULL,
        description NVARCHAR(255)
    );
    
    -- Thêm dữ liệu mẫu
    INSERT INTO WorkShift (shiftName, startTime, endTime, description)
    VALUES
        (N'Ca 1', '08:00:00', '12:00:00', N'Ca sáng'),
        (N'Ca 2', '13:00:00', '17:00:00', N'Ca chiều'),
        (N'Ca 3', '18:00:00', '22:00:00', N'Ca tối');
    
    PRINT 'Đã tạo và thêm dữ liệu cho bảng WorkShift';
END
ELSE
BEGIN
    -- Nếu bảng đã tồn tại, kiểm tra dữ liệu
    IF NOT EXISTS (SELECT * FROM WorkShift WHERE shiftId = 1)
    BEGIN
        -- Tìm và xóa dữ liệu trong bảng EmployeeShift trước (nếu có)
        IF EXISTS (SELECT * FROM sys.tables WHERE name = 'EmployeeShift')
        BEGIN
            -- Xóa dữ liệu trong bảng con (EmployeeShift) trước
            DELETE FROM EmployeeShift;
            PRINT 'Đã xóa dữ liệu từ bảng EmployeeShift';
        END
        
        -- Nếu không thể truncate, sử dụng xóa và thêm dữ liệu với ID cụ thể
        DELETE FROM WorkShift;
        
        -- Reset identity để bắt đầu từ 1
        DBCC CHECKIDENT ('WorkShift', RESEED, 0);
        
        -- Thêm dữ liệu mẫu
        INSERT INTO WorkShift (shiftName, startTime, endTime, description)
        VALUES
            (N'Ca 1', '08:00:00', '12:00:00', N'Ca sáng'),
            (N'Ca 2', '13:00:00', '17:00:00', N'Ca chiều'),
            (N'Ca 3', '18:00:00', '22:00:00', N'Ca tối');
        
        PRINT 'Đã cập nhật dữ liệu trong bảng WorkShift';
    END
    ELSE
    BEGIN
        -- Kiểm tra và thêm bổ sung nếu thiếu ca
        IF NOT EXISTS (SELECT * FROM WorkShift WHERE shiftId = 2)
        BEGIN
            INSERT INTO WorkShift (shiftName, startTime, endTime, description)
            VALUES (N'Ca 2', '13:00:00', '17:00:00', N'Ca chiều');
            
            PRINT 'Đã thêm Ca 2';
        END
        
        IF NOT EXISTS (SELECT * FROM WorkShift WHERE shiftId = 3)
        BEGIN
            INSERT INTO WorkShift (shiftName, startTime, endTime, description)
            VALUES (N'Ca 3', '18:00:00', '22:00:00', N'Ca tối');
            
            PRINT 'Đã thêm Ca 3';
        END
    END
END 