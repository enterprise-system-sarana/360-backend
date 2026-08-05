-- ============================================================
-- V1__initial_schema.sql
-- Initial database schema for Sarana Restaurant System
-- ============================================================

-- 1. Brand Table
CREATE TABLE tbl_brands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    image_url VARCHAR(500) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

-- 2. Category Table
CREATE TABLE tbl_category (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE,
    image_url VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

-- 3. Model Table (depends on tbl_brands, tbl_category)
CREATE TABLE tbl_model (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    brand_id BIGINT REFERENCES tbl_brands(id),
    category_id BIGINT REFERENCES tbl_category(id),
    status VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

-- 4. Product Table (depends on tbl_model)
CREATE TABLE tbl_product (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50),
    noted VARCHAR(255),
    image_url VARCHAR(255),
    status VARCHAR(50),
    model_id BIGINT REFERENCES tbl_model(id),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

-- 5. Product Variant Table (depends on tbl_product)
CREATE TABLE tbl_product_variant (
    variant_id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    cost_price NUMERIC(38,2),
    selling_price NUMERIC(38,2),
    image_url VARCHAR(255),
    status VARCHAR(50),
    product_id BIGINT REFERENCES tbl_product(id)
);

-- 6. Variant Types Table
CREATE TABLE tbl_variant_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255),
    name VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

-- 7. Variant Values Table (depends on tbl_variant_types)
CREATE TABLE tbl_variant_values (
    value_id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL,
    variant_type_id BIGINT NOT NULL REFERENCES tbl_variant_types(id),
    status VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

-- 8. Product Variant <-> Variant Value Join Table (Many-to-Many)
CREATE TABLE tbl_product_variant_value (
    variant_id BIGINT NOT NULL REFERENCES tbl_product_variant(variant_id),
    value_id BIGINT NOT NULL REFERENCES tbl_variant_values(value_id),
    PRIMARY KEY (variant_id, value_id)
);

-- 9. Customer Table
CREATE TABLE tbl_customer (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(50) NOT NULL UNIQUE,
    phone VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255),
    note VARCHAR(255),
    status VARCHAR(255)
);

-- 10. Banks Table
CREATE TABLE banks (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    account_name VARCHAR(50) NOT NULL UNIQUE,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    opending_banlance NUMERIC,
    current_balance NUMERIC,
    status VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

-- 11. Stores Table
CREATE TABLE tbl_store (
    id BIGSERIAL PRIMARY KEY,
    st_name VARCHAR(50) NOT NULL,
    st_code VARCHAR(20) NOT NULL,
    st_logo VARCHAR(500),
    st_email VARCHAR(100),
    st_phone VARCHAR(25) NOT NULL,
    st_address1 VARCHAR(500),
    st_address2 VARCHAR(500),
    st_city VARCHAR(50),
    st_state VARCHAR(50),
    st_postal_code VARCHAR(50),
    st_country VARCHAR(50),
    currency_code VARCHAR(50),
    st_receipt_header TEXT,
    st_receipt_footer TEXT,
    status VARCHAR(255)
);

-- 12. Suppliers Table
CREATE TABLE tbl_suppliers (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(50),
    email VARCHAR(255),
    address VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    note VARCHAR(255),
    status VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

-- 13. Audit Log Table
CREATE TABLE tbl_audit_log (
    id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    record_id VARCHAR(255),
    changed_by VARCHAR(255),
    changed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    changes TEXT
);

-- 14. Permission Groups Table
CREATE TABLE tbl_permission_groups (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(255)
);
CREATE INDEX idx_perm_group_code ON tbl_permission_groups(code);

-- 15. Permissions Table (depends on tbl_permission_groups)
CREATE TABLE tbl_permissions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(120) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    group_id BIGINT NOT NULL REFERENCES tbl_permission_groups(id)
);
CREATE INDEX idx_permission_code ON tbl_permissions(code);
CREATE INDEX idx_permission_group ON tbl_permissions(group_id);

-- 16. Role Table
CREATE TABLE tbl_role (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);
CREATE INDEX idx_role_code ON tbl_role(code);

-- 17. Role Permissions Join Table (depends on tbl_role, tbl_permissions)
CREATE TABLE tbl_role_permissions (
    role_id BIGINT NOT NULL REFERENCES tbl_role(id),
    permission_id BIGINT NOT NULL REFERENCES tbl_permissions(id),
    CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id)
);

-- 18. Users Table
CREATE TABLE tbl_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(25),
    password_hash VARCHAR(255) NOT NULL,
    is_active VARCHAR(50),
    is_verified BOOLEAN DEFAULT FALSE,
    is_locked BOOLEAN DEFAULT FALSE,
    failed_login_attempts INTEGER DEFAULT 0,
    last_login_at TIMESTAMP WITHOUT TIME ZONE,
    password_changed_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);
CREATE INDEX idx_users_active ON tbl_users(is_active, is_locked, deleted_at);

-- 19. User Roles Join Table (depends on tbl_users, tbl_role)
CREATE TABLE tbl_user_roles (
    user_id BIGINT NOT NULL REFERENCES tbl_users(id),
    role_id BIGINT NOT NULL REFERENCES tbl_role(id),
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
);

-- 20. Refresh Tokens Table (depends on tbl_users)
CREATE TABLE tbl_refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES tbl_users(id),
    token VARCHAR(255) NOT NULL UNIQUE,
    device_id VARCHAR(255),
    device_name VARCHAR(255),
    ip_address VARCHAR(255),
    user_agent VARCHAR(255),
    is_revoked BOOLEAN DEFAULT FALSE,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    revoked_at TIMESTAMP WITHOUT TIME ZONE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);
CREATE INDEX idx_refresh_user ON tbl_refresh_tokens(user_id);
CREATE INDEX idx_refresh_expires ON tbl_refresh_tokens(expires_at);
CREATE INDEX idx_refresh_revoked ON tbl_refresh_tokens(is_revoked);
CREATE INDEX idx_refresh_user_device ON tbl_refresh_tokens(user_id, device_id);

-- 21. Verification Tokens Table (depends on tbl_users)
CREATE TABLE tbl_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES tbl_users(id),
    type VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);
CREATE INDEX idx_verif_token ON tbl_verification_tokens(token);
CREATE INDEX idx_verif_user_type ON tbl_verification_tokens(user_id, type);
