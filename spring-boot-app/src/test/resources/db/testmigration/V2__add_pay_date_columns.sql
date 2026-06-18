-- Add next_pay_date and pay_frequency columns to employee_pto table.

ALTER TABLE employee_pto ADD COLUMN IF NOT EXISTS next_pay_date DATE;
ALTER TABLE employee_pto ADD COLUMN IF NOT EXISTS pay_frequency VARCHAR(20) DEFAULT 'BIWEEKLY';

UPDATE employee_pto SET next_pay_date = CURRENT_DATE + 14 WHERE next_pay_date IS NULL;
