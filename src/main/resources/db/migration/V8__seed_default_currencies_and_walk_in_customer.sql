-- Seed the base currency (USD), Cambodian Riel conversion, and POS walk-in customer.
INSERT INTO tbl_currency (code, name, operation, rate, symbol, status)
VALUES
    ('USD', 'US Dollar', '*', 1, '$', 'ACTIVE'),
    ('KHR', 'Cambodian Riel', '/', 4100, '៛', 'ACTIVE')
ON CONFLICT (code) DO UPDATE
SET name = EXCLUDED.name,
    operation = EXCLUDED.operation,
    rate = EXCLUDED.rate,
    symbol = EXCLUDED.symbol,
    status = EXCLUDED.status;

INSERT INTO tbl_customer (name, code, phone, email, note, status)
SELECT 'WALK-IN', 'WALK-IN', NULL, NULL, 'Default customer for point-of-sale transactions', 'ACTIVE'
WHERE NOT EXISTS (
    SELECT 1
    FROM tbl_customer
    WHERE code = 'WALK-IN'
       OR UPPER(name) = 'WALK-IN'
);
