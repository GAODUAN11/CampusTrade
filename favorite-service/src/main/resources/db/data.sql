INSERT INTO ct_favorite_product_snapshot (product_id, title, cover_image_url, price, status, updated_at)
VALUES
    (20001, 'iPad 9 64G', 'https://example.com/product/ipad-9.jpg', 1650.00, 'ON_SALE', '2026-04-24 09:00:00'),
    (20002, 'Road Bike', 'https://example.com/product/road-bike.jpg', 780.00, 'ON_SALE', '2026-04-24 09:00:00'),
    (20003, 'Java Book Set', 'https://example.com/product/java-book-set.jpg', 120.00, 'OFF_SHELF', '2026-04-24 09:00:00'),
    (20004, 'Desk Lamp', 'https://example.com/product/desk-lamp.jpg', 55.00, 'ON_SALE', '2026-04-24 09:00:00')
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    cover_image_url = VALUES(cover_image_url),
    price = VALUES(price),
    status = VALUES(status),
    updated_at = VALUES(updated_at);

INSERT INTO ct_favorite (favorite_id, user_id, product_id, created_at)
VALUES
    (30001, 10001, 20001, '2026-04-23 10:00:00'),
    (30002, 10001, 20002, '2026-04-23 11:00:00'),
    (30003, 10002, 20004, '2026-04-23 12:00:00')
ON DUPLICATE KEY UPDATE
    created_at = VALUES(created_at);
