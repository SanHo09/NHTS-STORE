ALTER TABLE invoice
ADD customer_id bigint

ALTER TABLE invoice
    ADD CONSTRAINT FK_INVOICE_ON_CUSTOMER FOREIGN KEY (customer_id) REFERENCES customer (id)
    GO