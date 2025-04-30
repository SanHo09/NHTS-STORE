

SET IDENTITY_INSERT customer ON;
INSERT INTO customer (id,name,email, phone_number, address, is_active, created_by, created_on, last_modified_by, last_modified_on)
VALUES
    (1,'Nguyen Van A', 'nguyenvana@gmail.com','0901234567', 'Ho Chi Minh City', 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (2,'Tran Thi B', 'tranthib@gmail.com','0912345678', 'Ha Noi', 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (3,'Le Van C', 'levanc@gmail.com','0923456789', 'Da Nang', 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (4,'Pham Thi D', 'phamthid@gmail.com','0934567890', 'Can Tho', 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (5,'Hoang Van E', 'hoangvane@gmail.com','0945678901', 'Nha Trang', 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);
SET IDENTITY_INSERT customer OFF ;

SET IDENTITY_INSERT orders ON;
INSERT INTO orders (id,create_date, total_amount, status, customer_id, is_active, created_by, created_on, last_modified_by, last_modified_on)
VALUES
(1,'2023-12-15', 15.50, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(2,'2024-01-05', 24.70, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(3,'2024-01-20', 8.20, 'COMPLETED', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(4,'2024-02-08', 16.90, 'COMPLETED', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(5,'2024-02-22', 32.40, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(6,'2024-03-10', 19.70, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(7,'2024-03-25', 27.50, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(8,'2024-04-05', 12.60, 'COMPLETED', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(9,'2024-04-18', 22.30, 'COMPLETED', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

(10,'2024-05-02', 18.50, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(11,'2024-05-05', 14.20, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(12,'2024-05-07', 9.80, 'IN_PROGRESS', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(13,'2024-05-08', 21.40, 'IN_PROGRESS', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(14,'2024-05-09', 11.60, 'IN_PROGRESS', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(15,'2024-05-10', 17.30, 'IN_PROGRESS', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(16,'2024-05-11', 8.90, 'IN_PROGRESS', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(17,'2024-05-12', 29.70, 'IN_PROGRESS', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(18,'2024-05-13', 13.80, 'IN_PROGRESS', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
(19,'2024-05-14', 6.50, 'IN_PROGRESS', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);
SET IDENTITY_INSERT orders OFF ;



SET IDENTITY_INSERT order_detail ON;
INSERT INTO order_detail ( id,order_id, product_id)
VALUES
-- Order 1 details
(1,1, 1), (2,1, 6),
-- Order 2 details
(3,2, 2), (4,2, 7), (5,2, 3),
-- Order 3 details
(6,3, 4), (7,3, 8),
-- Order 4 details
(8,4, 5), (9,4, 10),
-- Order 5 details
(10,5, 1), (11,5, 6), (12,5, 7), (13,5, 11),
-- Order 6 details
(14,6, 2), (15,6, 12),
-- Order 7 details
(16,7, 3), (17,7, 8), (18,7, 13),
-- Order 8 details
(19,8, 4), (20,8, 9),
-- Order 9 details
(21,9, 5), (22,9, 14),
-- Order 10 details
(23,10, 1), (24,10, 15),
-- Order 11 details
(25,11, 2), (26,11, 16),
-- Order 12 details
(27,12, 3), (28,12, 17),
-- Order 13 details
(29,13, 4), (30,13, 18),
-- Order 14 details
(31,14, 5), (32,14, 19),
-- Order 15 details
(33,15, 6), (34,15, 20),
-- Order 16 details
(35,16, 7), (36,16, 21),
-- Order 17 details
(37,17, 8),
-- Order 18 details
(38,18, 9), (39,18, 10), (40,18, 11),
-- Order 19 details
(41,19, 12), (42,19, 13);
SET IDENTITY_INSERT order_detail OFF ;

GO