-- Kiểm tra nếu bảng ServiceOrder tồn tại
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'ServiceOrder')
BEGIN
    -- Kiểm tra xem bảng đã có cột id chưa
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('ServiceOrder') AND name = 'id')
    BEGIN
        -- Kiểm tra xem đã có cột nào là primary key chưa
        DECLARE @pk_name NVARCHAR(128);
        SELECT @pk_name = name 
        FROM sys.indexes 
        WHERE object_id = OBJECT_ID('ServiceOrder') AND is_primary_key = 1;

        -- Nếu đã có primary key, xóa đi trước
        IF @pk_name IS NOT NULL
        BEGIN
            DECLARE @sql NVARCHAR(MAX);
            SET @sql = 'ALTER TABLE ServiceOrder DROP CONSTRAINT ' + @pk_name;
            EXEC sp_executesql @sql;
        END

        -- Kiểm tra các cột IDENTITY hiện có
        DECLARE @identity_column NVARCHAR(128) = NULL;
        SELECT @identity_column = name
        FROM sys.columns
        WHERE object_id = OBJECT_ID('ServiceOrder')
        AND is_identity = 1;

        -- Trường hợp đã có cột IDENTITY
        IF @identity_column IS NOT NULL
        BEGIN
            -- Nếu cột identity không phải là ServiceOrderID, thì đổi tên nó thành id
            IF @identity_column <> 'id' AND @identity_column <> 'ServiceOrderID'
            BEGIN
                DECLARE @rename_sql NVARCHAR(MAX);
                SET @rename_sql = 'ServiceOrder.' + @identity_column;
                EXEC sp_rename @rename_sql, 'id', 'COLUMN';
                
                -- Thêm khóa chính mới
                ALTER TABLE ServiceOrder ADD CONSTRAINT PK_ServiceOrder PRIMARY KEY (id);
            END
            -- Nếu cột identity là ServiceOrderID, đổi tên nó thành id
            ELSE IF @identity_column = 'ServiceOrderID'
            BEGIN
                EXEC sp_rename 'ServiceOrder.ServiceOrderID', 'id', 'COLUMN';
                
                -- Thêm khóa chính mới
                ALTER TABLE ServiceOrder ADD CONSTRAINT PK_ServiceOrder PRIMARY KEY (id);
            END
        END
        ELSE
        BEGIN
            -- Nếu không có cột IDENTITY nào, thêm cột id mới
            ALTER TABLE ServiceOrder ADD id INT IDENTITY(1,1) PRIMARY KEY;
        END
    END
END
GO

-- Tương tự cho bảng TopUpRequest
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'TopUpRequest')
BEGIN
    -- Kiểm tra xem bảng đã có cột id chưa
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('TopUpRequest') AND name = 'id')
    BEGIN
        -- Kiểm tra xem đã có cột nào là primary key chưa
        DECLARE @pk_name NVARCHAR(128);
        SELECT @pk_name = name 
        FROM sys.indexes 
        WHERE object_id = OBJECT_ID('TopUpRequest') AND is_primary_key = 1;

        -- Nếu đã có primary key, xóa đi trước
        IF @pk_name IS NOT NULL
        BEGIN
            DECLARE @sql NVARCHAR(MAX);
            SET @sql = 'ALTER TABLE TopUpRequest DROP CONSTRAINT ' + @pk_name;
            EXEC sp_executesql @sql;
        END

        -- Kiểm tra các cột IDENTITY hiện có
        DECLARE @identity_column NVARCHAR(128) = NULL;
        SELECT @identity_column = name
        FROM sys.columns
        WHERE object_id = OBJECT_ID('TopUpRequest')
        AND is_identity = 1;

        -- Trường hợp đã có cột IDENTITY
        IF @identity_column IS NOT NULL
        BEGIN
            -- Nếu cột identity không phải là id hoặc RequestID, thì đổi tên nó thành id
            IF @identity_column <> 'id' AND @identity_column <> 'RequestID'
            BEGIN
                DECLARE @rename_sql NVARCHAR(MAX);
                SET @rename_sql = 'TopUpRequest.' + @identity_column;
                EXEC sp_rename @rename_sql, 'id', 'COLUMN';
                
                -- Thêm khóa chính mới
                ALTER TABLE TopUpRequest ADD CONSTRAINT PK_TopUpRequest PRIMARY KEY (id);
            END
            -- Nếu cột identity là RequestID, đổi tên nó thành id
            ELSE IF @identity_column = 'RequestID'
            BEGIN
                EXEC sp_rename 'TopUpRequest.RequestID', 'id', 'COLUMN';
                
                -- Thêm khóa chính mới
                ALTER TABLE TopUpRequest ADD CONSTRAINT PK_TopUpRequest PRIMARY KEY (id);
            END
        END
        ELSE
        BEGIN
            -- Nếu không có cột IDENTITY nào, thêm cột id mới
            ALTER TABLE TopUpRequest ADD id INT IDENTITY(1,1) PRIMARY KEY;
        END
    END
END

sp_help 'ServiceOrder'
sp_help 'TopUpRequest' 