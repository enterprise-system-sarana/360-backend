ALTER TABLE tbl_payment
    ADD COLUMN purchase_id BIGINT REFERENCES tbl_purchases(id);

CREATE INDEX idx_payments_purchase_id ON tbl_payment(purchase_id);