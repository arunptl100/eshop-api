--Normalization:
--   This schema avoids data redundancy and ensures data integrity by separating product information
--   and cart information into different tables linked by a relationship table (cart_items).
--Performance:
--    Efficient use of primary and foreign keys optimizes queries related to cart
--    and product operations. Indexing the name field in the products table would enhance search capabilities.
--Flexibility and Scalability:
--    The use of JSON for labels and straightforward relational design for carts and cart items
--    allows the database to easily adapt to future requirements such as adding more product
--    attributes or cart functionalities.

drop table IF EXISTS products;
drop table IF EXISTS carts;
drop table IF EXISTS cart_items;

create TABLE products (
    product_id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL CHECK(length(name) <= 200) UNIQUE,
    price REAL NOT NULL,
    added_at TEXT NOT NULL,
    labels TEXT
);

create TABLE carts (
    cart_id INTEGER PRIMARY KEY AUTOINCREMENT,
    checked_out BOOLEAN NOT NULL DEFAULT 0  -- 0 for false, 1 for true
);


create TABLE cart_items (
    cart_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
    cart_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL CHECK(quantity > 0),
    FOREIGN KEY(cart_id) REFERENCES carts(cart_id) ON delete CASCADE,
    FOREIGN KEY(product_id) REFERENCES products(product_id) ON delete CASCADE
);


insert into products (name, price, added_at, labels) values
('Fancy IPA Beer', 5.99, '2022/09/01', '["drink", "limited"]'),
('Artisanal Sourdough Bread', 3.50, '2022/09/02', '["food"]'),
('Organic Blueberry Jam', 4.25, '2022/08/25', '["food"]'),
('Handcrafted Wool Sweater', 45.00, '2022/10/10', '["clothes"]'),
('Limited Edition Coffee Mug', 12.00, '2022/11/01', '["drink", "limited"]'),
('Vintage Vinyl Record', 17.99, '2022/07/15', '["limited"]');

insert into carts (checked_out) values
(FALSE),  -- Cart 1: Not checked out
(TRUE),   -- Cart 2: Checked out
(FALSE);  -- Cart 3: Not checked out

insert into cart_items (cart_id, product_id, quantity) values
(1, 1, 2),   -- Cart 1: 2 x Fancy IPA Beer
(1, 3, 1),   -- Cart 1: 1 x Organic Blueberry Jam
(2, 2, 3),   -- Cart 2: 3 x Artisanal Sourdough Bread
(2, 4, 1),   -- Cart 2: 1 x Handcrafted Wool Sweater
(3, 5, 1),   -- Cart 3: 1 x Limited Edition Coffee Mug
(3, 6, 2);   -- Cart 3: 2 x Vintage Vinyl Record


