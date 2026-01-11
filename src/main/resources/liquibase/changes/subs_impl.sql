alter table subscription_plans
    add column if not exists icon varchar(100) not null default 'pi pi-cog';

alter table subscription_plans
    add column if not exists price jsonb;

alter table subscription_plans
    add column if not exists features text[];

alter table subscription_plans
    add column if not exists color varchar(100);

alter table subscription_plans
    add column if not exists popular boolean default false;

comment on column subscription_plans.color is 'Choose between blue, green and orange';

-- License key table
CREATE TABLE IF NOT EXISTS license_keys
(
    id                  SERIAL8 PRIMARY KEY,
    license_key         VARCHAR(100) UNIQUE NOT NULL,
    product_id          VARCHAR(50)         NOT NULL,
    partner_code        VARCHAR(20),
    is_active           BOOLEAN   DEFAULT true,
    is_used             BOOLEAN   DEFAULT false,
    hardware_id         VARCHAR(255),
    ip_address          VARCHAR(45),
    activation_date     TIMESTAMP,
    expiry_date         TIMESTAMP,
    max_activations     INTEGER   DEFAULT 1,
    current_activations INTEGER   DEFAULT 0,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- License activation history
CREATE TABLE IF NOT EXISTS license_activations
(
    id                SERIAL8 PRIMARY KEY,
    license_key_id    SERIAL8 NOT NULL REFERENCES license_keys (id),
    activation_date   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    hardware_id       VARCHAR(255),
    ip_address        VARCHAR(45),
    user_agent        TEXT,
    is_valid          BOOLEAN   DEFAULT true,
    deactivation_date TIMESTAMP
);

-- Indexes
CREATE INDEX idx_license_key ON license_keys (license_key);
CREATE INDEX idx_license_user ON license_keys (partner_code);
CREATE INDEX idx_license_expiry ON license_keys (expiry_date);
CREATE INDEX idx_license_activations ON license_activations (license_key_id);