CREATE TABLE supplier_category
(
    id   bigint IDENTITY (1, 1) NOT NULL,
    name nvarchar(255)           NOT NULL,
    CONSTRAINT pk_supplier_category PRIMARY KEY (id)
)
    GO

-- Insert a default category to avoid FK issues
INSERT INTO supplier_category (name) VALUES ('Default Category');

ALTER TABLE supplier
    ADD supplier_category_id bigint NOT NULL DEFAULT 1
    GO

ALTER TABLE supplier
    ADD CONSTRAINT FK_SUPPLIER_ON_SUPPLIER_CATEGORY FOREIGN KEY (supplier_category_id) REFERENCES supplier_category (id)
    GO
