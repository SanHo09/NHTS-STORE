ALTER TABLE invoice_detail ADD quantity INT NOT NULL DEFAULT 1;
ALTER TABLE invoice_detail ADD unit_price DECIMAL(19, 2) NOT NULL DEFAULT 0;
ALTER TABLE invoice_detail ADD subtotal DECIMAL(19, 2) NOT NULL DEFAULT 0;
GO

UPDATE invoice_detail
SET unit_price = (SELECT sale_price FROM product WHERE product.id = invoice_detail.product_id),
    subtotal = (SELECT sale_price FROM product WHERE product.id = invoice_detail.product_id) * quantity
WHERE 1=1;

-- Remove default constraints after data is updated
ALTER TABLE invoice_detail ALTER COLUMN quantity INT NOT NULL;
ALTER TABLE invoice_detail ALTER COLUMN unit_price DECIMAL(19, 2) NOT NULL;
ALTER TABLE invoice_detail ALTER COLUMN subtotal DECIMAL(19, 2) NOT NULL;