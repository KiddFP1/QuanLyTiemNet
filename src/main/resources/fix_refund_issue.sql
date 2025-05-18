-- Tạo stored procedure để tự động hoàn tiền khi đơn hàng bị hủy
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'CancelOrder')
    DROP PROCEDURE CancelOrder
GO

CREATE PROCEDURE CancelOrder
    @OrderID INT,
    @EmployeeID INT,
    @Reason NVARCHAR(255) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    BEGIN TRANSACTION;
    
    DECLARE @MemberID INT;
    DECLARE @Amount DECIMAL(10, 2);
    DECLARE @Status NVARCHAR(20);
    
    -- Lấy thông tin đơn hàng
    SELECT 
        @MemberID = MemberID,
        @Amount = totalPrice,
        @Status = status
    FROM 
        ServiceOrder 
    WHERE 
        id = @OrderID;
    
    -- Kiểm tra xem đơn hàng có tồn tại không
    IF @MemberID IS NULL
    BEGIN
        ROLLBACK;
        RAISERROR('Đơn hàng không tồn tại!', 16, 1);
        RETURN;
    END
    
    -- Kiểm tra trạng thái đơn hàng
    IF @Status = 'CANCELLED'
    BEGIN
        ROLLBACK;
        RAISERROR('Đơn hàng đã bị hủy trước đó!', 16, 1);
        RETURN;
    END
    
    IF @Status = 'COMPLETED'
    BEGIN
        ROLLBACK;
        RAISERROR('Không thể hủy đơn hàng đã hoàn thành!', 16, 1);
        RETURN;
    END
    
    -- Cập nhật đơn hàng
    UPDATE ServiceOrder
    SET 
        status = 'CANCELLED',
        ProcessedByEmployeeID = @EmployeeID,
        note = CASE WHEN @Reason IS NULL THEN note ELSE @Reason END
    WHERE 
        id = @OrderID;
    
    -- Hoàn tiền cho thành viên
    UPDATE Member
    SET 
        Balance = Balance + @Amount
    WHERE 
        MemberID = @MemberID;
    
    -- Ghi log
    INSERT INTO ActivityLog (activityType, description, userId, userType, timestamp, details)
    VALUES (
        'ORDER_CANCELLED', 
        'Đơn hàng ' + CAST(@OrderID AS NVARCHAR) + ' đã bị hủy và hoàn tiền ' + CAST(@Amount AS NVARCHAR), 
        @EmployeeID, 
        'EMPLOYEE', 
        GETDATE(), 
        'OrderID: ' + CAST(@OrderID AS NVARCHAR) + ', MemberID: ' + CAST(@MemberID AS NVARCHAR) + ', Amount: ' + CAST(@Amount AS NVARCHAR)
    );
    
    COMMIT;
END
GO

-- Tạo bảng ActivityLog nếu chưa tồn tại để ghi log các hoạt động
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