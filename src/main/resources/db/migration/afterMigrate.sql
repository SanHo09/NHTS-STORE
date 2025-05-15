-- Only insert if data doesn't exist
IF NOT EXISTS (SELECT 1 FROM permissions)
    BEGIN
        SET IDENTITY_INSERT permissions ON;
        INSERT INTO permissions (permission_id, permission_name, description)
        VALUES (1, N'FULL_ACCESS', N'Toàn quyền truy cập'),
               (2,N'PRODUCT_MANAGEMENT', N'Quản lý sản phẩm'),
               (3,N'ORDER_MANAGEMENT', N'Quản lý đơn hàng'),
               (4,N'CUSTOMER_MANAGEMENT', N'Quản lý khách hàng'),
               (5,N'SUPPLIER_MANAGEMENT', N'Quản lý nhà cung cấp'),
               (6,N'USER_MANAGEMENT', N'Quản lý người dùng'),
               (7,N'ROLE_MANAGEMENT', N'Quản lý vai trò'),
               (8,N'STATISTICS', N'Thống kê'),
               (9,N'INVENTORY_MANAGEMENT', N'Quản lý tồn kho'),
               (10,N'SYSTEM_SETTINGS', N'Cài đặt hệ thống'),
               (13,N'POS_SALE', N'Bán hàng tại POS');

        SET IDENTITY_INSERT permissions OFF;

    END

IF NOT EXISTS (SELECT 1 FROM roles)
    BEGIN
        SET IDENTITY_INSERT roles ON;
        INSERT INTO roles (role_id, role_name, description)
        VALUES (1, N'SUPER_ADMIN', N'Quản trị toàn hệ thống'),
                (2,N'MANAGER', N'Quản lý cửa hàng'),
                (3,N'SALE', N'Nhân viên bán hàng');
        SET IDENTITY_INSERT roles OFF;
    END

IF NOT EXISTS (SELECT 1 FROM role_permissions)
    BEGIN
        INSERT INTO role_permissions (role_id, permission_id)
        VALUES
             (1, 1),
             (1, 2),
                (1, 3),
                (1, 4),
                (1, 5),
                (1, 6),
                (1, 7),
                (1, 8),
                (1, 9),
                (1, 10),
                (1, 13),
                (2, 2),
                (2, 3),
                (2, 4),
                (2, 5),
                (2, 6),
                (2, 7),
                (2, 8),
                (2, 9),
                (2, 10),
                (2, 13),
                (3, 10),
                (3, 13);
    END

IF NOT EXISTS (SELECT 1 FROM users)
    BEGIN
        SET IDENTITY_INSERT users ON;
        INSERT INTO users (user_id, username, password, email, full_name,created_by,role_id)
        VALUES
            (1, N'phamduyhuy', '{bcrypt}$2a$10$L73YHCOYAon9Hz1fmq9qheCf2HflSkuSZvulo2pVcoks0OhN5QaOa', 'huypd.dev@gmail.com', N'Phạm Duy Huy','system',1 ),
            (2, N'phamvinhsang', '{bcrypt}$2a$10$L73YHCOYAon9Hz1fmq9qheCf2HflSkuSZvulo2pVcoks0OhN5QaOa', 'phamvinhsang@gmail.com', N'Phạm Vĩnh Sang', 'system',1),
            (3, N'danghuuhoainam', '{bcrypt}$2a$10$L73YHCOYAon9Hz1fmq9qheCf2HflSkuSZvulo2pVcoks0OhN5QaOa', 'danghuuhoainam@gmail.com', N'Đặng Hữu Hoài Nam','system',1),
            (4, N'nguyenhuutam', '{bcrypt}$2a$10$L73YHCOYAon9Hz1fmq9qheCf2HflSkuSZvulo2pVcoks0OhN5QaOa', 'nguyenhuutam@gmail.com', N'Nguyễn Hữu Tâm','system',1);
        SET IDENTITY_INSERT users OFF;
    END

