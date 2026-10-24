-- ============================================================
-- V2__create_views.sql
-- High-Performance Database Reporting Views for Sarana Restaurant / 360 System
-- Includes:
--   1. view_sales_report (Aggregated Sales & Profit Reporting)
--   2. view_sales_items_report (Detailed Sales Items & Profit Reporting)
--   3. view_purchase_report (Aggregated Purchase Summary Reporting)
--   4. view_purchase_items_report (Detailed Purchase Items Reporting)
--   5. view_sale_payments_report (Sale Payments & Multi-line Method Reporting)
--   6. view_purchase_payments_report (Purchase Payments & Bank Lines Reporting)
-- ============================================================

-- 1. High-Performance Aggregated Sales Report View
CREATE OR REPLACE VIEW view_sales_report AS
SELECT
    s.id AS sale_id,
    s.no AS sale_no,
    s.reference AS reference,
    s.date AS sale_date,
    s.sale_status,
    s.payment_status,
    COALESCE(s.grand_total, 0) AS grand_total,
    COALESCE(s.discount, 0) AS discount,
    COALESCE(s.paid_amount, 0) AS paid_amount,
    COALESCE(s.return_amount, 0) AS return_amount,
    s.store_id,
    st.st_name AS store_name,
    s.customer_id,
    c.name AS customer_name,
    s.user_id,
    u.username AS cashier_name,
    s.cash_register_id,
    COALESCE(items.items_count, 0) AS total_items_count,
    COALESCE(items.total_qty, 0) AS total_quantity,
    COALESCE(items.total_cost, 0) AS total_cost,
    ROUND((COALESCE(s.grand_total, 0) - COALESCE(items.total_cost, 0))::numeric, 4) AS total_profit
FROM tbl_sales s
LEFT JOIN tbl_store st ON st.id = s.store_id
LEFT JOIN tbl_customer c ON c.id = s.customer_id
LEFT JOIN tbl_users u ON u.id = s.user_id
LEFT JOIN (
    SELECT
        si.sales_id,
        COUNT(si.id) AS items_count,
        SUM(si.quantity) AS total_qty,
        SUM(si.quantity * COALESCE(si.unit_cost_price, p.cost_price, 0)) AS total_cost
    FROM tbl_sale_items si
    LEFT JOIN tbl_product p ON p.id = si.product_id
    GROUP BY si.sales_id
) items ON items.sales_id = s.id;

-- 2. High-Performance Sales Item Detail Report View
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
    c.name AS customer_name,
    si.product_id,
    p.name AS product_name,
    p.code AS product_code,
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
LEFT JOIN tbl_customer c ON c.id = s.customer_id
LEFT JOIN tbl_product p ON p.id = si.product_id;

-- 3. High-Performance Aggregated Purchase Report View
CREATE OR REPLACE VIEW view_purchase_report AS
SELECT
    p.id AS purchase_id,
    p.reference_no,
    p.purchase_date,
    p.status,
    p.payment_status,
    COALESCE(p.total, 0) AS total,
    COALESCE(p.discount, 0) AS discount,
    COALESCE(p.grand_total, 0) AS grand_total,
    COALESCE(p.paid_amount, 0) AS paid_amount,
    COALESCE(p.due_amount, 0) AS due_amount,
    p.store_id,
    st.st_name AS store_name,
    p.supplier_id,
    sup.name AS supplier_name,
    p.bank_id,
    b.name AS bank_name,
    COALESCE(items.items_count, 0) AS total_items_count,
    COALESCE(items.total_qty, 0) AS total_quantity
FROM tbl_purchases p
LEFT JOIN tbl_store st ON st.id = p.store_id
LEFT JOIN tbl_suppliers sup ON sup.id = p.supplier_id
LEFT JOIN tbl_bank b ON b.id = p.bank_id
LEFT JOIN (
    SELECT
        pi.purchase_id,
        COUNT(pi.id) AS items_count,
        SUM(pi.quantity) AS total_qty
    FROM tbl_purchase_items pi
    GROUP BY pi.purchase_id
) items ON items.purchase_id = p.id;

-- 4. High-Performance Purchase Item Detail Report View
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
    pi.quantity,
    pi.cost AS unit_cost,
    pi.price AS unit_price,
    COALESCE(pi.subtotal, ROUND((pi.quantity * pi.cost)::numeric, 4)) AS subtotal
FROM tbl_purchase_items pi
JOIN tbl_purchases p ON p.id = pi.purchase_id
LEFT JOIN tbl_store st ON st.id = p.store_id
LEFT JOIN tbl_suppliers sup ON sup.id = p.supplier_id
LEFT JOIN tbl_product prod ON prod.id = pi.product_id;

-- 5. High-Performance Sale Payments Report View
CREATE OR REPLACE VIEW view_sale_payments_report AS
SELECT
    pay.id AS payment_id,
    pay.payment_no,
    pay.payment_date,
    pay.status AS payment_status,
    pay.total_amount,
    pay.received_amount,
    pay.return_amount,
    pay.due_amount,
    pay.note,
    pay.sale_id,
    s.no AS sale_no,
    s.reference AS sale_reference,
    s.store_id,
    st.st_name AS store_name,
    s.customer_id,
    c.name AS customer_name,
    pay.user_id,
    u.username AS cashier_name,
    lines.payment_methods,
    lines.bank_names,
    COALESCE(lines.total_lines_count, 0) AS total_lines_count
FROM tbl_payment pay
JOIN tbl_sales s ON s.id = pay.sale_id
LEFT JOIN tbl_store st ON st.id = s.store_id
LEFT JOIN tbl_customer c ON c.id = s.customer_id
LEFT JOIN tbl_users u ON u.id = pay.user_id
LEFT JOIN (
    SELECT
        pl.payment_id,
        STRING_AGG(DISTINCT pl.payment_method, ', ') AS payment_methods,
        STRING_AGG(DISTINCT b.name, ', ') AS bank_names,
        COUNT(pl.id) AS total_lines_count
    FROM tbl_payment_line pl
    LEFT JOIN tbl_bank b ON b.id = pl.bank_id
    GROUP BY pl.payment_id
) lines ON lines.payment_id = pay.id
WHERE pay.sale_id IS NOT NULL;

-- 6. High-Performance Purchase Payments Report View
CREATE OR REPLACE VIEW view_purchase_payments_report AS
SELECT
    pay.id AS payment_id,
    pay.payment_no,
    pay.payment_date,
    pay.status AS payment_status,
    pay.total_amount,
    pay.received_amount,
    pay.return_amount,
    pay.due_amount,
    pay.note,
    pay.purchase_id,
    p.reference_no AS purchase_reference_no,
    p.store_id,
    st.st_name AS store_name,
    p.supplier_id,
    sup.name AS supplier_name,
    pay.user_id,
    u.username AS created_by_user,
    lines.payment_methods,
    lines.bank_names,
    COALESCE(lines.total_lines_count, 0) AS total_lines_count
FROM tbl_payment pay
JOIN tbl_purchases p ON p.id = pay.purchase_id
LEFT JOIN tbl_store st ON st.id = p.store_id
LEFT JOIN tbl_suppliers sup ON sup.id = p.supplier_id
LEFT JOIN tbl_users u ON u.id = pay.user_id
LEFT JOIN (
    SELECT
        pl.payment_id,
        STRING_AGG(DISTINCT pl.payment_method, ', ') AS payment_methods,
        STRING_AGG(DISTINCT b.name, ', ') AS bank_names,
        COUNT(pl.id) AS total_lines_count
    FROM tbl_payment_line pl
    LEFT JOIN tbl_bank b ON b.id = pl.bank_id
    GROUP BY pl.payment_id
) lines ON lines.payment_id = pay.id
WHERE pay.purchase_id IS NOT NULL;
