-- Add columns used by the OAuth token-store (mirrors MuleSoft ObjectStore token management).
-- The MuleSoft source stores access_token + expires_at in api_clients via ON CONFLICT upsert.
ALTER TABLE api_clients ADD COLUMN access_token VARCHAR(512);
ALTER TABLE api_clients ADD COLUMN expires_at TIMESTAMP;
ALTER TABLE api_clients ADD COLUMN callback_url VARCHAR(512);
