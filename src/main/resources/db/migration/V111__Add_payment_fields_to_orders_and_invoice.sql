ALTER TABLE orders ADD payment_method NVARCHAR(50);
ALTER TABLE orders ADD payment_status NVARCHAR(50);
ALTER TABLE orders ADD payment_transaction_id NVARCHAR(255);
ALTER TABLE orders ALTER COLUMN total_amount DECIMAL(19,2);

ALTER TABLE order_detail ADD unit_price DECIMAL(19, 2);
ALTER TABLE order_detail ADD subtotal DECIMAL(19, 2);

ALTER TABLE invoice ADD payment_method NVARCHAR(50);
ALTER TABLE invoice ADD payment_status NVARCHAR(50);
ALTER TABLE invoice ADD payment_transaction_id NVARCHAR(255);
ALTER TABLE invoice ALTER COLUMN total_amount DECIMAL(19,2);
GO
UPDATE order_detail
SET unit_price = (SELECT sale_price FROM product WHERE product.id = order_detail.product_id),
    subtotal = (SELECT sale_price FROM product WHERE product.id = order_detail.product_id) * quantity
WHERE 1=1;

