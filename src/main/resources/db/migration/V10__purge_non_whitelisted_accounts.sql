-- Keep only the requested demo accounts.
-- If the old admin email still exists, rename it to the new one when possible.

UPDATE users
SET email = 'admijn@masante.sn'
WHERE lower(email) = 'admin@masante.sn'
  AND NOT EXISTS (
    SELECT 1
    FROM users
    WHERE lower(email) = 'admijn@masante.sn'
  );

DELETE FROM auth_sessions
WHERE user_id NOT IN (
  SELECT id
  FROM users
  WHERE lower(email) IN ('admijn@masante.sn', 'client@masante.sn')
);

DELETE FROM otp_codes
WHERE user_id NOT IN (
  SELECT id
  FROM users
  WHERE lower(email) IN ('admijn@masante.sn', 'client@masante.sn')
);

DELETE FROM clients
WHERE user_id IS NOT NULL
  AND user_id NOT IN (
    SELECT id
    FROM users
    WHERE lower(email) IN ('admijn@masante.sn', 'client@masante.sn')
  );

DELETE FROM partenaires
WHERE user_id IS NOT NULL
  AND user_id NOT IN (
    SELECT id
    FROM users
    WHERE lower(email) IN ('admijn@masante.sn', 'client@masante.sn')
  );

DELETE FROM admins
WHERE id NOT IN (
  SELECT id
  FROM users
  WHERE lower(email) IN ('admijn@masante.sn', 'client@masante.sn')
);

DELETE FROM users
WHERE lower(email) NOT IN ('admijn@masante.sn', 'client@masante.sn');
