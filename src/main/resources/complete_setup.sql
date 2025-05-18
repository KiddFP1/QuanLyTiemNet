-- Xóa database hiện tại nếu có
USE master;
GO
IF EXISTS (SELECT name FROM sys.databases WHERE name = 'tiemnet')
BEGIN
    ALTER DATABASE tiemnet SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE tiemnet;
END
GO

-- Tạo database mới
CREATE DATABASE tiemnet;
GO

USE tiemnet;
GO

-- Tạo bảng Employee
CREATE TABLE Employee (
    EmployeeID INT IDENTITY(1,1) PRIMARY KEY,
    Username VARCHAR(50) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    FullName NVARCHAR(100) NOT NULL,
    Role VARCHAR(20) NOT NULL
);
GO

-- Tạo bảng Member
CREATE TABLE Member (
    MemberID INT IDENTITY(1,1) PRIMARY KEY,
    Username VARCHAR(50) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    FullName NVARCHAR(100) NOT NULL,
    Phone VARCHAR(20),
    Balance DECIMAL(10,2) DEFAULT 0,
    Points INT DEFAULT 0,
    CreatedDate DATETIME DEFAULT GETDATE()
);
GO

-- Tạo bảng Computer
CREATE TABLE Computer (
    ComputerId INT IDENTITY(1,1) PRIMARY KEY,
    ComputerName VARCHAR(50) NOT NULL,
    Status VARCHAR(20) DEFAULT 'Available',
    Location NVARCHAR(50)
);
GO

-- Tạo bảng Service
CREATE TABLE Service (
    ServiceId INT IDENTITY(1,1) PRIMARY KEY,
    ServiceName VARCHAR(100) NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    Category NVARCHAR(50),
    StockQuantity INT DEFAULT 0
);
GO

-- Tạo bảng Shift
CREATE TABLE Shift (
    ShiftId INT IDENTITY(1,1) PRIMARY KEY,
    EmployeeID INT NOT NULL,
    StartTime DATETIME NOT NULL,
    EndTime DATETIME,
    Note NVARCHAR(255),
    FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)
);
GO

-- Tạo bảng ComputerUsage
CREATE TABLE ComputerUsage (
    UsageId INT IDENTITY(1,1) PRIMARY KEY,
    ComputerId INT NOT NULL,
    MemberID INT NOT NULL,
    StartTime DATETIME NOT NULL,
    EndTime DATETIME,
    TotalAmount DECIMAL(10,2) DEFAULT 0,
    Status VARCHAR(20) DEFAULT 'ACTIVE',
    FOREIGN KEY (ComputerId) REFERENCES Computer(ComputerId),
    FOREIGN KEY (MemberID) REFERENCES Member(MemberID)
);
GO

-- Tạo bảng ServiceOrder
CREATE TABLE ServiceOrder (
    OrderId INT IDENTITY(1,1) PRIMARY KEY,
    MemberID INT NOT NULL,
    ServiceID INT NOT NULL,
    Quantity INT NOT NULL DEFAULT 1,
    TotalPrice DECIMAL(10,2) NOT NULL,
    OrderDate DATETIME DEFAULT GETDATE(),
    Status VARCHAR(20) DEFAULT 'PENDING',
    Note NVARCHAR(255),
    FOREIGN KEY (MemberID) REFERENCES Member(MemberID),
    FOREIGN KEY (ServiceID) REFERENCES Service(ServiceId)
);
GO

-- Tạo bảng TopUpRequest
CREATE TABLE TopUpRequest (
    RequestId INT IDENTITY(1,1) PRIMARY KEY,
    MemberID INT NOT NULL,
    Amount DECIMAL(10,2) NOT NULL,
    RequestDate DATETIME DEFAULT GETDATE(),
    ProcessedDate DATETIME,
    Status VARCHAR(20) DEFAULT 'PENDING',
    Note NVARCHAR(255),
    ProcessedByEmployeeID INT,
    FOREIGN KEY (MemberID) REFERENCES Member(MemberID),
    FOREIGN KEY (ProcessedByEmployeeID) REFERENCES Employee(EmployeeID)
);
GO

-- Thêm nhân viên
INSERT INTO Employee (Username, Password, FullName, Role)
VALUES 
('nv1', '123456', N'Nguyễn Văn A', 'EMPLOYEE'),
('admin', 'admin123', N'Admin', 'ADMIN');
GO

-- Thêm thành viên
INSERT INTO Member (Username, Password, FullName, Phone, Balance, Points)
VALUES 
('member1', '123456', N'Trần Văn B', '0123456789', 100000, 0),
('member2', '123456', N'Lê Thị C', '0987654321', 200000, 0);
GO

-- Thêm máy tính
INSERT INTO Computer (ComputerName, Status, Location)
VALUES 
('PC-01', 'Available', N'Khu A'),
('PC-02', 'Available', N'Khu A'),
('PC-03', 'Available', N'Khu B'),
('PC-04', 'Available', N'Khu B'),
('PC-05', 'Available', N'Khu C');
GO

-- Thêm dịch vụ
INSERT INTO Service (ServiceName, Price, Category, StockQuantity)
VALUES 
(N'Mì tôm', 15000, N'Đồ ăn', 100),
(N'Coca Cola', 10000, N'Nước uống', 200),
(N'Snack', 12000, N'Đồ ăn', 150),
(N'Cafe', 20000, N'Nước uống', 100);
GO

-- Thêm dữ liệu mẫu cho bảng ServiceOrder
INSERT INTO ServiceOrder (MemberID, ServiceID, Quantity, TotalPrice, OrderDate, Status, Note)
VALUES
(1, 1, 2, 30000, GETDATE(), 'COMPLETED', N'Đã hoàn thành'),
(2, 3, 1, 12000, GETDATE(), 'PENDING', N'Đang xử lý');
GO

-- Thêm dữ liệu mẫu cho bảng TopUpRequest
INSERT INTO TopUpRequest (MemberID, Amount, RequestDate, Status, Note)
VALUES
(1, 100000, GETDATE(), 'APPROVED', N'Đã duyệt'),
(2, 50000, GETDATE(), 'PENDING', N'Đang chờ duyệt');
GO

-- Thêm dữ liệu mẫu cho bảng Shift
INSERT INTO Shift (EmployeeID, StartTime, EndTime, Note)
VALUES
(1, DATEADD(HOUR, -4, GETDATE()), NULL, N'Ca hiện tại');
GO

-- Thêm dữ liệu mẫu cho bảng ComputerUsage
INSERT INTO ComputerUsage (ComputerId, MemberID, StartTime, EndTime, TotalAmount, Status)
VALUES
(1, 1, DATEADD(HOUR, -2, GETDATE()), NULL, 0, 'ACTIVE'),
(2, 2, DATEADD(HOUR, -1, GETDATE()), GETDATE(), 15000, 'COMPLETED');
GO

-- Hiển thị dữ liệu đã thêm
SELECT * FROM Employee;
SELECT * FROM Member;
SELECT * FROM Computer;
SELECT * FROM Service;
SELECT * FROM ServiceOrder;
SELECT * FROM TopUpRequest;
SELECT * FROM Shift;
SELECT * FROM ComputerUsage;
GO 