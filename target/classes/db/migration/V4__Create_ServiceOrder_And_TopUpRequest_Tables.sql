-- Tạo bảng ServiceOrder
CREATE TABLE ServiceOrder (
    id INT IDENTITY(1,1) PRIMARY KEY,
    MemberID INT NOT NULL,
    ServiceID INT NOT NULL,
    quantity INT NOT NULL,
    totalPrice DECIMAL(10,2) NOT NULL,
    orderDate DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    note NVARCHAR(500),
    ProcessedByEmployeeID INT,
    FOREIGN KEY (MemberID) REFERENCES Member(MemberID),
    FOREIGN KEY (ServiceID) REFERENCES Service(ServiceID),
    FOREIGN KEY (ProcessedByEmployeeID) REFERENCES Employee(EmployeeID)
);

-- Tạo bảng TopUpRequest
CREATE TABLE TopUpRequest (
    id INT IDENTITY(1,1) PRIMARY KEY,
    MemberID INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    requestDate DATETIME NOT NULL,
    status VARCHAR(20) NOT NULL,
    note NVARCHAR(500),
    processedDate DATETIME,
    ProcessedByEmployeeID INT,
    FOREIGN KEY (MemberID) REFERENCES Member(MemberID),
    FOREIGN KEY (ProcessedByEmployeeID) REFERENCES Employee(EmployeeID)
); 