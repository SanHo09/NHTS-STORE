CREATE TABLE users (
                       user_id        INT IDENTITY(1,1) PRIMARY KEY,
                       username       VARCHAR(50) NOT NULL UNIQUE,
                       password       VARCHAR(256) NOT NULL,
                       email          VARCHAR(100) UNIQUE,
                       full_name      VARCHAR(100),
                       created_on     DATETIME2 DEFAULT SYSDATETIME(),
                       created_by     NVARCHAR(255),
                       last_modified_on DATETIME2 DEFAULT SYSDATETIME(),
                       last_modified_by NVARCHAR(255)
);

CREATE TABLE roles (
                       role_id     INT IDENTITY(1,1) PRIMARY KEY,
                       role_name   VARCHAR(50) NOT NULL UNIQUE,
                       description VARCHAR(255)
);

CREATE TABLE permissions (
                             permission_id   INT IDENTITY(1,1) PRIMARY KEY,
                             permission_name VARCHAR(50) NOT NULL UNIQUE,
                             description     VARCHAR(255)
);

CREATE TABLE role_permissions (
                                  role_id       INT NOT NULL,
                                  permission_id INT NOT NULL,
                                  PRIMARY KEY (role_id, permission_id),
                                  FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE,
                                  FOREIGN KEY (permission_id) REFERENCES permissions(permission_id) ON DELETE CASCADE
);
CREATE TABLE user_roles (
                            user_id INT NOT NULL,
                            role_id INT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                            FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE
);

