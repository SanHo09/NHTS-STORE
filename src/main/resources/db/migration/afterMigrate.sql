-- Only insert if data doesn't exist
IF NOT EXISTS (SELECT 1 FROM permissions)
    BEGIN
        SET IDENTITY_INSERT permissions ON;
        INSERT INTO permissions (permission_id, permission_name, description)
        VALUES (1, N'FULL_ACCESS', N'Toàn quyền truy cập'),
               (2, N'VIEW_ONLY', N'Chỉ xem');

        SET IDENTITY_INSERT permissions OFF;

    END

IF NOT EXISTS (SELECT 1 FROM roles)
    BEGIN
        SET IDENTITY_INSERT roles ON;
        INSERT INTO roles (role_id, role_name, description)
        VALUES (1, N'SUPER_ADMIN', N'Quản trị toàn hệ thống');
        SET IDENTITY_INSERT roles OFF;
    END

IF NOT EXISTS (SELECT 1 FROM role_permissions)
    BEGIN
        INSERT INTO role_permissions (role_id, permission_id)
        VALUES (1, 1);
    END

IF NOT EXISTS (SELECT 1 FROM users)
    BEGIN
        SET IDENTITY_INSERT users ON;
        INSERT INTO users (user_id, username, password, email, full_name, status,created_by)
        VALUES
            (1, N'phamduyhuy', '{bcrypt}$2a$10$L73YHCOYAon9Hz1fmq9qheCf2HflSkuSZvulo2pVcoks0OhN5QaOa', 'huypd.dev@gmail.com', N'Phạm Duy Huy', 'ACTIVE','system' ),
            (2, N'phamvinhsang', '{bcrypt}$2a$10$L73YHCOYAon9Hz1fmq9qheCf2HflSkuSZvulo2pVcoks0OhN5QaOa', 'phamvinhsang@gmail.com', N'Phạm Vĩnh Sang', 'ACTIVE','system'),
            (3, N'danghuuhoainam', '{bcrypt}$2a$10$L73YHCOYAon9Hz1fmq9qheCf2HflSkuSZvulo2pVcoks0OhN5QaOa', 'danghuuhoainam@gmail.com', N'Đặng Hữu Hoài Nam', 'ACTIVE','system'),
            (4, N'nguyenhuutam', '{bcrypt}$2a$10$L73YHCOYAon9Hz1fmq9qheCf2HflSkuSZvulo2pVcoks0OhN5QaOa', 'nguyenhuutam@gmail.com', N'Nguyễn Hữu Tâm', 'ACTIVE','system');
        SET IDENTITY_INSERT users OFF;
    END

IF NOT EXISTS (SELECT 1 FROM user_roles)
    BEGIN
        INSERT INTO user_roles (user_id, role_id)
        VALUES (1, 1), (2, 1), (3, 1), (4, 1);
    END