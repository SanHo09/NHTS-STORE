INSERT INTO category (name) VALUES
                                ('Beverages'),
                                ('Snacks'),
                                ('Dairy Products'),
                                ('Bakery'),
                                ('Personal Care'),
                                ('Frozen Foods'),
                                ('Fresh Produce'),
                                ('Meat & Seafood'),
                                ('Health Supplements'),
                                ('Household Items'),
                                ('Ready-to-Eat Meals'),
                                ('Condiments & Sauces'),
                                ('Instant Foods'),
                                ('Coffee & Tea'),
                                ('Ice Cream'),
                                ('Pet Supplies'),
                                ('Baby Products'),
                                ('Beauty Products'),
                                ('Cleaning Supplies'),
                                ('Canned Goods'),
                                ('Cereals & Breakfast Foods'),
                                ('Kitchen Essentials'),
                                ('Mobile Accessories'),
                                ('Magazines & Books'),
                                ('Seasonal Items');

INSERT INTO supplier (name, address) VALUES
                                         ('Vinamilk', 'Ho Chi Minh City, Vietnam'),
                                         ('PepsiCo', 'New York, USA'),
                                         ('Coca-Cola Co.', 'Atlanta, USA'),
                                         ('Nestle', 'Vevey, Switzerland'),
                                         ('Family Mart Vietnam', 'Ho Chi Minh City, Vietnam'),
                                         ('Circle K Vietnam', 'Ho Chi Minh City, Vietnam'),
                                         ('TH True Milk', 'Nghe An, Vietnam'),
                                         ('Unilever', 'London, UK'),
                                         ('P&G', 'Ohio, USA'),
                                         ('Mars Inc.', 'Virginia, USA'),
                                         ('Glico', 'Osaka, Japan'),
                                         ('FrieslandCampina', 'Amersfoort, Netherlands'),
                                         ('Meiji Co.', 'Tokyo, Japan'),
                                         ('Colgate-Palmolive', 'New York, USA'),
                                         ('Johnson & Johnson', 'New Jersey, USA'),
                                         ('Beiersdorf', 'Hamburg, Germany'),
                                         ('Local Bakery Co.', 'Ho Chi Minh City, Vietnam'),
                                         ('Fresh Farm', 'Dong Nai, Vietnam'),
                                         ('Seafood Market', 'Vung Tau, Vietnam'),
                                         ('PetCare Supplies', 'Singapore'),
                                         ('Happy Baby Co.', 'Hanoi, Vietnam'),
                                         ('BeautyPro', 'Seoul, South Korea'),
                                         ('Clean & Fresh', 'Tokyo, Japan'),
                                         ('Morning Star', 'California, USA'),
                                         ('Green Valley', 'Da Lat, Vietnam'),
                                         ('Mondelez', 'Illinois, USA'),
                                         ('HealthCare', 'Unknown Location');

INSERT INTO product (name, sale_price, purchase_price, manufacture_date, expiry_date, manufacturer, quantity, supplier_id, category_id)
VALUES
    ('Coca-Cola Can 330ml', 1.20, 0.80, '2025-03-01', '2026-03-01', 'Coca-Cola Co.', 200,
     (SELECT id FROM supplier WHERE name = 'Coca-Cola Co.'),
     (SELECT id FROM category WHERE name = 'Beverages')
    ),
    ('Pepsi Bottle 500ml', 1.30, 0.85, '2025-03-10', '2026-03-10', 'PepsiCo', 180,
     (SELECT id FROM supplier WHERE name = 'PepsiCo'),
     (SELECT id FROM category WHERE name = 'Beverages')
    ),
    ('Aquafina Water 500ml', 0.90, 0.60, '2025-03-15', '2026-03-15', 'PepsiCo', 250,
     (SELECT id FROM supplier WHERE name = 'PepsiCo'),
     (SELECT id FROM category WHERE name = 'Beverages')
    ),
    ('Red Bull 250ml', 1.80, 1.20, '2025-04-01', '2026-04-01', 'Red Bull', 150,
     (SELECT id FROM supplier WHERE name = 'PepsiCo'),  -- Assuming PepsiCo distributes Red Bull here
     (SELECT id FROM category WHERE name = 'Beverages')
    ),
    ('Lipton Green Tea 450ml', 1.50, 1.00, '2025-03-05', '2026-03-05', 'Unilever', 140,
     (SELECT id FROM supplier WHERE name = 'Unilever'),
     (SELECT id FROM category WHERE name = 'Beverages')
    ),
    ('Oreo Cookies 133g', 1.60, 1.10, '2025-02-01', '2025-09-01', 'Mondelez', 220,
     (SELECT id FROM supplier WHERE name = 'Mondelez'),
     (SELECT id FROM category WHERE name = 'Snacks')
    ),
    ('KitKat Chocolate 45g', 1.20, 0.90, '2025-01-20', '2025-09-20', 'Nestle', 180,
     (SELECT id FROM supplier WHERE name = 'Nestle'),
     (SELECT id FROM category WHERE name = 'Snacks')
    ),
    ('Pocky Strawberry', 2.20, 1.50, '2025-03-10', '2025-09-10', 'Glico', 150,
     (SELECT id FROM supplier WHERE name = 'Glico'),
     (SELECT id FROM category WHERE name = 'Snacks')
    ),
    ('Lays Classic 170g', 2.50, 1.70, '2025-02-15', '2025-08-15', 'PepsiCo', 180,
     (SELECT id FROM supplier WHERE name = 'PepsiCo'),
     (SELECT id FROM category WHERE name = 'Snacks')
    ),
    ('M&M''s Peanut 100g', 1.90, 1.30, '2025-01-10', '2025-08-10', 'Mars Inc.', 160,
     (SELECT id FROM supplier WHERE name = 'Mars Inc.'),
     (SELECT id FROM category WHERE name = 'Snacks')
    ),
    ('Vinamilk Fresh Milk 1L', 1.80, 1.30, '2025-04-01', '2025-05-01', 'Vinamilk', 120,
     (SELECT id FROM supplier WHERE name = 'Vinamilk'),
     (SELECT id FROM category WHERE name = 'Dairy Products')
    ),
    ('Dutch Lady Yogurt Cup', 0.80, 0.50, '2025-04-10', '2025-05-10', 'FrieslandCampina', 90,
     (SELECT id FROM supplier WHERE name = 'FrieslandCampina'),
     (SELECT id FROM category WHERE name = 'Dairy Products')
    ),
    ('TH True Milk 1L', 1.85, 1.25, '2025-03-25', '2025-05-25', 'TH True Milk', 100,
     (SELECT id FROM supplier WHERE name = 'TH True Milk'),
     (SELECT id FROM category WHERE name = 'Dairy Products')
    ),
    ('Meiji Chocolate Milk 200ml', 1.50, 1.00, '2025-04-10', '2025-06-10', 'Meiji Co.', 80,
     (SELECT id FROM supplier WHERE name = 'Meiji Co.'),
     (SELECT id FROM category WHERE name = 'Dairy Products')
    ),
    ('Yomost Yogurt Strawberry', 1.00, 0.70, '2025-04-01', '2025-05-01', 'Vinamilk', 95,
     (SELECT id FROM supplier WHERE name = 'Vinamilk'),
     (SELECT id FROM category WHERE name = 'Dairy Products')
    ),
    ('Family Mart Croissant', 0.90, 0.60, '2025-04-15', '2025-04-20', 'Family Mart', 60,
     (SELECT id FROM supplier WHERE name = 'Family Mart Vietnam'),
     (SELECT id FROM category WHERE name = 'Bakery')
    ),
    ('Circle K Donut Glazed', 1.20, 0.80, '2025-04-16', '2025-04-21', 'Circle K', 50,
     (SELECT id FROM supplier WHERE name = 'Circle K Vietnam'),
     (SELECT id FROM category WHERE name = 'Bakery')
    ),
    ('Whole Wheat Bread', 1.80, 1.10, '2025-04-10', '2025-04-20', 'Local Bakery Co.', 40,
     (SELECT id FROM supplier WHERE name = 'Local Bakery Co.'),
     (SELECT id FROM category WHERE name = 'Bakery')
    ),
    ('Ice Cream Vanilla Cup', 2.00, 1.40, '2025-03-01', '2025-09-01', 'Nestle', 100,
     (SELECT id FROM supplier WHERE name = 'Nestle'),
     (SELECT id FROM category WHERE name = 'Ice Cream')
    ),
    ('Frozen Pizza Pepperoni', 4.50, 3.20, '2025-03-15', '2025-09-15', 'Family Mart', 70,
     (SELECT id FROM supplier WHERE name = 'Family Mart Vietnam'),
     (SELECT id FROM category WHERE name = 'Frozen Foods')
    ),
    ('Fresh Lettuce 500g', 1.50, 1.00, '2025-04-10', '2025-04-17', 'Fresh Farm', 60,
     (SELECT id FROM supplier WHERE name = 'Fresh Farm'),
     (SELECT id FROM category WHERE name = 'Fresh Produce')
    ),
    ('Salmon Fillet 200g', 5.50, 4.20, '2025-04-05', '2025-04-10', 'Seafood Market', 30,
     (SELECT id FROM supplier WHERE name = 'Seafood Market'),
     (SELECT id FROM category WHERE name = 'Meat & Seafood')
    ),
    ('Vitamin C Tablets', 3.00, 2.00, '2025-01-01', '2027-01-01', 'HealthCare', 100,
     (SELECT id FROM supplier WHERE name = 'HealthCare'),
     (SELECT id FROM category WHERE name = 'Health Supplements')
    ),
    ('Colgate Toothpaste 100g', 1.50, 1.00, '2025-01-01', '2027-01-01', 'Colgate-Palmolive', 120,
     (SELECT id FROM supplier WHERE name = 'Colgate-Palmolive'),
     (SELECT id FROM category WHERE name = 'Personal Care')
    ),
    ('Pantene Shampoo 300ml', 3.50, 2.40, '2025-02-01', '2027-02-01', 'P&G', 90,
     (SELECT id FROM supplier WHERE name = 'P&G'),
     (SELECT id FROM category WHERE name = 'Personal Care')
    );