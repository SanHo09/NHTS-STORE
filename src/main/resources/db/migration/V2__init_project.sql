CREATE TABLE invoice
(
    id          BIGINT IDENTITY(1,1) NOT NULL,
    create_date DATE   NOT NULL,
    total_amount FLOAT NOT NULL,
    CONSTRAINT pk_invoice PRIMARY KEY (id)
);

CREATE TABLE invoice_detail
(
    id         BIGINT IDENTITY(1,1) NOT NULL,
    invoice_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT pk_invoicedetail PRIMARY KEY (id)
);

CREATE TABLE product
(
    id               BIGINT IDENTITY(1,1) NOT NULL,
    name             VARCHAR(255) NOT NULL,
    sale_price FLOAT NOT NULL,
    purchase_price FLOAT NULL,
    manufacture_date DATE NULL,
    expiry_date      DATE NULL,
    quantity         INT NULL,
    supplier_id      BIGINT NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

CREATE TABLE supplier
(
    id      BIGINT IDENTITY(1,1) NOT NULL,
    name    VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    CONSTRAINT pk_supplier PRIMARY KEY (id)
);

ALTER TABLE invoice_detail
    ADD CONSTRAINT FK_INVOICEDETAIL_ON_INVOICE FOREIGN KEY (invoice_id) REFERENCES invoice (id);

ALTER TABLE invoice_detail
    ADD CONSTRAINT FK_INVOICEDETAIL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id);

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_SUPPLIER FOREIGN KEY (supplier_id) REFERENCES supplier (id);
