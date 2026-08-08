-- Align legacy text columns with the Sales entity's typed fields.
ALTER TABLE IF EXISTS tbl_sales
    ALTER COLUMN date TYPE TIMESTAMP WITHOUT TIME ZONE
    USING CASE
        WHEN date IS NULL OR BTRIM(date::text) = '' THEN NULL
        ELSE date::timestamp without time zone
    END;

ALTER TABLE IF EXISTS tbl_sales
    ALTER COLUMN no TYPE INTEGER
    USING NULLIF(REGEXP_REPLACE(no::text, '\\D', '', 'g'), '')::INTEGER;
