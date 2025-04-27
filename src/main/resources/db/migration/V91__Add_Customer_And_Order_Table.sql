CREATE TABLE customer
(
    id           BIGINT IDENTITY(1,1)       NOT NULL,
    is_active    bit NOT NULL DEFAULT 1,
    name         NVARCHAR(255) NOT NULL,
    email        VARCHAR(255)  NOT NULL,
    phone_number VARCHAR(255)  NOT NULL,
    address      VARCHAR(255)  NOT NULL,
    CONSTRAINT pk_customer PRIMARY KEY (id)
);

CREATE TABLE order_detail
(
    id         BIGINT IDENTITY(1,1) NOT NULL,
    order_id   BIGINT                NOT NULL,
    product_id BIGINT                NOT NULL,
    CONSTRAINT pk_orderdetail PRIMARY KEY (id)
);

CREATE TABLE orders
(
    id           BIGINT IDENTITY(1,1)  NOT NULL,
    is_active    bit NOT NULL DEFAULT 1,
    create_date  date     NOT NULL,
    total_amount FLOAT   NOT NULL,
    status       SMALLINT NOT NULL,
    customer_id  BIGINT   NOT NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);


ALTER TABLE order_detail
    ADD CONSTRAINT FK_ORDERDETAIL_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);

ALTER TABLE order_detail
    ADD CONSTRAINT FK_ORDERDETAIL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id);

ALTER TABLE orders
    ADD CONSTRAINT FK_ORDERS_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer (id);