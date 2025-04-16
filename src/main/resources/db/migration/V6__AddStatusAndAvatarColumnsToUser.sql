ALTER TABLE users
    add is_active bit NOT NULL DEFAULT 1
GO
ALTER TABLE users
    add is_deleted bit NOT NULL DEFAULT 0
GO
ALTER TABLE users
    add avatar varchar(255) NULL