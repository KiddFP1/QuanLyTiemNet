-- Kiểm tra bảng TopUpRequest và thêm cột id nếu cần
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'TopUpRequest') 
BEGIN
    -- Kiểm tra nếu cột 'id' chưa tồn tại trong bảng TopUpRequest
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('TopUpRequest') AND name = 'id')
    BEGIN
        -- Kiểm tra xem đã có cột nào là primary key chưa
        DECLARE @pk_name NVARCHAR(128);
        SELECT @pk_name = i.name
        FROM sys.indexes i
        JOIN sys.index_columns ic ON i.object_id = ic.object_id AND i.index_id = ic.index_id
        WHERE i.is_primary_key = 1 AND i.object_id = OBJECT_ID('TopUpRequest');
        
        -- Nếu đã tồn tại primary key, xóa đi trước
        IF @pk_name IS NOT NULL
        BEGIN
            DECLARE @sql NVARCHAR(MAX) = 'ALTER TABLE TopUpRequest DROP CONSTRAINT ' + @pk_name;
            EXEC sp_executesql @sql;
        END
        
        -- Kiểm tra xem cột requestId có tồn tại không
        IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('TopUpRequest') AND name = 'requestId')
        BEGIN
            -- Đổi tên cột requestId thành id
            EXEC sp_rename 'TopUpRequest.requestId', 'id', 'COLUMN';
            
            -- Thêm lại primary key constraint
            ALTER TABLE TopUpRequest ADD CONSTRAINT PK_TopUpRequest PRIMARY KEY (id);
        END
        ELSE
        BEGIN
            -- Thêm cột id mới
            ALTER TABLE TopUpRequest ADD id INT IDENTITY(1,1);
            
            -- Thêm primary key constraint
            ALTER TABLE TopUpRequest ADD CONSTRAINT PK_TopUpRequest PRIMARY KEY (id);
        END
    END
END
GO 