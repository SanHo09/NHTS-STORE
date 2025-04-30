
ALTER TABLE product
    ADD created_on DATETIME2 DEFAULT SYSDATETIME(),
        created_by NVARCHAR(255),
        last_modified_on DATETIME2 DEFAULT SYSDATETIME(),
        last_modified_by NVARCHAR(255);

ALTER TABLE supplier
    ADD created_on DATETIME2 DEFAULT SYSDATETIME(),
        created_by NVARCHAR(255),
        last_modified_on DATETIME2 DEFAULT SYSDATETIME(),
        last_modified_by NVARCHAR(255);

ALTER TABLE customer
    ADD created_on DATETIME2 DEFAULT SYSDATETIME(),
        created_by NVARCHAR(255),
        last_modified_on DATETIME2 DEFAULT SYSDATETIME(),
        last_modified_by NVARCHAR(255);

ALTER TABLE orders
    ADD created_on DATETIME2 DEFAULT SYSDATETIME(),
        created_by NVARCHAR(255),
        last_modified_on DATETIME2 DEFAULT SYSDATETIME(),
        last_modified_by NVARCHAR(255);

ALTER TABLE category
    ADD created_on DATETIME2 DEFAULT SYSDATETIME(),
        created_by NVARCHAR(255),
        last_modified_on DATETIME2 DEFAULT SYSDATETIME(),
        last_modified_by NVARCHAR(255);

ALTER TABLE roles
    ADD created_on DATETIME2 DEFAULT SYSDATETIME(),
        created_by NVARCHAR(255),
        last_modified_on DATETIME2 DEFAULT SYSDATETIME(),
        last_modified_by NVARCHAR(255);

ALTER TABLE permissions
    ADD created_on DATETIME2 DEFAULT SYSDATETIME(),
        created_by NVARCHAR(255),
        last_modified_on DATETIME2 DEFAULT SYSDATETIME(),
        last_modified_by NVARCHAR(255);