ALTER TABLE order_detail
ADD quantity INT;

UPDATE order_detail
SET quantity = 1;

ALTER TABLE orders
ALTER COLUMN customer_id BIGINT NULL;

ALTER TABLE orders
    ADD user_id BIGINT NULL;

ALTER TABLE orders
ADD CONSTRAINT fk_orders_created_by
FOREIGN KEY (user_id) REFERENCES users(user_id);