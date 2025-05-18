-- Create computer_usage table
CREATE TABLE computer_usage (
    usage_id INT IDENTITY(1,1) PRIMARY KEY,
    computer_id INT,
    member_id INT,
    start_time DATETIME,
    end_time DATETIME,
    total_amount DECIMAL(10,2),
    FOREIGN KEY (computer_id) REFERENCES Computer(ComputerId),
    FOREIGN KEY (member_id) REFERENCES Member(MemberID)
);

-- Create TopUpRequest table
CREATE TABLE TopUpRequest (
    id INT IDENTITY(1,1) PRIMARY KEY,
    MemberID INT,
    amount DECIMAL(10,2),
    requestDate DATETIME,
    processedDate DATETIME,
    status VARCHAR(20),
    note VARCHAR(255),
    ProcessedByEmployeeID INT,
    FOREIGN KEY (MemberID) REFERENCES Member(MemberID),
    FOREIGN KEY (ProcessedByEmployeeID) REFERENCES Employee(EmployeeID)
);

-- Create TopUpHistory table
CREATE TABLE TopUpHistory (
    TopUpID INT IDENTITY(1,1) PRIMARY KEY,
    MemberID INT,
    Amount DECIMAL(10,2),
    TopUpDate DATETIME,
    Note VARCHAR(255),
    FOREIGN KEY (MemberID) REFERENCES Member(MemberID)
); 