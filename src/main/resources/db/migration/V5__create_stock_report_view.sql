-- ============================================================
-- V5__create_stock_report_view.sql
-- High-Performance Database Reporting Views & Comprehensive Indexes
-- Sarana Restaurant / 360 System
-- Includes:
--   1. view_stock_report (Stock Levels, Product & Model Hierarchy, Valuations, Status & Serials)
--   2. view_stock_movement_report (Inventory Audit Transactions with Catalog Details)
--   3. view_sales_items_report (Enhanced with Model, Brand, Category hierarchy)
--   4. view_purchase_items_report (Enhanced with Model, Brand, Category hierarchy)
--   5. Comprehensive Performance Indexes on All Report Tables
-- ============================================================

-- Drop views first to avoid PostgreSQL restriction: "cannot change name of view column"
DROP VIEW IF EXISTS view_sales_items_report CASCADE;
DROP VIEW IF EXISTS view_purchase_items_report CASCADE;
DROP VIEW IF EXISTS view_stock_movement_report CASCADE;
DROP VIEW IF EXISTS view_stock_report CASCADE;

-- 1. High-Performance Stock Report View
CREATE OR REPLACE VIEW view_stock_report AS
SELECT
    s.id AS stock_id,
    s.product_id,
    p.code AS product_code,
    p.name AS product_name,
    p.image_url,
    p.status AS product_status,
    p.has_serial,
    m.id AS model_id,
    m.name AS model_name,
    b.id AS brand_id,
    b.name AS brand_name,
    c.id AS category_id,
    c.name AS category_name,
    s.store_id,
    st.st_name AS store_name,
    st.st_code AS store_code,
    s.variant_value_id,
    vv.name AS variant_value_name,
    COALESCE(s.quantity, 0) AS current_quantity,
    COALESCE(s.alert_quantity, p.reorder_level, 0) AS alert_quantity,
    COALESCE(p.reorder_level, 0) AS reorder_level,
    COALESCE(p.cost_price, 0) AS cost_price,
    COALESCE(p.sale_price, 0) AS sale_price,
    ROUND((COALESCE(s.quantity, 0) * COALESCE(p.cost_price, 0))::numeric, 4) AS total_cost_value,
    ROUND((COALESCE(s.quantity, 0) * COALESCE(p.sale_price, 0))::numeric, 4) AS total_sale_value,
    ROUND((COALESCE(s.quantity, 0) * (COALESCE(p.sale_price, 0) - COALESCE(p.cost_price, 0)))::numeric, 4) AS potential_profit,
    CASE
        WHEN COALESCE(p.cost_price, 0) > 0
        THEN ROUND((((COALESCE(p.sale_price, 0) - COALESCE(p.cost_price, 0)) / p.cost_price) * 100)::numeric, 2)
        ELSE 0.00
    END AS margin_percentage,
    CASE
        WHEN COALESCE(s.quantity, 0) <= 0 THEN 'OUT_OF_STOCK'
        WHEN COALESCE(s.quantity, 0) <= COALESCE(s.alert_quantity, p.reorder_level, 0) THEN 'LOW_STOCK'
        ELSE 'IN_STOCK'
    END AS stock_status,
    COALESCE(serials.available_serials_count, 0) AS available_serials_count,
    s.created_at,
    s.updated_at
FROM tbl_stock s
JOIN tbl_product p ON p.id = s.product_id
JOIN tbl_store st ON st.id = s.store_id
LEFT JOIN tbl_model m ON m.id = p.model_id AND m.deleted_at IS NULL
LEFT JOIN tbl_brands b ON b.id = m.brand_id AND b.deleted_at IS NULL
LEFT JOIN tbl_category c ON c.id = m.category_id AND c.deleted_at IS NULL
LEFT JOIN tbl_variant_values vv ON vv.value_id = s.variant_value_id AND vv.deleted_at IS NULL
LEFT JOIN (
    SELECT
        ps.product_id,
        ps.store_id,
        COUNT(ps.id) AS available_serials_count
    FROM tbl_product_serials ps
    WHERE UPPER(COALESCE(ps.status, '')) IN ('AVAILABLE', 'IN_STOCK', 'ACTIVE')
      AND ps.deleted_at IS NULL
    GROUP BY ps.product_id, ps.store_id
) serials ON serials.product_id = s.product_id AND serials.store_id = s.store_id
WHERE s.deleted_at IS NULL
  AND p.deleted_at IS NULL
  AND st.deleted_at IS NULL;

-- 2. High-Performance Stock Movement & Inventory Transaction View
CREATE OR REPLACE VIEW view_stock_movement_report AS
SELECT
    tx.id AS transaction_id,
    tx.product_id,
    p.code AS product_code,
    p.name AS product_name,
    p.image_url,
    m.id AS model_id,
    m.name AS model_name,
    b.id AS brand_id,
    b.name AS brand_name,
    c.id AS category_id,
    c.name AS category_name,
    tx.store_id,
    st.st_name AS store_name,
    st.st_code AS store_code,
    tx.quantity,
    tx.type AS transaction_type,
    tx.reference_id,
    tx.transaction_date,
    tx.notes,
    tx.created_at,
    tx.created_by
FROM tbl_inventory_transactions tx
JOIN tbl_product p ON p.id = tx.product_id
JOIN tbl_store st ON st.id = tx.store_id
LEFT JOIN tbl_model m ON m.id = p.model_id AND m.deleted_at IS NULL
LEFT JOIN tbl_brands b ON b.id = m.brand_id AND b.deleted_at IS NULL
LEFT JOIN tbl_category c ON c.id = m.category_id AND c.deleted_at IS NULL
WHERE tx.deleted_at IS NULL
  AND p.deleted_at IS NULL
  AND st.deleted_at IS NULL;

-- 3. Update Sales Item Detail Report View to Include Model, Brand, Category Hierarchy
CREATE OR REPLACE VIEW view_sales_items_report AS
SELECT
    si.id AS item_id,
    s.id AS sale_id,
    s.no AS sale_no,
    s.date AS sale_date,
    s.sale_status,
    s.payment_status,
    s.store_id,
    st.st_name AS store_name,
    s.customer_id,
    cust.name AS customer_name,
    si.product_id,
    p.name AS product_name,
    p.code AS product_code,
    m.id AS model_id,
    m.name AS model_name,
    b.id AS brand_id,
    b.name AS brand_name,
    cat.id AS category_id,
    cat.name AS category_name,
    si.quantity,
    COALESCE(si.returned_quantity, 0) AS returned_quantity,
    si.price AS unit_price,
    COALESCE(si.unit_cost_price, p.cost_price, 0) AS unit_cost,
    COALESCE(si.item_discount, 0) AS item_discount,
    si.sub_total,
    ROUND((si.quantity * COALESCE(si.unit_cost_price, p.cost_price, 0))::numeric, 4) AS total_cost,
    ROUND((si.sub_total - (si.quantity * COALESCE(si.unit_cost_price, p.cost_price, 0)))::numeric, 4) AS total_profit
FROM tbl_sale_items si
JOIN tbl_sales s ON s.id = si.sales_id
LEFT JOIN tbl_store st ON st.id = s.store_id
LEFT JOIN tbl_customer cust ON cust.id = s.customer_id
LEFT JOIN tbl_product p ON p.id = si.product_id
LEFT JOIN tbl_model m ON m.id = p.model_id AND m.deleted_at IS NULL
LEFT JOIN tbl_brands b ON b.id = m.brand_id AND b.deleted_at IS NULL
LEFT JOIN tbl_category cat ON cat.id = m.category_id AND cat.deleted_at IS NULL;

-- 4. Update Purchase Item Detail Report View to Include Model, Brand, Category Hierarchy
CREATE OR REPLACE VIEW view_purchase_items_report AS
SELECT
    pi.id AS item_id,
    p.id AS purchase_id,
    p.reference_no,
    p.purchase_date,
    p.status AS purchase_status,
    p.payment_status,
    p.store_id,
    st.st_name AS store_name,
    p.supplier_id,
    sup.name AS supplier_name,
    pi.product_id,
    prod.name AS product_name,
    prod.code AS product_code,
    m.id AS model_id,
    m.name AS model_name,
    b.id AS brand_id,
    b.name AS brand_name,
    c.id AS category_id,
    c.name AS category_name,
    pi.quantity,
    pi.cost AS unit_cost,
    pi.price AS unit_price,
    COALESCE(pi.subtotal, ROUND((pi.quantity * pi.cost)::numeric, 4)) AS subtotal
FROM tbl_purchase_items pi
JOIN tbl_purchases p ON p.id = pi.purchase_id
LEFT JOIN tbl_store st ON st.id = p.store_id
LEFT JOIN tbl_suppliers sup ON sup.id = p.supplier_id
LEFT JOIN tbl_product prod ON prod.id = pi.product_id
LEFT JOIN tbl_model m ON m.id = prod.model_id AND m.deleted_at IS NULL
LEFT JOIN tbl_brands b ON b.id = m.brand_id AND b.deleted_at IS NULL
LEFT JOIN tbl_category c ON c.id = m.category_id AND c.deleted_at IS NULL;

-- ============================================================
-- 5. Comprehensive Performance Indexes for All Report Tables
-- ============================================================

-- Stock Reporting & Catalog Performance Indexes
CREATE INDEX IF NOT EXISTS idx_stock_store_product ON tbl_stock(store_id, product_id);
CREATE INDEX IF NOT EXISTS idx_stock_quantity ON tbl_stock(quantity);
CREATE INDEX IF NOT EXISTS idx_stock_alert_quantity ON tbl_stock(alert_quantity);
CREATE INDEX IF NOT EXISTS idx_stock_deleted_at ON tbl_stock(deleted_at);

CREATE INDEX IF NOT EXISTS idx_product_model_deleted ON tbl_product(model_id, deleted_at);
CREATE INDEX IF NOT EXISTS idx_product_status ON tbl_product(status);
CREATE INDEX IF NOT EXISTS idx_product_deleted_at ON tbl_product(deleted_at);

CREATE INDEX IF NOT EXISTS idx_model_brand_category ON tbl_model(brand_id, category_id, deleted_at);
CREATE INDEX IF NOT EXISTS idx_model_deleted_at ON tbl_model(deleted_at);

CREATE INDEX IF NOT EXISTS idx_brands_deleted_at ON tbl_brands(deleted_at);
CREATE INDEX IF NOT EXISTS idx_category_deleted_at ON tbl_category(deleted_at);

CREATE INDEX IF NOT EXISTS idx_product_serials_avail ON tbl_product_serials(product_id, store_id, status, deleted_at);
CREATE INDEX IF NOT EXISTS idx_product_serials_deleted_at ON tbl_product_serials(deleted_at);

-- Inventory Transactions & Stock Movement Indexes
CREATE INDEX IF NOT EXISTS idx_inv_tx_store_product_date ON tbl_inventory_transactions(store_id, product_id, transaction_date);
CREATE INDEX IF NOT EXISTS idx_inv_tx_type ON tbl_inventory_transactions(type);
CREATE INDEX IF NOT EXISTS idx_inv_tx_date ON tbl_inventory_transactions(transaction_date);
CREATE INDEX IF NOT EXISTS idx_inv_tx_deleted_at ON tbl_inventory_transactions(deleted_at);

-- Sales Reporting Performance Indexes
CREATE INDEX IF NOT EXISTS idx_sales_report_filter ON tbl_sales(store_id, customer_id, date, sale_status, payment_status);
CREATE INDEX IF NOT EXISTS idx_sales_deleted_at ON tbl_sales(deleted_at);
CREATE INDEX IF NOT EXISTS idx_sale_items_composite ON tbl_sale_items(sales_id, product_id);

-- Purchase Reporting Performance Indexes
CREATE INDEX IF NOT EXISTS idx_purchases_report_filter ON tbl_purchases(store_id, supplier_id, purchase_date, status, payment_status);
CREATE INDEX IF NOT EXISTS idx_purchases_date ON tbl_purchases(purchase_date);
CREATE INDEX IF NOT EXISTS idx_purchases_status ON tbl_purchases(status);
CREATE INDEX IF NOT EXISTS idx_purchases_payment_status ON tbl_purchases(payment_status);
CREATE INDEX IF NOT EXISTS idx_purchases_deleted_at ON tbl_purchases(deleted_at);
CREATE INDEX IF NOT EXISTS idx_purchase_items_composite ON tbl_purchase_items(purchase_id, product_id);

-- Payment Reporting Performance Indexes
CREATE INDEX IF NOT EXISTS idx_payment_report_sale ON tbl_payment(sale_id, payment_date, status);
CREATE INDEX IF NOT EXISTS idx_payment_report_purchase ON tbl_payment(purchase_id, payment_date, status);
CREATE INDEX IF NOT EXISTS idx_payment_deleted_at ON tbl_payment(deleted_at);
CREATE INDEX IF NOT EXISTS idx_payment_line_method ON tbl_payment_line(payment_id, payment_method);

-- Expense Reporting Performance Indexes
CREATE INDEX IF NOT EXISTS idx_expenses_report_filter ON tbl_expenses(store_id, expense_type_id, status);
CREATE INDEX IF NOT EXISTS idx_expenses_deleted_at ON tbl_expenses(deleted_at);
