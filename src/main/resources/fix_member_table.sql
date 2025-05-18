-- Kiểm tra và thêm cột Email vào bảng Member nếu chưa tồn tại
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Member')
BEGIN
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Member') AND name = 'Email')
    BEGIN
        ALTER TABLE Member ADD Email VARCHAR(100);
        PRINT 'Đã thêm cột Email vào bảng Member';
    END
    
    -- Kiểm tra các cột khác
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
    
    -- Cập nhật data vào các cột mới (nếu cần)
    -- Tách phần cập nhật thành batch riêng để SQL Server nhận diện các cột mới
    EXEC('
        UPDATE Member 
        SET Email = ''member1@example.com'',
            Phone = ''0912345678'',
            Balance = 100000
        WHERE Username = ''member1''
    ');
    
    PRINT 'Đã cập nhật dữ liệu cho bảng Member';
END 