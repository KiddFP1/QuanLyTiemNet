-- Kiểm tra và tạo bảng EmployeeShift nếu chưa tồn tại
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
END
ELSE
BEGIN
    -- Kiểm tra xem cột isActive có tồn tại không
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('EmployeeShift') AND name = 'isActive')
    BEGIN
        -- Thêm cột isActive nếu chưa có
        ALTER TABLE EmployeeShift ADD isActive BIT DEFAULT 1;
        PRINT 'Đã thêm cột isActive vào bảng EmployeeShift';
    END
    
    -- Kiểm tra ràng buộc UNIQUE nếu chưa có
    IF NOT EXISTS (
        SELECT * FROM sys.key_constraints 
        WHERE parent_object_id = OBJECT_ID('EmployeeShift') 
        AND name LIKE 'UQ_EmployeeShift%'
    )
    BEGIN
        -- Thêm ràng buộc UNIQUE
        ALTER TABLE EmployeeShift 
        ADD CONSTRAINT UQ_EmployeeShift UNIQUE (EmployeeID, ShiftID, dayOfWeek);
        
        PRINT 'Đã thêm ràng buộc UNIQUE cho bảng EmployeeShift';
    END
END 