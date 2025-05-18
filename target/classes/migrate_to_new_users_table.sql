-- Xóa các ràng buộc UNIQUE trên bảng users
IF EXISTS (SELECT * FROM sys.indexes WHERE name = 'UQ__users__B43B145F8A74C1F2') 
    ALTER TABLE users DROP CONSTRAINT UQ__users__B43B145F8A74C1F2;

-- Kiểm tra các ràng buộc UNIQUE khác trên cột phone
DECLARE @constraintName NVARCHAR(128)
SELECT @constraintName = name FROM sys.indexes 
WHERE object_id = OBJECT_ID('users') AND is_unique = 1 
AND index_id IN (SELECT index_id FROM sys.index_columns 
                WHERE object_id = OBJECT_ID('users') 
                AND column_id = (SELECT column_id FROM sys.columns 
                              WHERE object_id = OBJECT_ID('users') AND name = 'phone'))

IF @constraintName IS NOT NULL
    EXEC('ALTER TABLE users DROP CONSTRAINT ' + @constraintName)

-- Cập nhật bảng users
TRUNCATE TABLE users;

-- Đảm bảo bảng roles có đủ dữ liệu
INSERT INTO roles (name, description) 
SELECT t.name, t.description
FROM (
    VALUES 
        ('ROLE_ADMIN', N'Quản trị viên'),
        ('ROLE_EMPLOYEE', N'Nhân viên'),
        ('ROLE_MEMBER', N'Thành viên')
) AS t(name, description)
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = t.name);

-- Thêm lại dữ liệu từ Employee vào users
INSERT INTO users (username, password, full_name, email, phone, created_at, role_id)
SELECT 
    e.Username,
    e.Password,
    e.FullName,
    CONCAT(e.Username, '@example.com') as email,
    CONCAT('09', ROW_NUMBER() OVER (ORDER BY e.EmployeeID), RIGHT(CONVERT(varchar(20), NEWID()), 8)) as phone,
    GETDATE() as created_at,
    (SELECT id FROM roles WHERE name = 
        CASE 
            WHEN e.Role = 'ADMIN' THEN 'ROLE_ADMIN'
            WHEN e.Role = 'EMPLOYEE' THEN 'ROLE_EMPLOYEE'
            ELSE CONCAT('ROLE_', e.Role)
        END
    ) as role_id
FROM Employee e;

-- Thêm dữ liệu từ Member vào users
INSERT INTO users (username, password, full_name, email, phone, account_balance, points, created_at, role_id)
SELECT 
    m.Username,
    m.Password,
    m.FullName,
    CONCAT(m.Username, '@example.com') as email,
    CONCAT('08', ROW_NUMBER() OVER (ORDER BY m.MemberID), RIGHT(CONVERT(varchar(20), NEWID()), 8)) as phone,
    m.Balance as account_balance,
    m.Points as points,
    COALESCE(m.CreatedDate, GETDATE()) as created_at,
    (SELECT id FROM roles WHERE name = 'ROLE_MEMBER') as role_id
FROM Member m
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = m.Username); 