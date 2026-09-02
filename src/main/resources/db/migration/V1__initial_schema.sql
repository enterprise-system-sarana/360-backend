-- ============================================================
-- V1__initial_schema.sql
-- Complete database schema for Sarana Restaurant / 360 System (PostgreSQL)
-- Generated to match all JPA @Entity models
-- ============================================================

-- 1. Brand Table
CREATE TABLE tbl_brands (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    image_url VARCHAR(500),
    status VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_brand_name ON tbl_brands(name);

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

CREATE INDEX idx_category_name ON tbl_category(name);
CREATE INDEX idx_category_code ON tbl_category(code);

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

CREATE INDEX idx_model_name ON tbl_model(name);
CREATE INDEX idx_model_brand ON tbl_model(brand_id);
CREATE INDEX idx_model_category ON tbl_model(category_id);

-- 4. Product Table (depends on tbl_model)
CREATE TABLE tbl_product (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50),
    noted VARCHAR(255),
    image_url VARCHAR(255),
    cost_price NUMERIC(25, 4) NOT NULL,
    sale_price NUMERIC(25, 4) NOT NULL,
    reorder_level INTEGER,
    status VARCHAR(50),
    model_id BIGINT REFERENCES tbl_model(id),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_product_code ON tbl_product(code);
CREATE INDEX idx_product_model ON tbl_product(model_id);

-- 5. Variant Types Table
CREATE TABLE tbl_variant_types (
    variant_type_id BIGSERIAL PRIMARY KEY,
    code VARCHAR(255),
    name VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_variant_type_name ON tbl_variant_types(name);
CREATE INDEX idx_variant_type_code ON tbl_variant_types(code);

-- 6. Variant Values Table (depends on tbl_variant_types)
CREATE TABLE tbl_variant_values (
    value_id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(50) NOT NULL,
    variant_type_id BIGINT NOT NULL REFERENCES tbl_variant_types(variant_type_id),
    status VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_variant_value_name ON tbl_variant_values(name);
CREATE INDEX idx_variant_value_code ON tbl_variant_values(code);
CREATE INDEX idx_variant_value_type ON tbl_variant_values(variant_type_id);

-- 7. Product Variant Values (Many-to-Many Join Table)
CREATE TABLE tbl_product_variant_values (
    product_id BIGINT NOT NULL REFERENCES tbl_product(id) ON DELETE CASCADE,
    variant_value_id BIGINT NOT NULL REFERENCES tbl_variant_values(value_id) ON DELETE CASCADE,
    PRIMARY KEY (product_id, variant_value_id)
);

CREATE INDEX idx_product_variant_values_variant_value ON tbl_product_variant_values(variant_value_id);

-- 8. Stores Table
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
    status VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_store_name ON tbl_store(st_name);
CREATE INDEX idx_store_code ON tbl_store(st_code);

-- 9. Stock Table (depends on tbl_product, tbl_store)
CREATE TABLE tbl_stock (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES tbl_product(id),
    store_id BIGINT NOT NULL REFERENCES tbl_store(id),
    variant_value_id BIGINT,
    quantity NUMERIC(15, 4) NOT NULL,
    alert_quantity NUMERIC(15, 4) DEFAULT 0.0000,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100),
    CONSTRAINT uk_stock_product_variant_store UNIQUE (product_id, store_id)
);

CREATE INDEX idx_stock_product ON tbl_stock(product_id);
CREATE INDEX idx_stock_store ON tbl_stock(store_id);

-- 10. Suppliers Table
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
    status VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_supplier_name ON tbl_suppliers(name);
CREATE INDEX idx_supplier_code ON tbl_suppliers(code);
CREATE INDEX idx_supplier_phone ON tbl_suppliers(phone);

-- 11. Customer Table
CREATE TABLE tbl_customer (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50),
    code VARCHAR(50),
    phone VARCHAR(50),
    email VARCHAR(255),
    note VARCHAR(255),
    status VARCHAR(50)
);

CREATE INDEX idx_customer_name ON tbl_customer(name);
CREATE INDEX idx_customer_code ON tbl_customer(code);
CREATE INDEX idx_customer_phone ON tbl_customer(phone);

-- 12. Banks Table
CREATE TABLE tbl_bank (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    account_name VARCHAR(50) NOT NULL UNIQUE,
    account_number VARCHAR(50) NOT NULL UNIQUE,
    opening_balance NUMERIC(25, 4),
    current_balance NUMERIC(25, 4),
    status VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_bank_name ON tbl_bank(name);
CREATE INDEX idx_bank_account_name ON tbl_bank(account_name);
CREATE INDEX idx_bank_account_number ON tbl_bank(account_number);

-- Currency Table
CREATE TABLE tbl_currency (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL UNIQUE,
    operation VARCHAR(20),
    rate DOUBLE PRECISION,
    symbol VARCHAR(50),
    status VARCHAR(50),
    created_at TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP,
    deleted_by VARCHAR(100)
);

-- 13. Expense Types Table
CREATE TABLE tbl_expense_types (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    status VARCHAR(50),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

-- 14. Expenses Table (depends on tbl_store, tbl_bank, tbl_expense_types)
CREATE TABLE tbl_expenses (
    id BIGSERIAL PRIMARY KEY,
    reference VARCHAR(50) NOT NULL,
    amount NUMERIC(25, 4) NOT NULL,
    note VARCHAR(1000),
    store_id BIGINT REFERENCES tbl_store(id),
    status VARCHAR(50),
    bank_id BIGINT REFERENCES tbl_bank(id),
    expense_type_id BIGINT REFERENCES tbl_expense_types(id),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_expenses_store ON tbl_expenses(store_id);
CREATE INDEX idx_expenses_bank ON tbl_expenses(bank_id);
CREATE INDEX idx_expenses_type ON tbl_expenses(expense_type_id);

-- 15. Bank Transactions Table
CREATE TABLE tbl_bank_transactions (
    id BIGSERIAL PRIMARY KEY,
    expense_id BIGINT,
    amount NUMERIC(15, 2) NOT NULL,
    transaction_reference VARCHAR(255) NOT NULL UNIQUE,
    transaction_type VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    transaction_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

-- 16. Purchase Table (depends on tbl_suppliers, tbl_store)
CREATE TABLE tbl_purchases (
    id BIGSERIAL PRIMARY KEY,
    reference_no VARCHAR(50),
    supplier_id BIGINT REFERENCES tbl_suppliers(id),
    store_id BIGINT REFERENCES tbl_store(id),
    bank_id BIGINT REFERENCES tbl_bank(id),
    purchase_date DATE,
    total NUMERIC(25, 4),
    discount NUMERIC(25, 4),
    grand_total NUMERIC(25, 4),
    paid_amount NUMERIC(25, 4),
    due_amount NUMERIC(25, 4),
    payment_status VARCHAR(50),
    status VARCHAR(50),
    note TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_purchases_supplier ON tbl_purchases(supplier_id);
CREATE INDEX idx_purchases_store ON tbl_purchases(store_id);
CREATE INDEX idx_purchase_bank ON tbl_purchases(bank_id);

-- 17. Purchase Items Table (depends on tbl_purchases, tbl_product)
CREATE TABLE tbl_purchase_items (
    id BIGSERIAL PRIMARY KEY,
    purchase_id BIGINT NOT NULL REFERENCES tbl_purchases(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES tbl_product(id),
    quantity NUMERIC(15, 4) NOT NULL,
    cost NUMERIC(25, 4) NOT NULL,
    price NUMERIC(25, 4),
    subtotal NUMERIC(25, 4)
);

CREATE INDEX idx_purchase_items_purchase ON tbl_purchase_items(purchase_id);
CREATE INDEX idx_purchase_items_product ON tbl_purchase_items(product_id);

-- 18. Product Serials Table (depends on tbl_product, tbl_store, tbl_purchases, tbl_purchase_items)
CREATE TABLE tbl_product_serials (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES tbl_product(id),
    barcode VARCHAR(100) UNIQUE,
    price NUMERIC(25, 4) NOT NULL,
    cost NUMERIC(25, 4),
    quantity NUMERIC(15, 4) DEFAULT 0.0000,
    store_id BIGINT REFERENCES tbl_store(id),
    purchase_id BIGINT REFERENCES tbl_purchases(id),
    purchase_item_id BIGINT REFERENCES tbl_purchase_items(id),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_product ON tbl_product_serials(product_id);
CREATE INDEX idx_product_serials_store ON tbl_product_serials(store_id);
CREATE INDEX idx_product_serials_purchase ON tbl_product_serials(purchase_id);

-- 19. Inventory Transactions Table (depends on tbl_product, tbl_store)
CREATE TABLE tbl_inventory_transactions (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES tbl_product(id),
    product_variant_id BIGINT,
    store_id BIGINT NOT NULL REFERENCES tbl_store(id),
    quantity NUMERIC(15, 4) NOT NULL,
    type VARCHAR(50) NOT NULL,
    reference_id BIGINT,
    transaction_date TIMESTAMP WITHOUT TIME ZONE,
    notes TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_inv_product ON tbl_inventory_transactions(product_id);
CREATE INDEX idx_inv_store ON tbl_inventory_transactions(store_id);

-- 20. Sales Table (depends on tbl_store, tbl_customer)
CREATE TABLE tbl_sales (
    id BIGSERIAL PRIMARY KEY,
    date TIMESTAMP WITHOUT TIME ZONE,
    reference VARCHAR(255),
    no INTEGER,
    store_id BIGINT REFERENCES tbl_store(id),
    customer_id BIGINT REFERENCES tbl_customer(id),
    bank_id BIGINT REFERENCES tbl_bank(id),
    grand_total DOUBLE PRECISION,
    discount DOUBLE PRECISION,
    sale_status VARCHAR(50),
    payment_status VARCHAR(50),
    paid_amount DOUBLE PRECISION,
    return_amount DOUBLE PRECISION,
    noted VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_sales_store ON tbl_sales(store_id);
CREATE INDEX idx_sales_customer ON tbl_sales(customer_id);

-- 21. Sale Items Table (depends on tbl_sales, tbl_product)
CREATE TABLE tbl_sale_items (
    id BIGSERIAL PRIMARY KEY,
    sales_id BIGINT NOT NULL REFERENCES tbl_sales(id) ON DELETE CASCADE,
    product_id BIGINT REFERENCES tbl_product(id),
    quantity NUMERIC(15, 4) NOT NULL,
    price NUMERIC(25, 4) NOT NULL,
    item_discount NUMERIC(25, 4),
    sub_total NUMERIC(25, 4)
);

CREATE INDEX idx_sale_items_sales ON tbl_sale_items(sales_id);
CREATE INDEX idx_sale_items_product ON tbl_sale_items(product_id);

-- 22. Sale Item Serials (ElementCollection join table)
CREATE TABLE tbl_sale_item_serials (
    sale_item_id BIGINT NOT NULL REFERENCES tbl_sale_items(id) ON DELETE CASCADE,
    product_serial_id BIGINT
);

CREATE INDEX idx_sale_item_serials_sale_item ON tbl_sale_item_serials(sale_item_id);

-- 23. Quote Table (depends on tbl_customer)
CREATE TABLE tbl_quote (
    quote_id BIGSERIAL PRIMARY KEY,
    date TIMESTAMP WITHOUT TIME ZONE,
    reference VARCHAR(100) NOT NULL UNIQUE,
    no VARCHAR(100) NOT NULL UNIQUE,
    customer_id BIGINT NOT NULL REFERENCES tbl_customer(id),
    grand_total NUMERIC(19, 2),
    discount NUMERIC(19, 2),
    status VARCHAR(50),
    status_payment VARCHAR(50),
    paid_amount NUMERIC(19, 2),
    return_amount NUMERIC(19, 2),
    noted VARCHAR(255)
);

CREATE INDEX idx_quote_customer ON tbl_quote(customer_id);
CREATE INDEX idx_quote_reference ON tbl_quote(reference);

-- 24. Quote Items Table (depends on tbl_quote, tbl_product)
CREATE TABLE tbl_quote_items (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL REFERENCES tbl_product(id),
    quote_id BIGINT REFERENCES tbl_quote(quote_id) ON DELETE CASCADE,
    unit_quantity NUMERIC(19, 2),
    price NUMERIC(19, 2),
    discount_item NUMERIC(19, 2),
    subtotal NUMERIC(19, 2)
);

CREATE INDEX idx_quote_items_product ON tbl_quote_items(product_id);
CREATE INDEX idx_quote_items_quote ON tbl_quote_items(quote_id);

-- 25. Permission Groups Table
CREATE TABLE tbl_permission_groups (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(255)
);

CREATE INDEX idx_perm_group_code ON tbl_permission_groups(code);

-- 26. Permissions Table (depends on tbl_permission_groups)
CREATE TABLE tbl_permissions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(120) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    group_id BIGINT NOT NULL REFERENCES tbl_permission_groups(id) ON DELETE CASCADE
);

CREATE INDEX idx_permission_code ON tbl_permissions(code);
CREATE INDEX idx_permission_group ON tbl_permissions(group_id);

-- 27. Role Table
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

-- 28. Role Permissions Join Table
CREATE TABLE tbl_role_permissions (
    role_id BIGINT NOT NULL REFERENCES tbl_role(id) ON DELETE CASCADE,
    permission_id BIGINT NOT NULL REFERENCES tbl_permissions(id) ON DELETE CASCADE,
    CONSTRAINT uk_role_permission UNIQUE (role_id, permission_id)
);

-- 29. Users Table
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

-- 30. User Roles Join Table
CREATE TABLE tbl_user_roles (
    user_id BIGINT NOT NULL REFERENCES tbl_users(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES tbl_role(id) ON DELETE CASCADE,
    CONSTRAINT uk_user_role UNIQUE (user_id, role_id)
);

-- 31. Payments Table (depends on tbl_sales, tbl_bank, tbl_users)
CREATE TABLE tbl_payment (
    id BIGSERIAL PRIMARY KEY,
    payment_no VARCHAR(100),
    sale_id BIGINT REFERENCES tbl_sales(id),
    payment_method VARCHAR(255),
    bank_id BIGINT REFERENCES tbl_bank(id),
    amount NUMERIC(38, 2),
    transaction_no VARCHAR(255),
    status VARCHAR(50),
    payment_date TIMESTAMP WITHOUT TIME ZONE,
    user_id BIGINT REFERENCES tbl_users(id),
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(100),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(100),
    deleted_at TIMESTAMP WITHOUT TIME ZONE,
    deleted_by VARCHAR(100)
);

CREATE INDEX idx_payments_sale_id ON tbl_payment(sale_id);
CREATE INDEX idx_payments_payment_date ON tbl_payment(payment_date);
CREATE INDEX idx_payments_transaction_no ON tbl_payment(transaction_no);
CREATE INDEX idx_payments_bank_id ON tbl_payment(bank_id);

-- 32. Refresh Tokens Table (depends on tbl_users)
CREATE TABLE tbl_refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES tbl_users(id) ON DELETE CASCADE,
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

-- 33. Verification Tokens Table (depends on tbl_users)
CREATE TABLE tbl_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES tbl_users(id) ON DELETE CASCADE,
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

-- 34. OTP Verification Table
CREATE TABLE tbl_otp_verification (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    otp_code VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

-- 35. Audit Log Table
CREATE TABLE tbl_audit_log (
    id BIGSERIAL PRIMARY KEY,
    table_name VARCHAR(255) NOT NULL,
    action VARCHAR(255) NOT NULL,
    record_id VARCHAR(255),
    changed_by VARCHAR(255),
    changed_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    changes TEXT
);

CREATE INDEX idx_audit_table_record ON tbl_audit_log(table_name, record_id);