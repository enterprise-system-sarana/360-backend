-- ============================================================================
-- V4: Add bank_id, purchase_id, sale_id and audit columns to tbl_bank_transactions
-- ============================================================================

ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS bank_id BIGINT;
ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS purchase_id BIGINT;
ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS sale_id BIGINT;

ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS created_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);
ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE tbl_bank_transactions ADD COLUMN IF NOT EXISTS deleted_by VARCHAR(100);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_bank_transactions_bank'
    ) THEN
        ALTER TABLE tbl_bank_transactions
        ADD CONSTRAINT fk_bank_transactions_bank
        FOREIGN KEY (bank_id) REFERENCES tbl_bank(id) ON DELETE SET NULL;
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_bank_transactions_bank ON tbl_bank_transactions(bank_id);
CREATE INDEX IF NOT EXISTS idx_bank_transactions_purchase ON tbl_bank_transactions(purchase_id);
CREATE INDEX IF NOT EXISTS idx_bank_transactions_sale ON tbl_bank_transactions(sale_id);
CREATE INDEX IF NOT EXISTS idx_bank_transactions_expense ON tbl_bank_transactions(expense_id);
