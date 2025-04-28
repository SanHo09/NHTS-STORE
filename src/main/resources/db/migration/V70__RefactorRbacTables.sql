DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS role_permissions;
DROP TABLE IF EXISTS permissions;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS users
GO
CREATE TABLE roles
(
    role_id     bigint IDENTITY (1, 1)   NOT NULL,
    is_active   bit default 1 not null,
    role_name   varchar(50) NOT NULL,
    description nvarchar(255),
    CONSTRAINT pk_roles PRIMARY KEY (role_id)
)
GO

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_rolename UNIQUE (role_name)
GO

CREATE TABLE permissions
(
    permission_id   bigint  IDENTITY (1, 1)    NOT NULL,
    is_active       bit default 1 not null,
    permission_name varchar(50) NOT NULL,
    description     nvarchar(255),
    CONSTRAINT pk_permissions PRIMARY KEY (permission_id)
)
GO

ALTER TABLE permissions
    ADD CONSTRAINT uc_permissions_permissionname UNIQUE (permission_name)
GO
CREATE TABLE users
(
    user_id          bigint IDENTITY (1, 1) NOT NULL,
    created_on       datetimeoffset,
    created_by       varchar(255),
    last_modified_on datetimeoffset,
    last_modified_by varchar(255),
    username         varchar(50)  NOT NULL,
    password         varchar(256) NOT NULL,
    email            varchar(100),
    full_name        nvarchar(100),
    avatar           varbinary(MAX),
    role_id          bigint  NOT NULL,
    is_active        bit default 1 not null ,
    CONSTRAINT pk_users PRIMARY KEY (user_id)
)

GO
ALTER TABLE users
    ADD CONSTRAINT uc_users_username UNIQUE (username)
GO
ALTER TABLE users
    ADD CONSTRAINT FK_USERS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (role_id)
GO


CREATE TABLE role_permissions
(
    id            bigint IDENTITY (1, 1) NOT NULL,
    role_id       bigint,
    permission_id bigint,
    CONSTRAINT pk_role_permissions PRIMARY KEY (id)
)
GO

ALTER TABLE role_permissions
    ADD CONSTRAINT uc_e8ecd47e0540a13a477a6fe0e UNIQUE (role_id, permission_id)
GO

ALTER TABLE role_permissions
    ADD CONSTRAINT FK_ROLE_PERMISSIONS_ON_PERMISSION FOREIGN KEY (permission_id) REFERENCES permissions (permission_id)
GO

ALTER TABLE role_permissions
    ADD CONSTRAINT FK_ROLE_PERMISSIONS_ON_ROLE FOREIGN KEY (role_id) REFERENCES roles (role_id)

GO

