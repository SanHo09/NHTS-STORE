ALTER TABLE product
ADD barcode NVARCHAR(255) NULL;
GO
CREATE UNIQUE INDEX idx_product_barcode ON product (barcode) WHERE barcode IS NOT NULL;