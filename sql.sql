use tiemnet

-- Quản lý máy tính
CREATE TABLE Computer (
    ComputerID INT PRIMARY KEY IDENTITY(1,1),
    ComputerName NVARCHAR(50),
    Status NVARCHAR(20), -- Available/InUse/Maintenance
    Location NVARCHAR(50),
    LastMaintenanceDate DATETIME
);

-- Quản lý thành viên
CREATE TABLE Member (
    MemberID INT PRIMARY KEY IDENTITY(1,1),
    Username NVARCHAR(50) UNIQUE,
    Password NVARCHAR(100),
    FullName NVARCHAR(100),
    Phone NVARCHAR(15),
    Balance DECIMAL(10,2),
    Points INT DEFAULT 0,
    CreatedDate DATETIME DEFAULT GETDATE()
);

-- Gói dịch vụ
CREATE TABLE ServicePackage (
    PackageID INT PRIMARY KEY IDENTITY(1,1),
    PackageName NVARCHAR(50),
    PricePerHour DECIMAL(10,2),
    Description NVARCHAR(200)
);

-- Dịch vụ đi kèm (đồ ăn, nước, thẻ game...)
CREATE TABLE Service (
    ServiceID INT PRIMARY KEY IDENTITY(1,1),
    ServiceName NVARCHAR(100),
    Price DECIMAL(10,2),
    Category NVARCHAR(50), -- Food/Drink/GameCard/Printing
    StockQuantity INT
);

-- Lịch sử sử dụng máy
CREATE TABLE ComputerUsage (
    UsageID INT PRIMARY KEY IDENTITY(1,1),
    ComputerID INT FOREIGN KEY REFERENCES Computer(ComputerID),
    MemberID INT FOREIGN KEY REFERENCES Member(MemberID),
    StartTime DATETIME,
    EndTime DATETIME,
    TotalAmount DECIMAL(10,2)
);

-- Giao dịch dịch vụ
CREATE TABLE ServiceTransaction (
    TransactionID INT PRIMARY KEY IDENTITY(1,1),
    MemberID INT FOREIGN KEY REFERENCES Member(MemberID),
    ServiceID INT FOREIGN KEY REFERENCES Service(ServiceID),
    Quantity INT,
    TotalAmount DECIMAL(10,2),
    TransactionDate DATETIME DEFAULT GETDATE()
);

-- Nhân viên
CREATE TABLE Employee (
    EmployeeID INT PRIMARY KEY IDENTITY(1,1),
    Username NVARCHAR(50) UNIQUE,
    Password NVARCHAR(100),
    FullName NVARCHAR(100),
    Role NVARCHAR(20) -- Admin/Staff
);

-- Ca làm việc
CREATE TABLE Shift (
    ShiftID INT PRIMARY KEY IDENTITY(1,1),
    EmployeeID INT FOREIGN KEY REFERENCES Employee(EmployeeID),
    StartTime DATETIME,
    EndTime DATETIME,
    Status NVARCHAR(20) -- Scheduled/Completed/Cancelled
);

-- Chi phí vận hành
CREATE TABLE Expense (
    ExpenseID INT PRIMARY KEY IDENTITY(1,1),
    ExpenseType NVARCHAR(50), -- Electricity/Water/Internet/Salary
    Amount DECIMAL(10,2),
    ExpenseDate DATETIME,
    Description NVARCHAR(200)
);