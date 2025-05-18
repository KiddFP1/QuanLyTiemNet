-- Hiển thị tất cả các bảng trong database
SELECT 
    t.name AS TableName,
    p.rows AS RowCounts
FROM 
    sys.tables t
INNER JOIN 
    sys.indexes i ON t.object_id = i.object_id
INNER JOIN 
    sys.partitions p ON i.object_id = p.object_id AND i.index_id = p.index_id
WHERE 
    i.index_id < 2  -- Chỉ đếm các hàng trong clustered index
    AND i.is_primary_key = 1
ORDER BY 
    t.name;

-- Hiển thị các cột của bảng WorkShift
PRINT 'Cấu trúc bảng WorkShift:';
EXEC sp_columns 'WorkShift';

-- Hiển thị các cột của bảng EmployeeShift
PRINT 'Cấu trúc bảng EmployeeShift:';
EXEC sp_columns 'EmployeeShift';

-- Hiển thị các cột của bảng Employee
PRINT 'Cấu trúc bảng Employee:';
EXEC sp_columns 'Employee'; 