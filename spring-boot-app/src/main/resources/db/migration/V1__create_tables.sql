-- Schema matching the MuleSoft source PostgreSQL tables
-- This is the SAME schema the source MuleSoft app uses

CREATE TABLE IF NOT EXISTS api_clients (
    id SERIAL PRIMARY KEY,
    client_id VARCHAR(255) NOT NULL UNIQUE,
    client_secret VARCHAR(255) NOT NULL,
    user_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employee_goals (
    id SERIAL PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    goal_description TEXT NOT NULL,
    target_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employee_learning (
    id SERIAL PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    course_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    progress INTEGER DEFAULT 0,
    started_at TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employee_pto (
    id SERIAL PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    total_hours DOUBLE PRECISION NOT NULL DEFAULT 160.0,
    used_hours DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    "year" INTEGER NOT NULL DEFAULT 2025
);

CREATE TABLE IF NOT EXISTS pto_requests (
    id SERIAL PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    hours DOUBLE PRECISION NOT NULL,
    status VARCHAR(20) DEFAULT 'APPROVED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employee_pay_schedule (
    id SERIAL PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL UNIQUE,
    pay_frequency VARCHAR(20) NOT NULL,
    anchor_pay_date DATE NOT NULL
);

INSERT INTO api_clients (client_id, client_secret, user_id)
SELECT 'demo-client', 'demo-secret', 'user-001'
WHERE NOT EXISTS (SELECT 1 FROM api_clients WHERE client_id = 'demo-client');

INSERT INTO employee_goals (employee_id, goal_description, status)
SELECT 'EMP001', 'Build an Agentforce Agent using the new MCP protocol for integrations.', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM employee_goals WHERE employee_id = 'EMP001');
INSERT INTO employee_goals (employee_id, goal_description, status)
SELECT 'EMP001', 'Complete Spring Boot certification by Q3.', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM employee_goals WHERE employee_id = 'EMP001' AND goal_description = 'Complete Spring Boot certification by Q3.');
INSERT INTO employee_goals (employee_id, goal_description, status)
SELECT '1001', 'Deliver the employee directory modernization.', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM employee_goals WHERE employee_id = '1001');
INSERT INTO employee_goals (employee_id, goal_description, status)
SELECT '1001', 'Improve quarterly employee data quality.', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM employee_goals WHERE employee_id = '1001' AND goal_description = 'Improve quarterly employee data quality.');
INSERT INTO employee_goals (employee_id, goal_description, status)
SELECT '1002', 'Complete the payroll integration milestone.', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM employee_goals WHERE employee_id = '1002');
INSERT INTO employee_goals (employee_id, goal_description, status)
SELECT '1002', 'Document payroll support procedures.', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM employee_goals WHERE employee_id = '1002' AND goal_description = 'Document payroll support procedures.');
INSERT INTO employee_goals (employee_id, goal_description, status)
SELECT '1003', 'Launch the learning analytics dashboard.', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM employee_goals WHERE employee_id = '1003');
INSERT INTO employee_goals (employee_id, goal_description, status)
SELECT '1003', 'Complete the annual compliance review.', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM employee_goals WHERE employee_id = '1003' AND goal_description = 'Complete the annual compliance review.');
INSERT INTO employee_goals (employee_id, goal_description, status)
SELECT 'EMP002', 'Lead the API modernization initiative.', 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM employee_goals WHERE employee_id = 'EMP002');

INSERT INTO employee_learning (employee_id, course_name, status, progress)
SELECT 'EMP001', 'Spring Boot Fundamentals', 'COMPLETED', 100
WHERE NOT EXISTS (SELECT 1 FROM employee_learning WHERE employee_id = 'EMP001');
INSERT INTO employee_learning (employee_id, course_name, status, progress)
SELECT 'EMP001', 'Microservices Architecture', 'IN_PROGRESS', 65
WHERE NOT EXISTS (SELECT 1 FROM employee_learning WHERE employee_id = 'EMP001' AND course_name = 'Microservices Architecture');
INSERT INTO employee_learning (employee_id, course_name, status, progress)
SELECT '1001', 'Spring Boot Fundamentals', 'NOT_STARTED', 0
WHERE NOT EXISTS (SELECT 1 FROM employee_learning WHERE employee_id = '1001');
INSERT INTO employee_learning (employee_id, course_name, status, progress)
SELECT '1001', 'API Design', 'IN_PROGRESS', 50
WHERE NOT EXISTS (SELECT 1 FROM employee_learning WHERE employee_id = '1001' AND course_name = 'API Design');
INSERT INTO employee_learning (employee_id, course_name, status, progress)
SELECT '1002', 'Payroll Systems', 'COMPLETED', 100
WHERE NOT EXISTS (SELECT 1 FROM employee_learning WHERE employee_id = '1002');
INSERT INTO employee_learning (employee_id, course_name, status, progress)
SELECT '1002', 'Cloud-Native Development', 'IN_PROGRESS', 35
WHERE NOT EXISTS (SELECT 1 FROM employee_learning WHERE employee_id = '1002' AND course_name = 'Cloud-Native Development');
INSERT INTO employee_learning (employee_id, course_name, status, progress)
SELECT '1003', 'Learning Analytics', 'NOT_STARTED', 0
WHERE NOT EXISTS (SELECT 1 FROM employee_learning WHERE employee_id = '1003');
INSERT INTO employee_learning (employee_id, course_name, status, progress)
SELECT '1003', 'Compliance Training', 'COMPLETED', 100
WHERE NOT EXISTS (SELECT 1 FROM employee_learning WHERE employee_id = '1003' AND course_name = 'Compliance Training');
INSERT INTO employee_learning (employee_id, course_name, status, progress)
SELECT 'EMP002', 'Cloud-Native Development', 'NOT_STARTED', 0
WHERE NOT EXISTS (SELECT 1 FROM employee_learning WHERE employee_id = 'EMP002');

INSERT INTO employee_pto (employee_id, total_hours, used_hours, "year")
SELECT 'EMP001', 2000.0, 40.0, 2025
WHERE NOT EXISTS (SELECT 1 FROM employee_pto WHERE employee_id = 'EMP001' AND "year" = 2025);
INSERT INTO employee_pto (employee_id, total_hours, used_hours, "year")
SELECT '1001', 160.0, 16.0, 2025
WHERE NOT EXISTS (SELECT 1 FROM employee_pto WHERE employee_id = '1001' AND "year" = 2025);
INSERT INTO employee_pto (employee_id, total_hours, used_hours, "year")
SELECT '1002', 160.0, 24.0, 2025
WHERE NOT EXISTS (SELECT 1 FROM employee_pto WHERE employee_id = '1002' AND "year" = 2025);
INSERT INTO employee_pto (employee_id, total_hours, used_hours, "year")
SELECT '1003', 160.0, 32.0, 2025
WHERE NOT EXISTS (SELECT 1 FROM employee_pto WHERE employee_id = '1003' AND "year" = 2025);
INSERT INTO employee_pto (employee_id, total_hours, used_hours, "year")
SELECT 'EMP002', 160.0, 16.0, 2025
WHERE NOT EXISTS (SELECT 1 FROM employee_pto WHERE employee_id = 'EMP002' AND "year" = 2025);

INSERT INTO employee_pay_schedule (employee_id, pay_frequency, anchor_pay_date)
SELECT 'EMP001', 'BI_WEEKLY', '2025-01-03'
WHERE NOT EXISTS (SELECT 1 FROM employee_pay_schedule WHERE employee_id = 'EMP001');
INSERT INTO employee_pay_schedule (employee_id, pay_frequency, anchor_pay_date)
SELECT '1001', 'BI_WEEKLY', '2025-01-03'
WHERE NOT EXISTS (SELECT 1 FROM employee_pay_schedule WHERE employee_id = '1001');
INSERT INTO employee_pay_schedule (employee_id, pay_frequency, anchor_pay_date)
SELECT '1002', 'MONTHLY', '2025-01-03'
WHERE NOT EXISTS (SELECT 1 FROM employee_pay_schedule WHERE employee_id = '1002');
INSERT INTO employee_pay_schedule (employee_id, pay_frequency, anchor_pay_date)
SELECT '1003', 'SEMI_MONTHLY', '2025-01-03'
WHERE NOT EXISTS (SELECT 1 FROM employee_pay_schedule WHERE employee_id = '1003');
