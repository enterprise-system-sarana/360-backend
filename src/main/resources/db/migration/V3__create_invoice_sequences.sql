-- ============================================================================
-- V3: Create Table for Invoice Sequences (Atomic Invoice & Register Numbering)
-- ============================================================================

CREATE TABLE IF NOT EXISTS tbl_invoice_sequences (
    sequence_key VARCHAR(50) NOT NULL,
    last_value BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT pk_invoice_sequences PRIMARY KEY (sequence_key)
);

CREATE INDEX IF NOT EXISTS idx_invoice_sequences_key ON tbl_invoice_sequences(sequence_key);
