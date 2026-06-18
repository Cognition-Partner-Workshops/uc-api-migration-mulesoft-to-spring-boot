-- Add token storage columns to api_clients
-- Mirrors the MuleSoft source ALTER TABLE from database-setup.sql
ALTER TABLE api_clients ADD COLUMN access_token VARCHAR(255);
ALTER TABLE api_clients ADD COLUMN expires_at TIMESTAMP;
