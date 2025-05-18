-- Kiểm tra và tạo bảng Role nếu chưa tồn tại
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'roles')
BEGIN
    CREATE TABLE roles (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name VARCHAR(50) NOT NULL UNIQUE,
        description VARCHAR(255)
    );
    
    -- Thêm dữ liệu mẫu cho Role
    INSERT INTO roles (name, description)
    VALUES 
    ('ROLE_ADMIN', 'Quản trị viên'),
    ('ROLE_EMPLOYEE', 'Nhân viên'),
    ('ROLE_MEMBER', 'Thành viên');
END
ELSE
BEGIN
    -- Nếu bảng đã tồn tại, kiểm tra và thêm dữ liệu nếu chưa có
    IF NOT EXISTS (SELECT * FROM roles WHERE name = 'ROLE_ADMIN')
        INSERT INTO roles (name, description) VALUES ('ROLE_ADMIN', 'Quản trị viên');
    
    IF NOT EXISTS (SELECT * FROM roles WHERE name = 'ROLE_EMPLOYEE')
        INSERT INTO roles (name, description) VALUES ('ROLE_EMPLOYEE', 'Nhân viên');
        
    IF NOT EXISTS (SELECT * FROM roles WHERE name = 'ROLE_MEMBER')
        INSERT INTO roles (name, description) VALUES ('ROLE_MEMBER', 'Thành viên');
END
GO

-- Kiểm tra và tạo bảng User nếu chưa tồn tại
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'users')
BEGIN
    CREATE TABLE users (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        username VARCHAR(50) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL,
        full_name VARCHAR(100) NOT NULL,
        email VARCHAR(100) UNIQUE,
        phone VARCHAR(20) UNIQUE,
        account_balance FLOAT DEFAULT 0,
        remaining_time INT DEFAULT 0,
        points INT DEFAULT 0,
        created_at DATETIME NOT NULL DEFAULT GETDATE(),
        last_login DATETIME,
        is_active BIT DEFAULT 1,
        role_id BIGINT,
        FOREIGN KEY (role_id) REFERENCES roles(id)
    );
END
GO

-- Kiểm tra và thêm user nếu chưa tồn tại
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'users')
BEGIN
    -- Kiểm tra admin
    IF NOT EXISTS (SELECT * FROM users WHERE username = 'admin')
    BEGIN
        DECLARE @adminRoleId BIGINT;
        SELECT @adminRoleId = id FROM roles WHERE name = 'ROLE_ADMIN';
        
        INSERT INTO users (username, password, full_name, email, phone, created_at, role_id)
        VALUES ('admin', 'admin123', 'Admin', 'admin@example.com', '0123456789', GETDATE(), @adminRoleId);
    END

    -- Kiểm tra nv1
    IF NOT EXISTS (SELECT * FROM users WHERE username = 'nv1')
    BEGIN
        DECLARE @employeeRoleId BIGINT;
        SELECT @employeeRoleId = id FROM roles WHERE name = 'ROLE_EMPLOYEE';
        
        INSERT INTO users (username, password, full_name, email, phone, created_at, role_id)
        VALUES ('nv1', '123456', N'Nguyễn Văn A', 'nv1@example.com', '0987654321', GETDATE(), @employeeRoleId);
    END
END
GO 