SET IDENTITY_INSERT orders ON;
INSERT INTO orders (id,create_date, total_amount, status, customer_id, is_active, created_by, created_on, last_modified_by, last_modified_on)
VALUES
    (20,'2024-06-14', 123.50, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (21,'2024-06-15', 200.00, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (22,'2024-06-16', 150.75, 'COMPLETED', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (23,'2024-06-17', 300.25, 'IN_PROGRESS', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (24,'2024-06-18', 400.50, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (25,'2024-06-19', 500.00, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (26,'2024-06-20', 600.75, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (27,'2024-06-21', 700.25, 'COMPLETED', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (28,'2024-06-22', 800.50, 'IN_PROGRESS', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (29,'2024-06-23', 900.00, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

    (30,'2024-07-24', 1000.75, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (31,'2024-07-25', 1100.50, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (32,'2024-07-26', 1200.25, 'IN_PROGRESS', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (33,'2024-07-27', 1300.00, 'COMPLETED', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (34,'2024-07-28', 1400.75, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

    (35,'2024-08-29', 1500.50, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (36,'2024-08-30', 1600.25, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (37,'2024-08-31', 1700.00, 'COMPLETED', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (38,'2024-08-01', 1800.75, 'COMPLETED', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (39,'2024-08-02', 1900.50, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

    (40,'2024-09-03', 2000.25, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (41,'2024-09-04', 2100.00, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (42,'2024-09-05', 2200.75, 'IN_PROGRESS', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (43,'2024-09-06', 2300.50, 'COMPLETED', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (44,'2024-09-07', 2400.25, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

    (45,'2024-10-08', 2500.00, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (46,'2024-10-09', 2600.75, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (47,'2024-10-10', 2700.50, 'IN_PROGRESS', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (48,'2024-10-11', 2800.25, 'COMPLETED', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (49,'2024-10-12', 2900.00, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

    (50,'2024-11-13', 3000.75, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (51,'2024-11-14', 3100.50, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (52,'2024-11-15', 3200.25, 'IN_PROGRESS', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (53,'2024-11-16', 3300.00, 'COMPLETED', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (54,'2024-11-17', 3400.75, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

    (55,'2024-12-18', 3500.50, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (56,'2024-12-19', 3600.25, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (57,'2024-12-20', 3700.00, 'IN_PROGRESS', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (58,'2024-12-21', 3800.75, 'COMPLETED', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (59,'2024-12-22', 3900.50, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

    (60,'2025-01-23', 4000.25, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (61,'2025-01-24', 4100.00, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (62,'2025-01-25', 4200.75, 'IN_PROGRESS', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (63,'2025-01-26', 4300.50, 'COMPLETED', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (64,'2025-01-27', 4400.25, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

    (65,'2025-02-14', 4500.00, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (66,'2025-02-15', 4600.75, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (67,'2025-02-16', 4700.50, 'IN_PROGRESS', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (68,'2025-02-17', 4800.25, 'COMPLETED', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (69,'2025-02-18', 4900.00, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

    (70,'2025-03-02', 5000.75, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (71,'2025-03-03', 5100.50, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (72,'2025-03-04', 5200.25, 'IN_PROGRESS', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (73,'2025-03-05', 5300.00, 'COMPLETED', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (74,'2025-03-06', 5400.75, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

    (75,'2025-04-07', 5500.50, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (76,'2025-04-08', 5600.25, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (77,'2025-04-09', 5700.00, 'IN_PROGRESS', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (78,'2025-04-10', 5800.75, 'COMPLETED', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (79,'2025-04-11', 5900.50, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),

    (80,'2025-05-01', 6000.25, 'COMPLETED', 1, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (81,'2025-05-02', 6100.00, 'COMPLETED', 2, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (82,'2025-05-03', 6200.75, 'IN_PROGRESS', 3, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (83,'2025-05-04', 6300.50, 'COMPLETED', 4, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP),
    (84,'2025-05-05', 6400.25, 'COMPLETED', 5, 1, 'system', CURRENT_TIMESTAMP, 'system', CURRENT_TIMESTAMP);

SET IDENTITY_INSERT orders OFF ;
GO


SET IDENTITY_INSERT order_detail ON;
INSERT INTO order_detail (id, order_id, product_id, quantity)
VALUES
-- product have 25, order from 43 to 84
-- Order 20 details
(43, 20, 14, 2), (44, 20, 15, 1),
-- Order 21 details
(45, 21, 16, 3), (46, 21, 17, 1), (47, 21, 18, 2),
-- Order 22 details
(48, 22, 19, 1), (49, 22, 20, 4),
-- Order 23 details
(50, 23, 21, 2), (51, 23, 22, 2),
-- Order 24 details
(52, 24, 23, 1), (53, 24, 24, 3),
-- Order 25 details
(54, 25, 03, 2), (55, 25, 25, 1),
(56, 25, 07, 2), (57, 25, 01, 1),
-- Order 26 details
(58, 26, 11, 3), (59, 26, 14, 1), (60, 26, 15, 2),
(61, 26, 16, 1),

-- Order 27 details
(62, 27, 17, 2), (63, 27, 18, 1),
(64, 27, 19, 3),
-- Order 28 details
(65, 28, 20, 2), (66, 28, 21, 1),
(67, 28, 22, 4),
-- Order 29 details
(68, 29, 23, 1), (69, 29, 24, 2),
(70, 29, 25, 3),
-- Order 30 details
(71, 30, 01, 2), (72, 30, 02, 1),
(73, 30, 03, 2),
-- Order 31 details
(74, 31, 04, 3), (75, 31, 05, 1),
(76, 31, 06, 2),
-- Order 32 details
(77, 32, 07, 4), (78, 32, 08, 1),
(79, 32, 09, 3),
-- Order 33 details
(80, 33, 10, 2), (81, 33, 11, 2),
(82, 33, 12, 1),
(83, 33, 13, 3),
-- Order 34 details
(84, 34, 14, 2), (85, 34, 15, 1),
(86, 34, 16, 4),
-- Order 35 details
(87, 35, 17, 3), (88, 35, 18, 2),
(89, 35, 19, 1),
-- Order 36 details
(90, 36, 20, 2), (91, 36, 21, 3),
(92, 36, 22, 1),
-- Order 37 details
(93, 37, 23, 4), (94, 37, 24, 2),
(95, 37, 25, 1),
-- Order 38 details
(96, 38, 01, 2), (97, 38, 02, 1),
(98, 38, 03, 3),
-- Order 39 details
(99, 39, 04, 2), (100, 39, 05, 4),
(101, 39, 06, 1),
-- Order 40 details
(102, 40, 07, 3), (103, 40, 08, 2),
(104, 40, 09, 1),
-- Order 41 details
(105, 41, 10, 2), (106, 41, 11, 3),
(107, 41, 12, 1),
-- Order 42 details
(108, 42, 13, 4), (109, 42, 14, 2),
(110, 42, 15, 1),

-- Order 43 details
(111, 43, 16, 3), (112, 43, 17, 1),
(113, 43, 18, 2),
-- Order 44 details
(114, 44, 19, 2), (115, 44, 20, 1),
(116, 44, 21, 3),
-- Order 45 details
(117, 45, 22, 1), (118, 45, 23, 4),
(119, 45, 24, 2),
-- Order 46 details
(120, 46, 25, 2), (121, 46, 01, 1),
(122, 46, 02, 3),
-- Order 47 details
(123, 47, 03, 2), (124, 47, 04, 1),
(125, 47, 05, 4),
-- Order 48 details
(126, 48, 06, 3), (127, 48, 07, 2),
(128, 48, 08, 1),
-- Order 49 details
(129, 49, 09, 2), (130, 49, 10, 3),
(131, 49, 11, 1),
-- Order 50 details
(132, 50, 12, 4), (133, 50, 13, 2),
(134, 50, 14, 1),
-- Order 51 details
(135, 51, 15, 2), (136, 51, 16, 3),
(137, 51, 17, 1),
-- Order 52 details
(138, 52, 18, 2), (139, 52, 19, 4),
(140, 52, 20, 1),
-- Order 53 details
(141, 53, 21, 3), (142, 53, 22, 2),
(143, 53, 23, 1),
-- Order 54 details
(144, 54, 24, 2), (145, 54, 25, 1),
(146, 54, 01, 3),
-- Order 55 details
(147, 55, 02, 2), (148, 55, 03, 4),
(149, 55, 04, 1),
-- Order 56 details
(150, 56, 05, 3), (151, 56, 06, 2),
(152, 56, 07, 1),
-- Order 57 details
(153, 57, 08, 2), (154, 57, 09, 1),
(155, 57, 10, 4),
-- Order 58 details
(156, 58, 11, 3), (157, 58, 12, 2),
(158, 58, 13, 1),
-- Order 59 details
(159, 59, 14, 2), (160, 59, 15, 3),
(161, 59, 16, 1),
-- Order 60 details
(162, 60, 17, 4), (163, 60, 18, 2),
(164, 60, 19, 1),
-- Order 61 details
(165, 61, 20, 2), (166, 61, 21, 3),
(167, 61, 22, 1),
-- Order 62 details
(168, 62, 23, 2), (169, 62, 24, 1),
(170, 62, 25, 3),
-- Order 63 details
(171, 63, 01, 2), (172, 63, 02, 4),
(173, 63, 03, 1),
-- Order 64 details
(174, 64, 04, 3), (175, 64, 05, 2),
(176, 64, 06, 1),
-- Order 65 details
(177, 65, 07, 2), (178, 65, 08, 1),
(179, 65, 09, 3),
-- Order 66 details
(180, 66, 10, 4), (181, 66, 11, 2),
(182, 66, 12, 1),
-- Order 67 details
(183, 67, 13, 2), (184, 67, 14, 3),
(185, 67, 15, 1),
-- Order 68 details
(186, 68, 16, 2), (187, 68, 17, 1),
(188, 68, 18, 4),
-- Order 69 details
(189, 69, 19, 3), (190, 69, 20, 2),
(191, 69, 21, 1),
-- Order 70 details
(192, 70, 22, 2), (193, 70, 23, 1),
(194, 70, 24, 3),
-- Order 71 details
(195, 71, 25, 2), (196, 71, 01, 4),
(197, 71, 02, 1),
-- Order 72 details
(198, 72, 03, 3), (199, 72, 04, 2),
(200, 72, 05, 1),
-- Order 73 details
(201, 73, 06, 2), (202, 73, 07, 3),
(203, 73, 08, 1),
-- Order 74 details
(204, 74, 09, 4), (205, 74, 10, 2),
(206, 74, 11, 1),
-- Order 75 details
(207, 75, 12, 2), (208, 75, 13, 1),
(209, 75, 14, 3),
-- Order 76 details
(210, 76, 15, 2), (211, 76, 16, 4),
(212, 76, 17, 1),
-- Order 77 details
(213, 77, 18, 3), (214, 77, 19, 2),
(215, 77, 20, 1),
-- Order 78 details
(216, 78, 21, 2), (217, 78, 22, 1),
(218, 78, 23, 3),
-- Order 79 details
(219, 79, 24, 2), (220, 79, 25, 4),
(221, 79, 01, 1),
-- Order 80 details
(222, 80, 02, 3), (223, 80, 03, 2),
(224, 80, 04, 1),
-- Order 81 details
(225, 81, 05, 2), (226, 81, 06, 1),
(227, 81, 07, 3),
-- Order 82 details
(228, 82, 08, 2), (229, 82, 09, 4),
(230, 82, 10, 1),
-- Order 83 details
(231, 83, 11, 3), (232, 83, 12, 2),
(233, 83, 13, 1),
-- Order 84 details
(234, 84, 14, 2), (235, 84, 15, 1),
(236, 84, 16, 3)
SET IDENTITY_INSERT order_detail OFF;


GO
IF OBJECT_ID('tempdb..#OrderTotals') IS NOT NULL
    DROP TABLE #OrderTotals;

CREATE TABLE #OrderTotals (
                              order_id INT,
                              total_amount DECIMAL(18, 2)
);
-- Calculate the total amount for each order
INSERT INTO #OrderTotals (order_id, total_amount)
SELECT
    od.order_id,
    SUM(p.sale_price * od.quantity) AS total_amount
FROM
    order_detail od
        INNER JOIN
    product p ON od.product_id = p.id
GROUP BY
    od.order_id;

-- Update the orders table with the calculated totals
UPDATE o
SET o.total_amount = ot.total_amount
FROM
    orders o
        INNER JOIN
    #OrderTotals ot ON o.id = ot.order_id;

DROP TABLE #OrderTotals;