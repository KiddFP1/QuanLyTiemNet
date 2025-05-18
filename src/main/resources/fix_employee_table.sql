-- Kiểm tra và tạo tài khoản admin nếu chưa có
IF NOT EXISTS (SELECT * FROM Employee WHERE Username = 'admin')
BEGIN
    INSERT INTO Employee (Username, Password, FullName, Role)
    VALUES ('admin', 'admin123', N'Quản trị viên', 'ROLE_ADMIN')
END
ELSE
BEGIN
    -- Cập nhật mật khẩu admin và role nếu đã tồn tại
    UPDATE Employee
    SET Password = 'admin123', Role = 'ROLE_ADMIN'
    WHERE Username = 'admin'
END

-- Kiểm tra và tạo tài khoản nv1 nếu chưa có
IF NOT EXISTS (SELECT * FROM Employee WHERE Username = 'nv1')
BEGIN
    INSERT INTO Employee (Username, Password, FullName, Role)
    VALUES ('nv1', '123456', N'Nhân viên 1', 'ROLE_EMPLOYEE')
END
ELSE
BEGIN
    -- Cập nhật mật khẩu nv1 và role nếu đã tồn tại
    UPDATE Employee
    SET Password = '123456', Role = 'ROLE_EMPLOYEE'
    WHERE Username = 'nv1'
END 