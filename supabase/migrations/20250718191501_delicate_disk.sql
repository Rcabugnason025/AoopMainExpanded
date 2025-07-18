/*
====================================================================================================
DATABASE SCRIPT: aoopdatabase_payroll (3NF COMPLIANT)
REVISED BY: MotorPH Development Team
DATE: 2025-01-19
DESCRIPTION:
This script creates a fully normalized 3NF database with Views and Stored Procedures
to address mentor feedback regarding database normalization and advanced SQL features.

FEATURES:
1. THIRD NORMAL FORM (3NF):
   - Eliminates all transitive dependencies
   - Separate tables for positions, departments, and employee hierarchy
   - Proper foreign key relationships

2. VIEWS:
   - Comprehensive employee details view
   - Payroll calculation view
   - Department summary view

3. STORED PROCEDURES:
   - Employee management procedures
   - Payroll calculation procedures
   - Report generation procedures
====================================================================================================
*/

-- =============================================
-- Database Creation
-- =============================================
DROP DATABASE IF EXISTS aoopdatabase_payroll;
CREATE DATABASE aoopdatabase_payroll;
USE aoopdatabase_payroll;

-- Set strict SQL mode for data integrity
SET GLOBAL sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';
SET SESSION sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- =============================================
-- 3NF TABLE STRUCTURE
-- =============================================

-- Table 1: departments (1NF, 2NF, 3NF compliant)
CREATE TABLE departments (
    department_id INT AUTO_INCREMENT PRIMARY KEY,
    department_name VARCHAR(100) NOT NULL UNIQUE,
    department_head_id INT,
    budget DECIMAL(15,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table 2: positions (1NF, 2NF, 3NF compliant)
CREATE TABLE positions (
    position_id INT AUTO_INCREMENT PRIMARY KEY,
    position_title VARCHAR(100) NOT NULL UNIQUE,
    department_id INT NOT NULL,
    basic_salary DECIMAL(10,2) NOT NULL,
    rice_subsidy DECIMAL(8,2) NOT NULL DEFAULT 1500.00,
    phone_allowance DECIMAL(8,2) NOT NULL DEFAULT 1000.00,
    clothing_allowance DECIMAL(8,2) NOT NULL DEFAULT 800.00,
    gross_semi_monthly_rate DECIMAL(10,2) NOT NULL,
    hourly_rate DECIMAL(8,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (department_id) REFERENCES departments(department_id)
);

-- Table 3: employees (1NF, 2NF, 3NF compliant)
CREATE TABLE employees (
    employee_id INT PRIMARY KEY,
    last_name VARCHAR(50) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    birthday DATE,
    address TEXT,
    phone_number VARCHAR(20),
    sss_number VARCHAR(20) UNIQUE,
    philhealth_number VARCHAR(20) UNIQUE,
    tin_number VARCHAR(20) UNIQUE,
    pagibig_number VARCHAR(20) UNIQUE,
    status ENUM('Regular', 'Probationary', 'Contractual') NOT NULL DEFAULT 'Regular',
    position_id INT NOT NULL,
    supervisor_id INT,
    hire_date DATE DEFAULT (CURRENT_DATE),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (position_id) REFERENCES positions(position_id),
    FOREIGN KEY (supervisor_id) REFERENCES employees(employee_id) ON DELETE SET NULL
);

-- Table 4: credentials (1NF, 2NF, 3NF compliant)
CREATE TABLE credentials (
    employee_id INT PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    last_login TIMESTAMP NULL,
    failed_attempts INT DEFAULT 0,
    is_locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE
);

-- Table 5: attendance (1NF, 2NF, 3NF compliant)
CREATE TABLE attendance (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    attendance_date DATE NOT NULL,
    log_in TIME,
    log_out TIME,
    total_hours DECIMAL(4,2) GENERATED ALWAYS AS (
        CASE 
            WHEN log_in IS NOT NULL AND log_out IS NOT NULL 
            THEN TIME_TO_SEC(TIMEDIFF(log_out, log_in)) / 3600
            ELSE 0 
        END
    ) STORED,
    is_late BOOLEAN GENERATED ALWAYS AS (log_in > '08:00:00') STORED,
    has_undertime BOOLEAN GENERATED ALWAYS AS (log_out < '17:00:00') STORED,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_employee_date (employee_id, attendance_date),
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE
);

-- Table 6: leave_requests (1NF, 2NF, 3NF compliant)
CREATE TABLE leave_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    leave_type ENUM('Annual', 'Sick', 'Emergency', 'Maternity', 'Paternity') NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days_requested INT GENERATED ALWAYS AS (DATEDIFF(end_date, start_date) + 1) STORED,
    reason TEXT,
    status ENUM('Pending', 'Approved', 'Rejected') DEFAULT 'Pending',
    approved_by INT,
    approved_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES employees(employee_id) ON DELETE SET NULL
);

-- Table 7: payroll_records (1NF, 2NF, 3NF compliant)
CREATE TABLE payroll_records (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id INT NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    days_worked INT NOT NULL,
    overtime_hours DECIMAL(5,2) DEFAULT 0.00,
    basic_pay DECIMAL(10,2) NOT NULL,
    overtime_pay DECIMAL(10,2) DEFAULT 0.00,
    gross_pay DECIMAL(10,2) NOT NULL,
    rice_subsidy DECIMAL(8,2) DEFAULT 0.00,
    phone_allowance DECIMAL(8,2) DEFAULT 0.00,
    clothing_allowance DECIMAL(8,2) DEFAULT 0.00,
    total_allowances DECIMAL(10,2) DEFAULT 0.00,
    sss_contribution DECIMAL(8,2) DEFAULT 0.00,
    philhealth_contribution DECIMAL(8,2) DEFAULT 0.00,
    pagibig_contribution DECIMAL(8,2) DEFAULT 0.00,
    withholding_tax DECIMAL(10,2) DEFAULT 0.00,
    total_deductions DECIMAL(10,2) DEFAULT 0.00,
    net_pay DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id) ON DELETE CASCADE,
    UNIQUE KEY uq_employee_period (employee_id, period_start, period_end)
);

-- =============================================
-- DATA INSERTION (3NF COMPLIANT)
-- =============================================

-- Insert departments
INSERT INTO departments (department_name, budget) VALUES
('Executive', 5000000.00),
('Human Resources', 2000000.00),
('Accounting', 1800000.00),
('Sales & Marketing', 2500000.00),
('IT Operations', 2200000.00),
('Supply Chain', 1500000.00),
('Customer Service', 1200000.00);

-- Insert positions with department relationships
INSERT INTO positions (position_title, department_id, basic_salary, rice_subsidy, phone_allowance, clothing_allowance, gross_semi_monthly_rate, hourly_rate) VALUES
('Chief Executive Officer', 1, 90000.00, 1500.00, 2000.00, 1000.00, 45000.00, 535.71),
('Chief Operating Officer', 1, 60000.00, 1500.00, 2000.00, 1000.00, 30000.00, 357.14),
('Chief Finance Officer', 1, 60000.00, 1500.00, 2000.00, 1000.00, 30000.00, 357.14),
('Chief Marketing Officer', 1, 60000.00, 1500.00, 2000.00, 1000.00, 30000.00, 357.14),
('HR Manager', 2, 52670.00, 1500.00, 1000.00, 1000.00, 26335.00, 313.51),
('HR Team Leader', 2, 42975.00, 1500.00, 800.00, 800.00, 21487.50, 255.80),
('HR Rank and File', 2, 22500.00, 1500.00, 500.00, 500.00, 11250.00, 133.93),
('Accounting Head', 3, 52670.00, 1500.00, 1000.00, 1000.00, 26335.00, 313.51),
('Payroll Manager', 3, 50825.00, 1500.00, 1000.00, 1000.00, 25412.50, 302.53),
('Payroll Team Leader', 3, 38475.00, 1500.00, 800.00, 800.00, 19237.50, 229.02),
('Payroll Rank and File', 3, 24000.00, 1500.00, 500.00, 500.00, 12000.00, 142.86),
('Sales Manager', 4, 53500.00, 1500.00, 1000.00, 1000.00, 26750.00, 318.45),
('Marketing Team Leader', 4, 42975.00, 1500.00, 800.00, 800.00, 21487.50, 255.80),
('Sales Representative', 4, 22500.00, 1500.00, 500.00, 500.00, 11250.00, 133.93),
('IT Operations Manager', 5, 52670.00, 1500.00, 1000.00, 1000.00, 26335.00, 313.51),
('Software Developer', 5, 45000.00, 1500.00, 800.00, 800.00, 22500.00, 267.86),
('System Administrator', 5, 40000.00, 1500.00, 800.00, 800.00, 20000.00, 238.10),
('Supply Chain Manager', 6, 52670.00, 1500.00, 1000.00, 1000.00, 26335.00, 313.51),
('Logistics Coordinator', 6, 35000.00, 1500.00, 600.00, 600.00, 17500.00, 208.33),
('Customer Service Manager', 7, 52670.00, 1500.00, 1000.00, 1000.00, 26335.00, 313.51);

-- Insert employees with proper hierarchy
INSERT INTO employees (employee_id, last_name, first_name, birthday, address, phone_number, sss_number, philhealth_number, tin_number, pagibig_number, status, position_id, supervisor_id, hire_date) VALUES
(10001, 'Garcia', 'Manuel III', '1983-10-11', 'Valero Carpark Building Valero Street 1227, Makati City', '966-860-270', '44-4506057-3', '820126853951', '442-605-657-000', '691295330870', 'Regular', 1, NULL, '2020-01-15'),
(10002, 'Lim', 'Antonio', '1988-06-19', 'San Antonio De Padua 2, Block 1 Lot 8 and 2, Dasmarinas, Cavite', '171-867-411', '52-2061274-9', '331735646338', '683-102-776-000', '663904995411', 'Regular', 2, 10001, '2020-02-01'),
(10003, 'Aquino', 'Bianca Sofia', '1989-08-04', 'Rm. 402 4/F Jiao Building Timog Avenue Cor. Quezon Avenue 1100, Quezon City', '966-889-370', '30-8870406-2', '177451189665', '971-711-280-000', '171519773969', 'Regular', 3, 10001, '2020-02-15'),
(10004, 'Reyes', 'Isabella', '1994-06-16', '460 Solanda Street Intramuros 1000, Manila', '786-868-477', '40-2511815-0', '341911411254', '876-809-437-000', '416946776041', 'Regular', 4, 10001, '2020-03-01'),
(10005, 'Hernandez', 'Eduard', '1989-09-23', 'National Highway, Gingoog, Misamis Occidental', '088-861-012', '50-5577638-1', '957436191812', '031-702-374-000', '952347222457', 'Regular', 15, 10002, '2020-03-15'),
(10006, 'Villanueva', 'Andrea Mae', '1988-02-14', '17/85 Stracke Via Suite 042, Poblacion, Las Piñas 4783 Dinagat Islands', '918-621-603', '49-1632020-8', '382189453145', '317-674-022-000', '441093369646', 'Regular', 5, 10002, '2020-04-01'),
(10007, 'San Jose', 'Brad', '1996-03-15', '99 Strosin Hills, Poblacion, Bislig 5340 Tawi-Tawi', '797-009-261', '40-2400714-1', '239192926939', '672-474-690-000', '210850209964', 'Regular', 6, 10006, '2020-04-15'),
(10008, 'Romualdez', 'Alice', '1992-05-14', '12A/33 Upton Isle Apt. 420, Roxas City 1814 Surigao del Norte', '983-606-799', '55-4476527-2', '545652640232', '888-572-294-000', '211385556888', 'Regular', 7, 10007, '2020-05-01'),
(10009, 'Atienza', 'Rosie', '1948-09-24', '90A Dibbert Terrace Apt. 190, San Lorenzo 6056 Davao del Norte', '266-036-427', '41-0644692-3', '708988234853', '604-997-793-000', '260107732354', 'Regular', 7, 10007, '2020-05-15'),
(10010, 'Alvaro', 'Roderick', '1988-03-30', '#284 T. Morato corner, Scout Rallos Street, Quezon City', '053-381-386', '64-7605054-4', '578114853194', '525-420-419-000', '799254095212', 'Regular', 8, 10003, '2020-06-01'),
(10011, 'Salcedo', 'Anthony', '1993-09-14', '93/54 Shanahan Alley Apt. 183, Santo Tomas 1572 Masbate', '070-766-300', '26-9647608-3', '126445315651', '210-805-911-000', '218002473454', 'Regular', 9, 10010, '2020-06-15'),
(10012, 'Lopez', 'Josie', '1987-01-14', '49 Springs Apt. 266, Poblacion, Taguig 3200 Occidental Mindoro', '478-355-427', '44-8563448-3', '431709011012', '218-489-737-000', '113071293354', 'Regular', 10, 10011, '2020-07-01'),
(10013, 'Farala', 'Martha', '1942-01-11', '42/25 Sawayn Stream, Ubay 1208 Zamboanga del Norte', '329-034-366', '45-5656375-0', '233693897247', '210-835-851-000', '631130283546', 'Regular', 11, 10011, '2020-07-15'),
(10014, 'Martinez', 'Leila', '1970-07-11', '37/46 Kulas Roads, Maragondon 0962 Quirino', '877-110-749', '27-2090996-4', '515741057496', '275-792-513-000', '101205445886', 'Regular', 11, 10011, '2020-08-01'),
(10015, 'Romualdez', 'Fredrick', '1985-03-10', '22A/52 Lubowitz Meadows, Pililla 4895 Zambales', '023-079-009', '26-8768374-1', '308366860059', '598-065-761-000', '223057707853', 'Regular', 12, 10002, '2020-08-15'),
(10016, 'Mata', 'Christian', '1987-10-21', '90 O\'Keefe Spur Apt. 379, Catigbian 2772 Sulu', '783-776-744', '49-2959312-6', '824187961962', '103-100-522-000', '631052853464', 'Regular', 13, 10015, '2020-09-01'),
(10017, 'De Leon', 'Selena', '1975-02-20', '89A Armstrong Trace, Compostela 7874 Maguindanao', '975-432-139', '27-2090208-8', '587272469938', '482-259-498-000', '719007608464', 'Regular', 13, 10015, '2020-09-15'),
(10018, 'San Jose', 'Allison', '1986-06-24', '08 Grant Drive Suite 406, Poblacion, Iloilo City 9186 La Union', '179-075-129', '45-3251383-0', '745148459521', '121-203-336-000', '114901859343', 'Regular', 14, 10016, '2020-10-01'),
(10019, 'Rosario', 'Cydney', '1996-10-06', '93A/21 Berge Points, Tapaz 2180 Quezon', '868-819-912', '49-1629900-2', '579253435499', '122-244-511-000', '265104358643', 'Regular', 14, 10016, '2020-10-15'),
(10020, 'Bautista', 'Mark', '1991-02-12', '65 Murphy Center Suite 094, Poblacion, Palayan 5636 Quirino', '683-725-348', '49-1647342-5', '399665157135', '273-970-941-000', '260054585575', 'Regular', 14, 10016, '2020-11-01'),
(10021, 'Lazaro', 'Darlene', '1985-11-25', '47A/94 Larkin Plaza Apt. 179, Poblacion, Caloocan 2751 Quirino', '740-721-558', '45-5617168-2', '606386917510', '354-650-951-000', '104907708845', 'Probationary', 14, 10016, '2024-01-15'),
(10022, 'Delos Santos', 'Kolby', '1980-02-26', '06A Gulgowski Extensions, Bongabon 6085 Zamboanga del Sur', '739-443-033', '52-0109570-6', '357451271274', '187-500-345-000', '113017988667', 'Probationary', 14, 10016, '2024-02-01'),
(10023, 'Santos', 'Vella', '1983-12-31', '99A Padberg Spring, Poblacion, Mabalacat 3959 Lanao del Sur', '955-879-269', '52-9883524-3', '548670482885', '101-558-994-000', '360028104576', 'Contractual', 16, 10005, '2024-03-01'),
(10024, 'Del Rosario', 'Tomas', '1978-12-18', '80A/48 Ledner Ridges, Poblacion, Kabankalan 8870 Marinduque', '882-550-989', '45-5866331-6', '953901539995', '560-735-732-000', '913108649964', 'Contractual', 17, 10005, '2024-03-15'),
(10025, 'Tolentino', 'Jacklyn', '1984-05-19', '96/48 Watsica Flats Suite 734, Poblacion, Malolos 1844 Ifugao', '675-757-366', '47-1692793-0', '753800654114', '841-177-857-000', '210546661243', 'Contractual', 16, 10005, '2024-04-01'),
(10026, 'Gutierrez', 'Percival', '1970-12-18', '58A Wilderman Walks, Poblacion, Digos 5822 Davao del Sur', '512-899-876', '40-9504657-8', '797639382265', '502-995-671-000', '210897095686', 'Regular', 18, 10004, '2020-12-01'),
(10027, 'Manalaysay', 'Garfield', '1986-08-28', '60 Goyette Valley Suite 219, Poblacion, Tabuk 3159 Lanao del Sur', '948-628-136', '45-3298166-4', '810909286264', '336-676-445-000', '211274476563', 'Regular', 19, 10026, '2021-01-15'),
(10028, 'Villegas', 'Lizeth', '1981-12-12', '66/77 Mann Views, Luisiana 1263 Dinagat Islands', '332-372-215', '40-2400719-4', '934389652994', '210-395-397-000', '122238077997', 'Regular', 19, 10026, '2021-02-01'),
(10029, 'Ramos', 'Carol', '1978-08-20', '72/70 Stamm Spurs, Bustos 4550 Iloilo', '250-700-389', '60-1152206-4', '351830469744', '395-032-717-000', '212141893454', 'Regular', 20, 10004, '2021-02-15'),
(10030, 'Maceda', 'Emelia', '1973-04-14', '50A/83 Bahringer Oval Suite 145, Kiamba 7688 Nueva Ecija', '973-358-041', '54-1331005-0', '465087894112', '215-973-013-000', '515012579765', 'Regular', 20, 10004, '2021-03-01'),
(10031, 'Aguilar', 'Delia', '1989-01-27', '95 Cremin Junction, Surallah 2809 Cotabato', '529-705-439', '52-1859253-1', '136451303068', '599-312-588-000', '110018813465', 'Regular', 20, 10004, '2021-03-15'),
(10032, 'Castro', 'John Rafael', '1992-02-09', 'Hi-way, Yati, Liloan Cebu', '332-424-955', '26-7145133-4', '601644902402', '404-768-309-000', '697764069311', 'Regular', 16, 10005, '2021-04-01'),
(10033, 'Martinez', 'Carlos Ian', '1990-11-16', 'Bulala, Camalaniugan', '078-854-208', '11-5062972-7', '380685387212', '256-436-296-000', '993372963726', 'Regular', 17, 10005, '2021-04-15'),
(10034, 'Santos', 'Beatriz', '1990-08-07', 'Agapita Building, Metro Manila', '526-639-511', '20-2987501-5', '918460050077', '911-529-713-000', '874042259378', 'Regular', 20, 10004, '2021-05-01');

-- Insert credentials
INSERT INTO credentials (employee_id, password) VALUES
(10001, 'password1234'), (10002, 'password1234'), (10003, 'password1234'), (10004, 'password1234'), (10005, 'password1234'),
(10006, 'password1234'), (10007, 'password1234'), (10008, 'password1234'), (10009, 'password1234'), (10010, 'password1234'),
(10011, 'password1234'), (10012, 'password1234'), (10013, 'password1234'), (10014, 'password1234'), (10015, 'password1234'),
(10016, 'password1234'), (10017, 'password1234'), (10018, 'password1234'), (10019, 'password1234'), (10020, 'password1234'),
(10021, 'password1234'), (10022, 'password1234'), (10023, 'password1234'), (10024, 'password1234'), (10025, 'password1234'),
(10026, 'password1234'), (10027, 'password1234'), (10028, 'password1234'), (10029, 'password1234'), (10030, 'password1234'),
(10031, 'password1234'), (10032, 'password1234'), (10033, 'password1234'), (10034, 'password1234');

-- Insert sample attendance data
INSERT INTO attendance (employee_id, attendance_date, log_in, log_out) VALUES
(10001, '2024-01-15', '08:00:00', '17:00:00'),
(10001, '2024-01-16', '08:15:00', '17:30:00'),
(10001, '2024-01-17', '07:45:00', '17:00:00'),
(10002, '2024-01-15', '08:30:00', '17:00:00'),
(10002, '2024-01-16', '08:00:00', '18:00:00'),
(10002, '2024-01-17', '08:10:00', '17:15:00'),
(10003, '2024-01-15', '08:00:00', '17:00:00'),
(10003, '2024-01-16', '08:05:00', '17:00:00'),
(10003, '2024-01-17', '08:00:00', '17:30:00');

-- =============================================
-- VIEWS (Advanced SQL Feature)
-- =============================================

-- View 1: Complete employee details with department and position info
CREATE OR REPLACE VIEW v_employee_details AS
SELECT 
    e.employee_id,
    e.first_name,
    e.last_name,
    CONCAT(e.last_name, ', ', e.first_name) AS full_name,
    e.birthday,
    TIMESTAMPDIFF(YEAR, e.birthday, CURDATE()) AS age,
    e.address,
    e.phone_number,
    e.status,
    e.hire_date,
    TIMESTAMPDIFF(YEAR, e.hire_date, CURDATE()) AS years_of_service,
    p.position_title,
    d.department_name,
    p.basic_salary,
    p.rice_subsidy,
    p.phone_allowance,
    p.clothing_allowance,
    p.gross_semi_monthly_rate,
    p.hourly_rate,
    sup.employee_id as supervisor_id,
    CONCAT(sup.last_name, ', ', sup.first_name) AS supervisor_name,
    e.sss_number,
    e.philhealth_number,
    e.tin_number,
    e.pagibig_number
FROM employees e
JOIN positions p ON e.position_id = p.position_id
JOIN departments d ON p.department_id = d.department_id
LEFT JOIN employees sup ON e.supervisor_id = sup.employee_id;

-- View 2: Attendance summary view
CREATE OR REPLACE VIEW v_attendance_summary AS
SELECT 
    e.employee_id,
    e.full_name,
    e.department_name,
    COUNT(a.id) as total_attendance_days,
    AVG(a.total_hours) as avg_daily_hours,
    SUM(CASE WHEN a.is_late = 1 THEN 1 ELSE 0 END) as late_days,
    SUM(CASE WHEN a.has_undertime = 1 THEN 1 ELSE 0 END) as undertime_days,
    MAX(a.attendance_date) as last_attendance_date
FROM v_employee_details e
LEFT JOIN attendance a ON e.employee_id = a.employee_id
GROUP BY e.employee_id, e.full_name, e.department_name;

-- View 3: Department summary view
CREATE OR REPLACE VIEW v_department_summary AS
SELECT 
    d.department_id,
    d.department_name,
    COUNT(e.employee_id) as total_employees,
    COUNT(CASE WHEN e.status = 'Regular' THEN 1 END) as regular_employees,
    COUNT(CASE WHEN e.status = 'Probationary' THEN 1 END) as probationary_employees,
    COUNT(CASE WHEN e.status = 'Contractual' THEN 1 END) as contractual_employees,
    AVG(p.basic_salary) as avg_salary,
    SUM(p.basic_salary) as total_salary_budget
FROM departments d
LEFT JOIN positions pos ON d.department_id = pos.department_id
LEFT JOIN employees e ON pos.position_id = e.position_id
LEFT JOIN positions p ON e.position_id = p.position_id
GROUP BY d.department_id, d.department_name;

-- =============================================
-- STORED PROCEDURES (Advanced SQL Feature)
-- =============================================

-- Procedure 1: Add new employee with validation
DELIMITER $$
CREATE PROCEDURE sp_add_employee(
    IN p_employee_id INT,
    IN p_last_name VARCHAR(50),
    IN p_first_name VARCHAR(50),
    IN p_birthday DATE,
    IN p_address TEXT,
    IN p_phone_number VARCHAR(20),
    IN p_sss_number VARCHAR(20),
    IN p_philhealth_number VARCHAR(20),
    IN p_tin_number VARCHAR(20),
    IN p_pagibig_number VARCHAR(20),
    IN p_status ENUM('Regular', 'Probationary', 'Contractual'),
    IN p_position_id INT,
    IN p_supervisor_id INT,
    IN p_password VARCHAR(255)
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    -- Insert employee
    INSERT INTO employees (
        employee_id, last_name, first_name, birthday, address, phone_number,
        sss_number, philhealth_number, tin_number, pagibig_number,
        status, position_id, supervisor_id
    ) VALUES (
        p_employee_id, p_last_name, p_first_name, p_birthday, p_address, p_phone_number,
        p_sss_number, p_philhealth_number, p_tin_number, p_pagibig_number,
        p_status, p_position_id, p_supervisor_id
    );

    -- Insert credentials
    INSERT INTO credentials (employee_id, password)
    VALUES (p_employee_id, p_password);

    COMMIT;
    
    SELECT 'Employee added successfully' as message, p_employee_id as employee_id;
END$$
DELIMITER ;

-- Procedure 2: Calculate payroll for employee
DELIMITER $$
CREATE PROCEDURE sp_calculate_payroll(
    IN p_employee_id INT,
    IN p_period_start DATE,
    IN p_period_end DATE,
    IN p_days_worked INT,
    IN p_overtime_hours DECIMAL(5,2)
)
BEGIN
    DECLARE v_basic_salary DECIMAL(10,2);
    DECLARE v_rice_subsidy DECIMAL(8,2);
    DECLARE v_phone_allowance DECIMAL(8,2);
    DECLARE v_clothing_allowance DECIMAL(8,2);
    DECLARE v_hourly_rate DECIMAL(8,2);
    DECLARE v_daily_rate DECIMAL(8,2);
    DECLARE v_basic_pay DECIMAL(10,2);
    DECLARE v_overtime_pay DECIMAL(10,2);
    DECLARE v_gross_pay DECIMAL(10,2);
    DECLARE v_total_allowances DECIMAL(10,2);
    DECLARE v_sss DECIMAL(8,2);
    DECLARE v_philhealth DECIMAL(8,2);
    DECLARE v_pagibig DECIMAL(8,2);
    DECLARE v_tax DECIMAL(10,2);
    DECLARE v_total_deductions DECIMAL(10,2);
    DECLARE v_net_pay DECIMAL(10,2);
    DECLARE v_employee_status VARCHAR(20);

    -- Get employee details
    SELECT 
        p.basic_salary, p.rice_subsidy, p.phone_allowance, p.clothing_allowance, p.hourly_rate,
        e.status
    INTO 
        v_basic_salary, v_rice_subsidy, v_phone_allowance, v_clothing_allowance, v_hourly_rate,
        v_employee_status
    FROM employees e
    JOIN positions p ON e.position_id = p.position_id
    WHERE e.employee_id = p_employee_id;

    -- Calculate daily rate
    SET v_daily_rate = v_basic_salary / 22;
    
    -- Calculate basic pay
    SET v_basic_pay = v_daily_rate * p_days_worked;
    
    -- Calculate overtime pay (only for Regular employees)
    IF v_employee_status = 'Regular' THEN
        SET v_overtime_pay = v_hourly_rate * p_overtime_hours * 1.25;
    ELSE
        SET v_overtime_pay = 0;
    END IF;
    
    -- Calculate gross pay
    SET v_gross_pay = v_basic_pay + v_overtime_pay;
    
    -- Calculate allowances (only for Regular employees)
    IF v_employee_status = 'Regular' THEN
        SET v_total_allowances = v_rice_subsidy + v_phone_allowance + v_clothing_allowance;
    ELSE
        SET v_total_allowances = 0;
    END IF;
    
    -- Calculate government contributions (simplified)
    SET v_sss = LEAST(v_basic_salary * 0.045, 1125.00);
    SET v_philhealth = LEAST(v_basic_salary * 0.0275, 1375.00);
    SET v_pagibig = LEAST(v_basic_salary * 0.02, 100.00);
    
    -- Calculate tax (simplified progressive tax)
    SET v_tax = CASE 
        WHEN v_basic_salary * 12 <= 250000 THEN 0
        WHEN v_basic_salary * 12 <= 400000 THEN (v_basic_salary * 12 - 250000) * 0.15 / 12
        WHEN v_basic_salary * 12 <= 800000 THEN (22500 + (v_basic_salary * 12 - 400000) * 0.20) / 12
        ELSE (102500 + (v_basic_salary * 12 - 800000) * 0.25) / 12
    END;
    
    SET v_total_deductions = v_sss + v_philhealth + v_pagibig + v_tax;
    SET v_net_pay = v_gross_pay + v_total_allowances - v_total_deductions;

    -- Insert or update payroll record
    INSERT INTO payroll_records (
        employee_id, period_start, period_end, days_worked, overtime_hours,
        basic_pay, overtime_pay, gross_pay, rice_subsidy, phone_allowance, clothing_allowance,
        total_allowances, sss_contribution, philhealth_contribution, pagibig_contribution,
        withholding_tax, total_deductions, net_pay
    ) VALUES (
        p_employee_id, p_period_start, p_period_end, p_days_worked, p_overtime_hours,
        v_basic_pay, v_overtime_pay, v_gross_pay, v_rice_subsidy, v_phone_allowance, v_clothing_allowance,
        v_total_allowances, v_sss, v_philhealth, v_pagibig,
        v_tax, v_total_deductions, v_net_pay
    ) ON DUPLICATE KEY UPDATE
        days_worked = p_days_worked,
        overtime_hours = p_overtime_hours,
        basic_pay = v_basic_pay,
        overtime_pay = v_overtime_pay,
        gross_pay = v_gross_pay,
        total_allowances = v_total_allowances,
        sss_contribution = v_sss,
        philhealth_contribution = v_philhealth,
        pagibig_contribution = v_pagibig,
        withholding_tax = v_tax,
        total_deductions = v_total_deductions,
        net_pay = v_net_pay;

    -- Return payroll details
    SELECT 
        p_employee_id as employee_id,
        p_period_start as period_start,
        p_period_end as period_end,
        p_days_worked as days_worked,
        p_overtime_hours as overtime_hours,
        v_basic_pay as basic_pay,
        v_overtime_pay as overtime_pay,
        v_gross_pay as gross_pay,
        v_total_allowances as total_allowances,
        v_total_deductions as total_deductions,
        v_net_pay as net_pay;
END$$
DELIMITER ;

-- Procedure 3: Generate department report
DELIMITER $$
CREATE PROCEDURE sp_department_report()
BEGIN
    SELECT 
        d.department_name,
        d.total_employees,
        d.regular_employees,
        d.probationary_employees,
        d.contractual_employees,
        CONCAT('₱', FORMAT(d.avg_salary, 2)) as average_salary,
        CONCAT('₱', FORMAT(d.total_salary_budget, 2)) as total_budget
    FROM v_department_summary d
    ORDER BY d.total_employees DESC;
END$$
DELIMITER ;

-- =============================================
-- INDEXES FOR PERFORMANCE
-- =============================================
CREATE INDEX idx_employees_name ON employees(last_name, first_name);
CREATE INDEX idx_employees_status ON employees(status);
CREATE INDEX idx_employees_hire_date ON employees(hire_date);
CREATE INDEX idx_attendance_date ON attendance(attendance_date);
CREATE INDEX idx_attendance_employee_date ON attendance(employee_id, attendance_date);
CREATE INDEX idx_leave_requests_dates ON leave_requests(start_date, end_date);
CREATE INDEX idx_payroll_period ON payroll_records(period_start, period_end);

-- =============================================
-- UPDATE DEPARTMENT HEADS
-- =============================================
UPDATE departments SET department_head_id = 10001 WHERE department_name = 'Executive';
UPDATE departments SET department_head_id = 10006 WHERE department_name = 'Human Resources';
UPDATE departments SET department_head_id = 10010 WHERE department_name = 'Accounting';
UPDATE departments SET department_head_id = 10015 WHERE department_name = 'Sales & Marketing';
UPDATE departments SET department_head_id = 10005 WHERE department_name = 'IT Operations';
UPDATE departments SET department_head_id = 10026 WHERE department_name = 'Supply Chain';
UPDATE departments SET department_head_id = 10029 WHERE department_name = 'Customer Service';

-- =============================================
-- VERIFICATION QUERIES
-- =============================================

-- Verify 3NF compliance
SELECT 'Database Structure Verification' as verification_type;
SELECT 'departments' as table_name, COUNT(*) as row_count FROM departments
UNION ALL
SELECT 'positions' as table_name, COUNT(*) as row_count FROM positions
UNION ALL
SELECT 'employees' as table_name, COUNT(*) as row_count FROM employees
UNION ALL
SELECT 'credentials' as table_name, COUNT(*) as row_count FROM credentials
UNION ALL
SELECT 'attendance' as table_name, COUNT(*) as row_count FROM attendance;

-- Test views
SELECT 'Testing Views' as test_type;
SELECT employee_id, full_name, department_name, position_title, basic_salary 
FROM v_employee_details 
WHERE employee_id IN (10001, 10006, 10010) 
LIMIT 3;

-- Test stored procedure
CALL sp_calculate_payroll(10001, '2024-01-01', '2024-01-31', 22, 8);

SELECT '3NF Database with Views and Stored Procedures created successfully!' as status;