DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'auth_sessions'
      AND column_name = 'token'
  ) THEN
    ALTER TABLE auth_sessions
      ALTER COLUMN token DROP NOT NULL;

    ALTER TABLE auth_sessions
      ALTER COLUMN token SET DEFAULT '';

    UPDATE auth_sessions
    SET token = ''
    WHERE token IS NULL;
  END IF;
END $$;
