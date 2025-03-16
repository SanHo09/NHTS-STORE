CREATE SEQUENCE invoice_detail_seq START WITH 1 INCREMENT BY 50
GO

CREATE SEQUENCE invoice_seq START WITH 1 INCREMENT BY 50
GO

CREATE TABLE invoice
(
    id           bigint    NOT NULL,
    create_date  date      NOT NULL,
    total_amount float(53) NOT NULL,
    CONSTRAINT pk_invoice PRIMARY KEY (id)
)
GO

CREATE TABLE invoice_detail
(
    id         bigint NOT NULL,
    invoice_id bigint NOT NULL,
    product_id bigint NOT NULL,
    CONSTRAINT pk_invoicedetail PRIMARY KEY (id)
)
GO

CREATE TABLE permissions
(
    permission_id   int         NOT NULL,
    permission_name varchar(50) NOT NULL,
    description     varchar(255),
    CONSTRAINT pk_permissions PRIMARY KEY (permission_id)
)
GO

CREATE TABLE product
(
    id               bigint IDENTITY (1, 1) NOT NULL,
    name             varchar(255)           NOT NULL,
    sale_price       float(53)              NOT NULL,
    purchase_price   float(53),
    manufacture_date date,
    expiry_date      date,
    manufacturer     varchar(255),
    quantity         int,
    supplier_id      bigint                 NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
)
GO

CREATE TABLE role_permissions
(
    permission_id int NOT NULL,
    role_id       int NOT NULL,
    CONSTRAINT pk_role_permissions PRIMARY KEY (permission_id, role_id)
)
GO

CREATE TABLE roles
(
    role_id     int         NOT NULL,
    role_name   varchar(50) NOT NULL,
    description varchar(255),
    CONSTRAINT pk_roles PRIMARY KEY (role_id)
)
GO

CREATE TABLE supplier
(
    id      bigint       NOT NULL,
    name    varchar(255) NOT NULL,
    address varchar(255) NOT NULL,
    CONSTRAINT pk_supplier PRIMARY KEY (id)
)
GO

CREATE TABLE user_roles
(
    role_id int NOT NULL,
    user_id int NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
)
GO

CREATE TABLE users
(
    user_id    int          NOT NULL,
    username   varchar(50)  NOT NULL,
    password   varchar(256) NOT NULL,
    email      varchar(100),
    full_name  varchar(100),
    created_at datetime,
    updated_at datetime,
    CONSTRAINT pk_users PRIMARY KEY (user_id)
)
GO

ALTER TABLE permissions
    ADD CONSTRAINT uc_permissions_permissionname UNIQUE (permission_name)
GO

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_rolename UNIQUE (role_name)
GO

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email)
GO

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username)
GO

ALTER TABLE invoice_detail
    ADD CONSTRAINT FK_INVOICEDETAIL_ON_INVOICE FOREIGN KEY (invoice_id) REFERENCES invoice (id)
GO

ALTER TABLE invoice_detail
    ADD CONSTRAINT FK_INVOICEDETAIL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id)
GO

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_SUPPLIER FOREIGN KEY (supplier_id) REFERENCES supplier (id)
GO

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_rolper_on_permission FOREIGN KEY (permission_id) REFERENCES permissions (permission_id)
GO

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_rolper_on_role FOREIGN KEY (role_id) REFERENCES roles (role_id)
GO

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES roles (role_id)
GO

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (user_id)
GO