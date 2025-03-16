CREATE TABLE invoice
(
    id          BIGINT NOT NULL,
    create_date date   NOT NULL,
    total_amount DOUBLE NOT NULL,
    CONSTRAINT pk_invoice PRIMARY KEY (id)
);

CREATE TABLE invoice_detail
(
    id         BIGINT NOT NULL,
    invoice_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT pk_invoicedetail PRIMARY KEY (id)
);

CREATE TABLE product
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    name             VARCHAR(255) NOT NULL,
    sale_price DOUBLE NOT NULL,
    purchase_price DOUBLE NULL,
    manufacture_date date NULL,
    expiry_date      date NULL,
    manufacturer     VARCHAR(255) NULL,
    quantity         INT NULL,
    supplier_id      BIGINT       NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

CREATE TABLE supplier
(
    id      BIGINT       NOT NULL,
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