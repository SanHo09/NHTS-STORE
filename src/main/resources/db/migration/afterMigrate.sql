-- Only insert if data doesn't exist
IF NOT EXISTS (SELECT 1 FROM permissions)
    BEGIN
        SET IDENTITY_INSERT permissions ON;
        INSERT INTO permissions (permission_id, permission_name, description)
        VALUES (1, N'FULL_ACCESS', N'Toàn quyền truy cập'),
               (2, N'USER_MANAGEMENT', N'Quản lý người dùng'),
               (3, N'ROLE_MANAGEMENT', N'Quản lý vai trò'),
               (4, N'PERMISSION_MANAGEMENT', N'Quản lý quyền truy cập'),
               (5, N'PRODUCT_MANAGEMENT', N'Quản lý sản phẩm'),
               (6, N'ORDER_MANAGEMENT', N'Quản lý đơn hàng'),
               (7, N'CUSTOMER_MANAGEMENT', N'Quản lý khách hàng'),
               (8,N'USER_CREATION', N'Tạo người dùng'),
               (9,N'USER_UPDATE', N'Cập nhật người dùng'),
               (10,N'USER_DELETION', N'Xóa người dùng'),
               (11,N'ROLE_CREATION', N'Tạo vai trò'),
               (12,N'ROLE_UPDATE', N'Cập nhật vai trò'),
               (13,N'ROLE_DELETION', N'Xóa vai trò'),
               (14,N'PERMISSION_CREATION', N'Tạo quyền truy cập'),
               (15,N'PERMISSION_UPDATE', N'Cập nhật quyền truy cập'),
               (16,N'PERMISSION_DELETION', N'Xóa quyền truy cập');

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