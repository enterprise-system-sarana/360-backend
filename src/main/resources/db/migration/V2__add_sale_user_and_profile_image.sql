ALTER TABLE tbl_sales
    ADD COLUMN IF NOT EXISTS user_id BIGINT REFERENCES tbl_users(id);

ALTER TABLE tbl_users
    ADD COLUMN IF NOT EXISTS profile_image VARCHAR(500);

CREATE INDEX IF NOT EXISTS idx_sales_user ON tbl_sales(user_id);
