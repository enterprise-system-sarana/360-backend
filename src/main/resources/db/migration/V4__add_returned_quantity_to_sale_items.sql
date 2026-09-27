ALTER TABLE tbl_sale_items
    ADD COLUMN returned_quantity NUMERIC(15, 4) NOT NULL DEFAULT 0;

CREATE TABLE tbl_sale_item_returned_serials (
    sale_item_id BIGINT NOT NULL REFERENCES tbl_sale_items(id),
    product_serial_id BIGINT NOT NULL
);
