-- Sửa lỗi bảng ServiceOrder
-- Kiểm tra xem đã có cột id chưa
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('ServiceOrder') AND name = 'id')
BEGIN
    -- Thêm cột id mới, không phải IDENTITY
    ALTER TABLE ServiceOrder ADD id INT;
    
    -- Cập nhật giá trị cho cột id nếu có ServiceOrderID
    IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('ServiceOrder') AND name = 'ServiceOrderID')
    BEGIN
        EXEC('UPDATE ServiceOrder SET id = ServiceOrderID');
    END
    ELSE
    BEGIN
        -- Nếu không có ServiceOrderID, tạo id tuần tự
        DECLARE @counter INT = 1;
        DECLARE @rowcount INT = 0;
        
        SELECT @rowcount = COUNT(*) FROM ServiceOrder;
        
        IF @rowcount > 0
        BEGIN
            EXEC('
                DECLARE @i INT = 1;
                DECLARE curs CURSOR FOR SELECT ROW_NUMBER() OVER(ORDER BY (SELECT NULL)) FROM ServiceOrder;
                OPEN curs;
                DECLARE @rn INT;
                FETCH NEXT FROM curs INTO @rn;
                
                WHILE @@FETCH_STATUS = 0
                BEGIN
                    UPDATE ServiceOrder 
                    SET id = @i 
                    WHERE CURRENT OF curs;
                    
                    SET @i = @i + 1;
                    FETCH NEXT FROM curs INTO @rn;
                END;
                
                CLOSE curs;
                DEALLOCATE curs;
            ');
        END
    END
    
    -- Thêm primary key nếu không có rằng buộc nào
    IF NOT EXISTS (SELECT * FROM sys.key_constraints WHERE parent_object_id = OBJECT_ID('ServiceOrder') AND type = 'PK')
    BEGIN
        ALTER TABLE ServiceOrder ADD CONSTRAINT PK_ServiceOrder PRIMARY KEY (id);
    END
END

-- Sửa lỗi bảng TopUpRequest
-- Kiểm tra xem đã có cột id chưa
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('TopUpRequest') AND name = 'id')
BEGIN
    -- Thêm cột id mới, không phải IDENTITY
    ALTER TABLE TopUpRequest ADD id INT;
    
    -- Cập nhật giá trị cho cột id nếu có RequestID
    IF EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('TopUpRequest') AND name = 'RequestID')
    BEGIN
        EXEC('UPDATE TopUpRequest SET id = RequestID');
    END
    ELSE
    BEGIN
        -- Nếu không có RequestID, tạo id tuần tự
        DECLARE @counter INT = 1;
        DECLARE @rowcount INT = 0;
        
        SELECT @rowcount = COUNT(*) FROM TopUpRequest;
        
        IF @rowcount > 0
        BEGIN
            EXEC('
                DECLARE @i INT = 1;
                DECLARE curs CURSOR FOR SELECT ROW_NUMBER() OVER(ORDER BY (SELECT NULL)) FROM TopUpRequest;
                OPEN curs;
                DECLARE @rn INT;
                FETCH NEXT FROM curs INTO @rn;
                
                WHILE @@FETCH_STATUS = 0
                BEGIN
                    UPDATE TopUpRequest 
                    SET id = @i 
                    WHERE CURRENT OF curs;
                    
                    SET @i = @i + 1;
                    FETCH NEXT FROM curs INTO @rn;
                END;
                
                CLOSE curs;
                DEALLOCATE curs;
            ');
        END
    END
    
    -- Thêm primary key nếu không có rằng buộc nào
    IF NOT EXISTS (SELECT * FROM sys.key_constraints WHERE parent_object_id = OBJECT_ID('TopUpRequest') AND type = 'PK')
    BEGIN
        ALTER TABLE TopUpRequest ADD CONSTRAINT PK_TopUpRequest PRIMARY KEY (id);
    END
END 