-- Create core tables for a fresh database (aligned with current JPA model)

CREATE TABLE IF NOT EXISTS users (
  id VARCHAR(64) PRIMARY KEY,
  email VARCHAR(255) UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(255) NOT NULL,
  role VARCHAR(32) NOT NULL,
  actif BOOLEAN NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS auth_sessions (
  id VARCHAR(64) PRIMARY KEY,
  user_id VARCHAR(64) NOT NULL,
  refresh_token_hash VARCHAR(512) NOT NULL UNIQUE,
  issued_at TIMESTAMP NOT NULL,
  expires_at TIMESTAMP NOT NULL,
  active BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS admins (
  id VARCHAR(64) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS agents (
  id VARCHAR(64) PRIMARY KEY,
  matricule VARCHAR(120) NOT NULL,
  prenom VARCHAR(120) NOT NULL,
  nom VARCHAR(120) NOT NULL,
  telephone VARCHAR(64) NOT NULL,
  statut VARCHAR(64) NOT NULL
);

CREATE TABLE IF NOT EXISTS agent_locations (
  id VARCHAR(64) PRIMARY KEY,
  agent_id VARCHAR(64) NOT NULL,
  latitude DOUBLE PRECISION NOT NULL,
  longitude DOUBLE PRECISION NOT NULL,
  speed_kmh DOUBLE PRECISION NOT NULL,
  moving BOOLEAN NOT NULL,
  updated_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS clients (
  id VARCHAR(64) PRIMARY KEY,
  user_id VARCHAR(64) UNIQUE,
  numero_assurance VARCHAR(120) NOT NULL UNIQUE,
  prenom VARCHAR(120) NOT NULL,
  nom VARCHAR(120) NOT NULL,
  date_naissance DATE,
  telephone VARCHAR(64) NOT NULL,
  adresse VARCHAR(255),
  numero_cni VARCHAR(120),
  photo_url VARCHAR(255),
  type_assurance VARCHAR(120),
  statut VARCHAR(32) NOT NULL,
  created_by_agent_id VARCHAR(64),
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS packs (
  id VARCHAR(64) PRIMARY KEY,
  code VARCHAR(120) NOT NULL UNIQUE,
  nom VARCHAR(255) NOT NULL,
  description TEXT,
  prix NUMERIC(19,2) NOT NULL,
  duree INTEGER NOT NULL,
  actif BOOLEAN NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS garanties (
  id VARCHAR(64) PRIMARY KEY,
  libelle VARCHAR(255) NOT NULL,
  description TEXT,
  plafond NUMERIC(19,2)
);

CREATE TABLE IF NOT EXISTS pack_garanties (
  id VARCHAR(64) PRIMARY KEY,
  pack_id VARCHAR(64) NOT NULL,
  garantie_id VARCHAR(64) NOT NULL,
  plafond_specifique NUMERIC(19,2),
  UNIQUE (pack_id, garantie_id)
);

CREATE TABLE IF NOT EXISTS partenaires (
  id VARCHAR(64) PRIMARY KEY,
  user_id VARCHAR(64),
  nom VARCHAR(255) NOT NULL,
  type VARCHAR(120) NOT NULL,
  adresse VARCHAR(255),
  telephone VARCHAR(64),
  latitude DOUBLE PRECISION NOT NULL,
  longitude DOUBLE PRECISION NOT NULL,
  actif BOOLEAN NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS conventions_partenaires (
  id VARCHAR(64) PRIMARY KEY,
  pack_id VARCHAR(64) NOT NULL,
  partenaire_id VARCHAR(64) NOT NULL,
  acceptee BOOLEAN NOT NULL,
  taux_couverture NUMERIC(8,2),
  plafond NUMERIC(19,2),
  actif BOOLEAN NOT NULL,
  UNIQUE (pack_id, partenaire_id)
);

CREATE TABLE IF NOT EXISTS souscriptions (
  id VARCHAR(64) PRIMARY KEY,
  client_id VARCHAR(64) NOT NULL,
  agent_id VARCHAR(64),
  pack_id VARCHAR(64) NOT NULL,
  date_debut DATE NOT NULL,
  date_fin DATE,
  date_prochain_paiement DATE,
  statut VARCHAR(32) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS paiements (
  id VARCHAR(64) PRIMARY KEY,
  souscription_id VARCHAR(64) NOT NULL,
  montant NUMERIC(19,2) NOT NULL,
  reference VARCHAR(120),
  statut VARCHAR(32) NOT NULL,
  provider VARCHAR(80),
  transaction_id VARCHAR(120),
  payment_url VARCHAR(255),
  date_creation TIMESTAMP NOT NULL,
  date_validation TIMESTAMP,
  period_debut DATE,
  period_fin DATE
);

CREATE TABLE IF NOT EXISTS cartes (
  id VARCHAR(64) PRIMARY KEY,
  souscription_id VARCHAR(64) NOT NULL UNIQUE,
  numero_carte VARCHAR(120) NOT NULL UNIQUE,
  date_emission DATE,
  date_expiration DATE,
  qr_code VARCHAR(255),
  statut VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS beneficiaires (
  id VARCHAR(64) PRIMARY KEY,
  souscription_id VARCHAR(64) NOT NULL,
  nom VARCHAR(120) NOT NULL,
  prenom VARCHAR(120) NOT NULL,
  date_naissance DATE,
  lien VARCHAR(80),
  is_principal BOOLEAN NOT NULL
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_beneficiaires_souscription ON beneficiaires(souscription_id);
CREATE INDEX IF NOT EXISTS idx_souscriptions_client ON souscriptions(client_id);
CREATE INDEX IF NOT EXISTS idx_souscriptions_pack ON souscriptions(pack_id);
