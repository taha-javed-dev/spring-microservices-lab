-- Orders Table Data
INSERT INTO orders (order_status, total_price)
VALUES
('PENDING', 2499.98),
('CONFIRMED', 7999.50),
('PENDING', 1549.75),
('DELIVERED', 12499.00),
('CANCELLED', 3499.99);

-- Order Items Table Data
INSERT INTO order_item (product_id, quantity, order_id)
VALUES
-- Order 1
(101, 2, 1),
(105, 1, 1),

-- Order 2
(102, 1, 2),
(108, 3, 2),

-- Order 3
(110, 5, 3),

-- Order 4
(115, 1, 4),
(117, 2, 4),

-- Order 5
(120, 1, 5),
(122, 4, 5);