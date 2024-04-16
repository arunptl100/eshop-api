DROP TABLE IF EXISTS products;

CREATE TABLE products (
    product_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL CHECK(length(name) <= 200) UNIQUE,
    price REAL NOT NULL,
    added_at TEXT NOT NULL,
    labels TEXT
);

INSERT INTO products (name, price, added_at, labels) VALUES
('Fancy IPA Beer', 5.99, '2022/09/01', '["drink", "limited"]'),
('Artisanal Sourdough Bread', 3.50, '2022/09/02', '["food"]'),
('Organic Blueberry Jam', 4.25, '2022/08/25', '["food"]'),
('Handcrafted Wool Sweater', 45.00, '2022/10/10', '["clothes"]'),
('Limited Edition Coffee Mug', 12.00, '2022/11/01', '["drink", "limited"]'),
('Vintage Vinyl Record', 17.99, '2022/07/15', '["limited"]');

