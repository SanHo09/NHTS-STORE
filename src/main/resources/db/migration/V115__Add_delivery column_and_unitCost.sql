ALTER TABLE orders
ADD  fulfilment_method NVARCHAR(255) DEFAULT 'CUSTOMER_TAKEAWAY' NOT NULL;

GO
EXEC sp_rename 'orders.status', 'delivery_status', 'COLUMN';
GO
ALTER TABLE orders
ALTER COLUMN delivery_status NVARCHAR(50) not null ;

GO
ALTER TABLE orders
ADD delivery_fee DECIMAL(19, 2);

GO
ALTER TABLE orders
ADD delivery_address NVARCHAR(255);

GO
ALTER TABLE invoice
ADD fulfilment_method NVARCHAR(255) DEFAULT 'CUSTOMER_TAKEAWAY' NOT NULL;

Go
ALTER TABLE  invoice
ADD delivery_fee DECIMAL(19, 2);

GO
EXEC sp_rename 'invoice.shipping_address', 'delivery_address', 'COLUMN';

Go
ALTER TABLE order_detail
ADD unit_cost DECIMAL(19, 2) DEFAULT 0.00 NOT NULL;

GO
ALTER TABLE customer
ALTER COLUMN address NVARCHAR(255) NULL;

GO
UPDATE order_detail
SET unit_cost = (SELECT purchase_price FROM product WHERE product.id = order_detail.product_id)
WHERE order_detail.unit_cost = 0.00 or 1=1;

