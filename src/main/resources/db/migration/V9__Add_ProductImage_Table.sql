CREATE TABLE product_image (
    id BIGINT IDENTITY(1,1) NOT NULL,
    image_data VARBINARY(MAX) NOT NULL,
    image_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(100) NOT NULL,
    is_thumbnail BIT NOT NULL DEFAULT 0,
    product_id BIGINT NOT NULL,
    created_on DATETIME2 DEFAULT SYSDATETIME(),
    created_by NVARCHAR(255),
    last_modified_on DATETIME2 DEFAULT SYSDATETIME(),
    last_modified_by NVARCHAR(255),
    is_active BIT NOT NULL DEFAULT 1,
    CONSTRAINT pk_product_image PRIMARY KEY (id)
);

ALTER TABLE product_image
    ADD CONSTRAINT FK_PRODUCTIMAGE_ON_PRODUCT
    FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE;
