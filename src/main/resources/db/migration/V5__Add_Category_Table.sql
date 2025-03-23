CREATE TABLE category
(
    id   BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_category PRIMARY KEY (id)
);

CREATE TABLE invoice
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    create_date date NOT NULL,
    total_amount DOUBLE NOT NULL,
    CONSTRAINT pk_invoice PRIMARY KEY (id)
);

CREATE TABLE invoice_detail
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    invoice_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    CONSTRAINT pk_invoicedetail PRIMARY KEY (id)
);

CREATE TABLE permissions
(
    permission_id   INT         NOT NULL,
    permission_name VARCHAR(50) NOT NULL,
    `description`   VARCHAR(255) NULL,
    CONSTRAINT pk_permissions PRIMARY KEY (permission_id)
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
    category_id      BIGINT       NOT NULL,
    CONSTRAINT pk_product PRIMARY KEY (id)
);

CREATE TABLE role_permissions
(
    permission_id INT NOT NULL,
    role_id       INT NOT NULL,
    CONSTRAINT pk_role_permissions PRIMARY KEY (permission_id, role_id)
);

CREATE TABLE roles
(
    role_id       INT         NOT NULL,
    role_name     VARCHAR(50) NOT NULL,
    `description` VARCHAR(255) NULL,
    CONSTRAINT pk_roles PRIMARY KEY (role_id)
);

CREATE TABLE supplier
(
    id      BIGINT       NOT NULL,
    name    VARCHAR(255) NOT NULL,
    address VARCHAR(255) NOT NULL,
    CONSTRAINT pk_supplier PRIMARY KEY (id)
);

CREATE TABLE user_roles
(
    role_id INT NOT NULL,
    user_id INT NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
);

CREATE TABLE users
(
    user_id          INT          NOT NULL,
    created_on       datetime NULL,
    created_by       VARCHAR(255) NULL,
    last_modified_on datetime NULL,
    last_modified_by VARCHAR(255) NULL,
    username         VARCHAR(50)  NOT NULL,
    password         VARCHAR(256) NOT NULL,
    email            VARCHAR(100) NULL,
    full_name        VARCHAR(100) NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id)
);

ALTER TABLE permissions
    ADD CONSTRAINT uc_permissions_permissionname UNIQUE (permission_name);

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_rolename UNIQUE (role_name);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username);

ALTER TABLE invoice_detail
    ADD CONSTRAINT FK_INVOICEDETAIL_ON_INVOICE FOREIGN KEY (invoice_id) REFERENCES invoice (id);

ALTER TABLE invoice_detail
    ADD CONSTRAINT FK_INVOICEDETAIL_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id);

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES supplier (id);

ALTER TABLE product
    ADD CONSTRAINT FK_PRODUCT_ON_SUPPLIER FOREIGN KEY (supplier_id) REFERENCES supplier (id);

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_rolper_on_permission FOREIGN KEY (permission_id) REFERENCES permissions (permission_id);

ALTER TABLE role_permissions
    ADD CONSTRAINT fk_rolper_on_role FOREIGN KEY (role_id) REFERENCES roles (role_id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES roles (role_id);

ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (user_id);