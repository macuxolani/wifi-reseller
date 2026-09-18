CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE customers (
    id UUID PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    account_created_at TIMESTAMPTZ NOT NULL,
    last_login_at TIMESTAMPTZ,
    last_seen_at TIMESTAMPTZ
);

CREATE INDEX idx_customers_phone_number ON customers(phone_number);
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_username ON customers(username);

CREATE TABLE packages (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    duration_minutes INTEGER NOT NULL,
    download_speed_mbps INTEGER NOT NULL,
    upload_speed_mbps INTEGER NOT NULL,
    price NUMERIC(19,2) NOT NULL,
    currency CHAR(3) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ,
    status VARCHAR(32) NOT NULL
);

CREATE TABLE vouchers (
    id UUID PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    package_id UUID,
    duration_minutes INTEGER,
    speed_download_mbps INTEGER,
    speed_upload_mbps INTEGER,
    price NUMERIC(19,2),
    status VARCHAR(32) NOT NULL,
    generated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    activated_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ,
    customer_id UUID,
    created_by VARCHAR(100),
    batch_id VARCHAR(100),
    CONSTRAINT fk_vouchers_package FOREIGN KEY (package_id) REFERENCES packages(id)
);

CREATE INDEX idx_vouchers_code ON vouchers(code);
CREATE INDEX idx_vouchers_status ON vouchers(status);
CREATE INDEX idx_vouchers_customer_id ON vouchers(customer_id);

CREATE TABLE hotspot (
    id UUID PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    location_name VARCHAR(120),
    address VARCHAR(255),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    router_ip VARCHAR(45),
    router_api_port INTEGER,
    radius_server VARCHAR(255),
    radius_secret_encrypted TEXT,
    router_username_encrypted TEXT,
    router_password_encrypted TEXT,
    status VARCHAR(32) NOT NULL,
    last_heartbeat TIMESTAMPTZ,
    max_users INTEGER,
    current_users INTEGER DEFAULT 0,
    bandwidth_capacity BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX idx_hotspots_status ON hotspot(status);

CREATE TABLE sessions (
    id UUID PRIMARY KEY,
    session_uuid UUID NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    hotspot_id UUID,
    radius_session_id VARCHAR(100),
    mac_address VARCHAR(32),
    ip_address VARCHAR(45),
    package_id UUID,
    download_speed INTEGER,
    upload_speed INTEGER,
    started_at TIMESTAMPTZ NOT NULL,
    last_accounting_update_at TIMESTAMPTZ,
    ended_at TIMESTAMPTZ,
    duration_seconds BIGINT,
    bytes_uploaded BIGINT DEFAULT 0,
    bytes_downloaded BIGINT DEFAULT 0,
    termination_reason VARCHAR(120),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ
);

CREATE INDEX idx_sessions_customer_id ON sessions(customer_id);
CREATE INDEX idx_sessions_status ON sessions(status);
CREATE INDEX idx_sessions_hotspot_id ON sessions(hotspot_id);
CREATE INDEX idx_sessions_started_at ON sessions(started_at);

CREATE TABLE payments (
    id UUID PRIMARY KEY,
    reference VARCHAR(100) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    package_id UUID,
    amount NUMERIC(19,2) NOT NULL,
    currency CHAR(3) NOT NULL,
    provider VARCHAR(64),
    provider_reference VARCHAR(128),
    status VARCHAR(32) NOT NULL,
    initiated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at TIMESTAMPTZ,
    failure_reason VARCHAR(255)
);

CREATE INDEX idx_payments_reference ON payments(reference);
CREATE INDEX idx_payments_customer_id ON payments(customer_id);
CREATE INDEX idx_payments_status ON payments(status);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    admin_user_id UUID,
    action VARCHAR(200) NOT NULL,
    entity_type VARCHAR(100),
    entity_id VARCHAR(100),
    previous_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE roles (
    id UUID PRIMARY KEY,
    name VARCHAR(64) NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE admin_users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE customer_entitlements (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    package_id UUID,
    original_duration_minutes INTEGER NOT NULL,
    remaining_duration_minutes INTEGER NOT NULL,
    source_type VARCHAR(32) NOT NULL,
    source_reference VARCHAR(100),
    activated_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ,
    status VARCHAR(32) NOT NULL
);

CREATE INDEX idx_entitlements_customer_id ON customer_entitlements(customer_id);
CREATE INDEX idx_entitlements_status ON customer_entitlements(status);

CREATE TABLE devices (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    mac_address VARCHAR(32) NOT NULL,
    device_name VARCHAR(120),
    device_type VARCHAR(64),
    first_seen TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_seen TIMESTAMPTZ,
    status VARCHAR(32) NOT NULL
);

CREATE TABLE network_events (
    id UUID PRIMARY KEY,
    hotspot_id UUID,
    event_type VARCHAR(100) NOT NULL,
    severity VARCHAR(20),
    message TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE support_tickets (
    id UUID PRIMARY KEY,
    customer_id UUID,
    subject VARCHAR(255),
    message TEXT,
    status VARCHAR(32),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    customer_id UUID,
    message TEXT NOT NULL,
    read_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE radius_accounts (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    hotspot_id UUID,
    username VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255),
    status VARCHAR(32) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE payment_events (
    id UUID PRIMARY KEY,
    payment_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload TEXT,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE voucher_batches (
    id UUID PRIMARY KEY,
    code_prefix VARCHAR(30),
    count INTEGER NOT NULL,
    package_id UUID,
    created_by VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO roles (id, name) VALUES
    ('11111111-1111-1111-1111-111111111111', 'SUPER_ADMIN'),
    ('22222222-2222-2222-2222-222222222222', 'NETWORK_ADMIN'),
    ('33333333-3333-3333-3333-333333333333', 'SUPPORT_AGENT'),
    ('44444444-4444-4444-4444-444444444444', 'FINANCE_ADMIN'),
    ('55555555-5555-5555-5555-555555555555', 'REPORT_VIEWER');

INSERT INTO packages (id, name, duration_minutes, download_speed_mbps, upload_speed_mbps, price, currency, description, created_at, updated_at, status)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '30 Min @ 5 Mbps', 30, 5, 2, 60.00, 'ZAR', '30 minutes with 5Mbps package', now(), now(), 'ACTIVE'),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '1 Hour @ 10 Mbps', 60, 10, 5, 120.00, 'ZAR', '1 hour with 10Mbps package', now(), now(), 'ACTIVE'),
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '1 Hour @ 25 Mbps', 60, 25, 10, 180.00, 'ZAR', '1 hour with 25Mbps package', now(), now(), 'ACTIVE'),
    ('dddddddd-dddd-dddd-dddd-dddddddddddd', '1 Hour @ 50 Mbps', 60, 50, 20, 260.00, 'ZAR', '1 hour with 50Mbps package', now(), now(), 'ACTIVE'),
    ('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '3 Hours @ 25 Mbps', 180, 25, 10, 480.00, 'ZAR', '3 hours with 25Mbps package', now(), now(), 'ACTIVE'),
    ('ffffffff-ffff-ffff-ffff-ffffffffffff', '24 Hours @ 25 Mbps', 1440, 25, 10, 680.00, 'ZAR', '24 hours with 25Mbps package', now(), now(), 'ACTIVE');

INSERT INTO hotspot (id, uuid, name, code, location_name, address, latitude, longitude, router_ip, router_api_port, radius_server, radius_secret_encrypted, router_username_encrypted, router_password_encrypted, status, last_heartbeat, max_users, current_users, bandwidth_capacity, created_at, updated_at)
VALUES
    ('12121212-1212-1212-1212-121212121212', gen_random_uuid(), 'Hotspot 1', 'HS-001', 'Johannesburg CBD', '14 Main Road', -26.2041, 28.0473, '10.0.0.11', 8728, 'radius.local', 'enc-secret', 'enc-user', 'enc-pass', 'ONLINE', now(), 200, 12, 1000, now(), now()),
    ('13131313-1313-1313-1313-131313131313', gen_random_uuid(), 'Hotspot 2', 'HS-002', 'Sandton', '45 Rivonia Road', -26.1076, 28.0567, '10.0.0.12', 8728, 'radius.local', 'enc-secret', 'enc-user', 'enc-pass', 'ONLINE', now(), 200, 9, 1000, now(), now()),
    ('14141414-1414-1414-1414-141414141414', gen_random_uuid(), 'Hotspot 3', 'HS-003', 'Cape Town Waterfront', '2 Dock Road', -33.9189, 18.4233, '10.0.0.13', 8728, 'radius.local', 'enc-secret', 'enc-user', 'enc-pass', 'ONLINE', now(), 200, 7, 1000, now(), now());

INSERT INTO admin_users (id, username, email, password_hash, status, created_at)
VALUES ('99999999-9999-9999-9999-999999999999', 'admin', 'admin@yourwifi.local', '$2a$10$Q9dFx4G2D3xqD79pzfKZ0u7Z7sL3GQORv5fgW0H7eJb5B5O0hEo1O', 'ACTIVE', now());

