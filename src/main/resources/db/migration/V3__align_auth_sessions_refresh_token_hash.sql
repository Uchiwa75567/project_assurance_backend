CREATE TABLE IF NOT EXISTS auth_sessions (
  id VARCHAR(64) PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL,
  refresh_token_hash VARCHAR(512) NOT NULL DEFAULT '',
  issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  expires_at TIMESTAMP NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE
);

ALTER TABLE auth_sessions
  ADD COLUMN IF NOT EXISTS refresh_token_hash VARCHAR(512);

ALTER TABLE auth_sessions
  ALTER COLUMN refresh_token_hash SET DEFAULT '';

UPDATE auth_sessions
SET refresh_token_hash = ''
WHERE refresh_token_hash IS NULL;

ALTER TABLE auth_sessions
  ALTER COLUMN refresh_token_hash SET NOT NULL;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'auth_sessions'
      AND column_name = 'refresh_token'
  ) THEN
    UPDATE auth_sessions
    SET active = FALSE
    WHERE refresh_token_hash = '';
  END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS ux_auth_sessions_refresh_token_hash
  ON auth_sessions(refresh_token_hash)
  WHERE refresh_token_hash <> '';
