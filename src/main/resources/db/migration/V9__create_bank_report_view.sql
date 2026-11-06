-- Migration V9: Create Bank Transactions Report View and Backfill

-- 1. Backfill past sale payments with bank into tbl_bank_transactions if not yet present
INSERT INTO tbl_bank_transactions (
    bank_id, sale_id, amount, transaction_reference, transaction_type, status, transaction_date, description, created_at
)
SELECT 
    pl.bank_id,
    p.sale_id,
    pl.amount,
    COALESCE(NULLIF(TRIM(pl.transaction_no), ''), CONCAT('BACKFILL-SPAY-', p.id, '-', pl.id)),
    'SALE',
    'ACT',
    COALESCE(p.payment_date, CURRENT_TIMESTAMP),
    CONCAT('Sale payment #', COALESCE(s.no, s.reference, CAST(p.sale_id AS VARCHAR))),
    CURRENT_TIMESTAMP
FROM tbl_payment_line pl
JOIN tbl_payment p ON p.id = pl.payment_id
JOIN tbl_sales s ON s.id = p.sale_id
WHERE pl.bank_id IS NOT NULL 
  AND p.sale_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM tbl_bank_transactions bt 
      WHERE bt.transaction_reference = COALESCE(NULLIF(TRIM(pl.transaction_no), ''), CONCAT('BACKFILL-SPAY-', p.id, '-', pl.id))
  );

-- 2. Backfill past purchase payments with bank into tbl_bank_transactions if not yet present
INSERT INTO tbl_bank_transactions (
    bank_id, purchase_id, amount, transaction_reference, transaction_type, status, transaction_date, description, created_at
)
SELECT 
    pl.bank_id,
    p.purchase_id,
    pl.amount,
    COALESCE(NULLIF(TRIM(pl.transaction_no), ''), CONCAT('BACKFILL-PPAY-', p.id, '-', pl.id)),
    'PURCHASE',
    'ACT',
    COALESCE(p.payment_date, CURRENT_TIMESTAMP),
    CONCAT('Purchase payment #', COALESCE(pur.reference_no, CAST(p.purchase_id AS VARCHAR))),
    CURRENT_TIMESTAMP
FROM tbl_payment_line pl
JOIN tbl_payment p ON p.id = pl.payment_id
JOIN tbl_purchases pur ON pur.id = p.purchase_id
WHERE pl.bank_id IS NOT NULL 
  AND p.purchase_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM tbl_bank_transactions bt 
      WHERE bt.transaction_reference = COALESCE(NULLIF(TRIM(pl.transaction_no), ''), CONCAT('BACKFILL-PPAY-', p.id, '-', pl.id))
  );

-- 3. Create or replace High-Performance View for Bank Report Tracking
CREATE OR REPLACE VIEW view_bank_transactions_report AS
SELECT
    bt.id AS transaction_id,
    bt.bank_id,
    b.name AS bank_name,
    b.account_name,
    b.account_number,
    COALESCE(b.current_balance, b.opening_balance, 0) AS bank_current_balance,
    bt.amount,
    bt.transaction_type,
    bt.transaction_reference,
    bt.status AS transaction_status,
    bt.transaction_date,
    bt.description,
    bt.sale_id,
    s.no AS sale_no,
    s.reference AS sale_reference,
    bt.purchase_id,
    p.reference_no AS purchase_reference_no,
    bt.expense_id,
    e.reference AS expense_reference,
    CASE 
        WHEN bt.transaction_type = 'SALE' THEN 'IN'
        WHEN bt.transaction_type IN ('PURCHASE', 'EXPENSE') THEN 'OUT'
        ELSE 'OTHER'
    END AS flow_type,
    bt.created_at
FROM tbl_bank_transactions bt
JOIN tbl_bank b ON b.id = bt.bank_id
LEFT JOIN tbl_sales s ON s.id = bt.sale_id
LEFT JOIN tbl_purchases p ON p.id = bt.purchase_id
LEFT JOIN tbl_expenses e ON e.id = bt.expense_id;

