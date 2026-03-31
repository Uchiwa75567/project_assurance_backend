-- Align schema with updated domain model (pack/garantie join, conventions, beneficiaires, cartes, paiements)

-- Packs: add prix + duree
ALTER TABLE IF EXISTS packs ADD COLUMN IF NOT EXISTS prix NUMERIC(19,2);
ALTER TABLE IF EXISTS packs ADD COLUMN IF NOT EXISTS duree INTEGER;

-- Backfill prix/duree only when table exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_name = 'packs'
    ) THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name = 'packs' AND column_name = 'prix_mensuel'
        ) THEN
            UPDATE packs SET prix = prix_mensuel WHERE prix IS NULL;
        END IF;
        UPDATE packs SET duree = 1 WHERE duree IS NULL;
        ALTER TABLE packs ALTER COLUMN prix SET NOT NULL;
        ALTER TABLE packs ALTER COLUMN duree SET NOT NULL;
    END IF;
END $$;

-- Partenaires: add user_id
ALTER TABLE IF EXISTS partenaires ADD COLUMN IF NOT EXISTS user_id VARCHAR(64);

-- Souscriptions: add agent_id + date_prochain_paiement
ALTER TABLE IF EXISTS souscriptions ADD COLUMN IF NOT EXISTS agent_id VARCHAR(64);
ALTER TABLE IF EXISTS souscriptions ADD COLUMN IF NOT EXISTS date_prochain_paiement DATE;

-- Paiements: add new fields
ALTER TABLE IF EXISTS paiements ADD COLUMN IF NOT EXISTS reference VARCHAR(120);
ALTER TABLE IF EXISTS paiements ADD COLUMN IF NOT EXISTS provider VARCHAR(80);
ALTER TABLE IF EXISTS paiements ADD COLUMN IF NOT EXISTS transaction_id VARCHAR(120);
ALTER TABLE IF EXISTS paiements ADD COLUMN IF NOT EXISTS payment_url VARCHAR(255);
ALTER TABLE IF EXISTS paiements ADD COLUMN IF NOT EXISTS date_creation TIMESTAMP;
ALTER TABLE IF EXISTS paiements ADD COLUMN IF NOT EXISTS date_validation TIMESTAMP;
ALTER TABLE IF EXISTS paiements ADD COLUMN IF NOT EXISTS period_debut DATE;
ALTER TABLE IF EXISTS paiements ADD COLUMN IF NOT EXISTS period_fin DATE;

-- Backfill date_creation from created_at if present
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_name = 'paiements'
    ) THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name = 'paiements' AND column_name = 'created_at'
        ) THEN
            UPDATE paiements SET date_creation = created_at WHERE date_creation IS NULL;
        END IF;
        ALTER TABLE paiements ALTER COLUMN date_creation SET NOT NULL;
    END IF;
END $$;

-- Cartes: migrate to souscription-based cards
ALTER TABLE IF EXISTS cartes ADD COLUMN IF NOT EXISTS souscription_id VARCHAR(64);
ALTER TABLE IF EXISTS cartes ADD COLUMN IF NOT EXISTS date_emission DATE;
ALTER TABLE IF EXISTS cartes ADD COLUMN IF NOT EXISTS date_expiration DATE;
ALTER TABLE IF EXISTS cartes ADD COLUMN IF NOT EXISTS qr_code VARCHAR(255);

-- Backfill date_emission/date_expiration from validite if present
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_name = 'cartes'
    ) THEN
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name = 'cartes' AND column_name = 'validite'
        ) THEN
            UPDATE cartes SET date_expiration = validite WHERE date_expiration IS NULL;
            UPDATE cartes SET date_emission = CURRENT_DATE WHERE date_emission IS NULL;
        END IF;

        -- Keep existing databases deployable:
        -- older rows may exist without a linked souscription, so only enforce
        -- the NOT NULL constraint when the table is already clean.
        IF NOT EXISTS (
            SELECT 1
            FROM cartes
            WHERE souscription_id IS NULL
        ) THEN
            ALTER TABLE cartes ALTER COLUMN souscription_id SET NOT NULL;
        END IF;
    END IF;
END $$;

-- Agent locations: add id primary key if missing (only if table exists)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.tables
        WHERE table_name = 'agent_locations'
    ) THEN
        ALTER TABLE agent_locations ADD COLUMN IF NOT EXISTS id VARCHAR(64);
        UPDATE agent_locations
        SET id = md5(random()::text || clock_timestamp()::text)
        WHERE id IS NULL;
        ALTER TABLE agent_locations DROP CONSTRAINT IF EXISTS agent_locations_pkey;
        ALTER TABLE agent_locations ADD CONSTRAINT agent_locations_pkey PRIMARY KEY (id);
        ALTER TABLE agent_locations DROP CONSTRAINT IF EXISTS uq_agent_locations_agent;
    END IF;
END $$;

-- Admins table
CREATE TABLE IF NOT EXISTS admins (
  id VARCHAR(64) PRIMARY KEY
);

-- Beneficiaires table
CREATE TABLE IF NOT EXISTS beneficiaires (
  id VARCHAR(64) PRIMARY KEY,
  souscription_id VARCHAR(64) NOT NULL,
  nom VARCHAR(120) NOT NULL,
  prenom VARCHAR(120) NOT NULL,
  date_naissance DATE,
  lien VARCHAR(80),
  is_principal BOOLEAN NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_beneficiaires_souscription ON beneficiaires(souscription_id);

-- Pack-Garanties join table
CREATE TABLE IF NOT EXISTS pack_garanties (
  id VARCHAR(64) PRIMARY KEY,
  pack_id VARCHAR(64) NOT NULL,
  garantie_id VARCHAR(64) NOT NULL,
  plafond_specifique NUMERIC(19,2)
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_pack_garanties ON pack_garanties(pack_id, garantie_id);

-- Conventions partenaires table
CREATE TABLE IF NOT EXISTS conventions_partenaires (
  id VARCHAR(64) PRIMARY KEY,
  pack_id VARCHAR(64) NOT NULL,
  partenaire_id VARCHAR(64) NOT NULL,
  acceptee BOOLEAN NOT NULL,
  taux_couverture NUMERIC(8,2),
  plafond NUMERIC(19,2),
  actif BOOLEAN NOT NULL
);
CREATE UNIQUE INDEX IF NOT EXISTS uq_convention_pack_partenaire ON conventions_partenaires(pack_id, partenaire_id);
