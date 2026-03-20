DO $$
DECLARE
  rec RECORD;
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'auth_sessions'
      AND column_name = 'token'
  ) THEN
    -- Drop UNIQUE constraints that still target the legacy "token" column.
    FOR rec IN
      SELECT tc.constraint_name
      FROM information_schema.table_constraints tc
      JOIN information_schema.key_column_usage kcu
        ON tc.constraint_name = kcu.constraint_name
       AND tc.table_schema = kcu.table_schema
       AND tc.table_name = kcu.table_name
      WHERE tc.table_schema = 'public'
        AND tc.table_name = 'auth_sessions'
        AND tc.constraint_type = 'UNIQUE'
        AND kcu.column_name = 'token'
    LOOP
      EXECUTE format('ALTER TABLE public.auth_sessions DROP CONSTRAINT IF EXISTS %I', rec.constraint_name);
    END LOOP;

    -- Drop standalone UNIQUE indexes on "token" if any remain.
    FOR rec IN
      SELECT indexname
      FROM pg_indexes
      WHERE schemaname = 'public'
        AND tablename = 'auth_sessions'
        AND indexdef ILIKE '%UNIQUE%'
        AND indexdef ILIKE '%(token)%'
    LOOP
      EXECUTE format('DROP INDEX IF EXISTS public.%I', rec.indexname);
    END LOOP;
  END IF;
END $$;
