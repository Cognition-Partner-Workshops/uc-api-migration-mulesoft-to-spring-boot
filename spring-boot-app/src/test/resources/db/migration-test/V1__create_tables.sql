-- H2-compatible schema for tests (mirrors the PostgreSQL schema in main)

CREATE TABLE IF NOT EXISTS api_clients (
    id INT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(255) NOT NULL UNIQUE,
    client_secret VARCHAR(255) NOT NULL,
    user_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employee_goals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    goal_description CLOB NOT NULL,
    target_date DATE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employee_learning (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    course_name VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    progress INTEGER DEFAULT 0,
    started_at TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS employee_pto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    total_hours DOUBLE NOT NULL DEFAULT 160.0,
    used_hours DOUBLE NOT NULL DEFAULT 0.0,
    "year" INTEGER NOT NULL DEFAULT YEAR(CURRENT_DATE)
);

CREATE TABLE IF NOT EXISTS pto_requests (
    id INT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    hours DOUBLE NOT NULL,
    status VARCHAR(20) DEFAULT 'APPROVED',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Seed data for testing
MERGE INTO api_clients (client_id, client_secret, user_id)
KEY (client_id)
VALUES ('demo-client', 'demo-secret', 'user-001');

INSERT INTO employee_goals (employee_id, goal_description, status)
VALUES
    ('EMP001', 'Build an Agentforce Agent using the new MCP protocol for integrations.', 'ACTIVE'),
    ('EMP001', 'Complete Spring Boot certification by Q3.', 'ACTIVE'),
    ('EMP002', 'Lead the API modernization initiative.', 'ACTIVE');

INSERT INTO employee_learning (employee_id, course_name, status, progress)
VALUES
    ('EMP001', 'Spring Boot Fundamentals', 'COMPLETED', 100),
    ('EMP001', 'Microservices Architecture', 'IN_PROGRESS', 65),
    ('EMP002', 'Cloud-Native Development', 'NOT_STARTED', 0);

INSERT INTO employee_pto (employee_id, total_hours, used_hours, "year")
VALUES
    ('EMP001', 160.0, 40.0, YEAR(CURRENT_DATE)),
    ('EMP002', 160.0, 16.0, YEAR(CURRENT_DATE));
