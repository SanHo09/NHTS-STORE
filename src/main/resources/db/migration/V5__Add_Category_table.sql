CREATE TABLE category
(
    id   bigint IDENTITY (1, 1) NOT NULL,
    name varchar(255)           NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
)
    GO

ALTER TABLE product
    ADD category_id bigint NOT NULL
    GO

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id)
    GO