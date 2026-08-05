-- Add first_name, last_name, and phone columns to tbl_users
ALTER TABLE tbl_users ADD COLUMN IF NOT EXISTS first_name VARCHAR(50);
ALTER TABLE tbl_users ADD COLUMN IF NOT EXISTS last_name VARCHAR(50);
ALTER TABLE tbl_users ADD COLUMN IF NOT EXISTS phone VARCHAR(25);