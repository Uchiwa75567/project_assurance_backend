-- V9__otp_codes.sql
-- Table pour OTP inscription client

CREATE TABLE IF NOT EXISTS otp_codes (
  id VARCHAR(64) PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL,
  type VARCHAR(10) NOT NULL CHECK (type IN ('SMS', 'EMAIL')),
  code VARCHAR(6) NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  used BOOLEAN NOT NULL DEFAULT false,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_otp_user_type ON otp_codes(user_id, type);
CREATE INDEX IF NOT EXISTS idx_otp_used_expires ON otp_codes(used, expires_at);

-- Update users table for status
ALTER TABLE users ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('PENDING', 'ACTIVE'));

-- Update clients table for status sync
ALTER TABLE clients ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE' CHECK (status IN ('PENDING', 'ACTIVE'));

