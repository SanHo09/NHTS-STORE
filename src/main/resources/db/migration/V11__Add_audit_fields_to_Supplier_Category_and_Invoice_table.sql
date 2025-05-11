ALTER TABLE supplier_category
    ADD created_on DATETIME2 DEFAULT SYSDATETIME(),
        created_by NVARCHAR(255),
        last_modified_on DATETIME2 DEFAULT SYSDATETIME(),
        last_modified_by NVARCHAR(255),
        is_active bit NOT NULL DEFAULT 1;

ALTER TABLE invoice
    ADD created_on DATETIME2 DEFAULT SYSDATETIME(),
        created_by NVARCHAR(255),
        last_modified_on DATETIME2 DEFAULT SYSDATETIME(),
        last_modified_by NVARCHAR(255),
        is_active bit NOT NULL DEFAULT 1;