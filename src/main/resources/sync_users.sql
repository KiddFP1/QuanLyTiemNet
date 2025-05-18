-- Đồng bộ dữ liệu từ Employee sang users
MERGE INTO users AS target
USING (
    SELECT 
        Username, 
        Password, 
        FullName, 
        EmployeeID,
        'admin@example.com' AS Email,
        CONCAT('09', EmployeeID, '9876') AS Phone,  -- Tạo số điện thoại duy nhất theo ID
        GETDATE() AS CreatedDate
    FROM Employee
) AS source
ON (target.username = source.Username)
WHEN NOT MATCHED THEN
    INSERT (username, password, full_name, email, phone, created_at, role_id)
    VALUES (
        source.Username, 
        source.Password, 
        source.FullName, 
        source.Email,
        source.Phone,
        source.CreatedDate,
        CASE 
            WHEN source.Username = 'admin' THEN 1 -- ROLE_ADMIN
            ELSE 2 -- ROLE_EMPLOYEE
        END
    );
    
-- Đồng bộ dữ liệu từ Member sang users
MERGE INTO users AS target
USING (
    SELECT 
        Username, 
        Password, 
        FullName,
        CASE
            WHEN Phone IS NULL THEN CONCAT('08', MemberID, '5432')  -- Nếu Phone NULL tạo số mới
            ELSE Phone  -- Ngược lại giữ nguyên phone
        END AS Phone,
        Balance AS account_balance,
        Points AS points,
        CreatedDate
    FROM Member
) AS source
ON (target.username = source.Username)
WHEN NOT MATCHED THEN
    INSERT (username, password, full_name, phone, account_balance, points, created_at, role_id)
    VALUES (
        source.Username, 
        source.Password, 
        source.FullName,
        source.Phone,
        source.account_balance,
        source.points,
        source.CreatedDate,
        3 -- ROLE_MEMBER
    ); 