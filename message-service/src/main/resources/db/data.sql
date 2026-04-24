INSERT INTO ct_message_product_snapshot (product_id, title, cover_image_url, updated_at)
VALUES
    (20001, 'iPad 9 64G', 'https://example.com/product/ipad-9.jpg', '2026-04-24 09:00:00'),
    (20002, 'Road Bike', 'https://example.com/product/road-bike.jpg', '2026-04-24 09:00:00'),
    (20004, 'Desk Lamp', 'https://example.com/product/desk-lamp.jpg', '2026-04-24 09:00:00')
ON DUPLICATE KEY UPDATE
    title = VALUES(title),
    cover_image_url = VALUES(cover_image_url),
    updated_at = VALUES(updated_at);

INSERT INTO ct_message_conversation (
    conversation_id, product_id, buyer_id, seller_id, product_title, product_cover_image_url,
    last_message_preview, last_sender_id, last_message_time, archived, created_at, updated_at
)
VALUES
    (
        40001, 20001, 10001, 20001, 'iPad 9 64G', 'https://example.com/product/ipad-9.jpg',
        'Yes, still available.', 20001, '2026-04-23 10:05:00', 0, '2026-04-23 10:00:00', '2026-04-23 10:05:00'
    ),
    (
        40002, 20004, 10002, 20002, 'Desk Lamp', 'https://example.com/product/desk-lamp.jpg',
        'Can you lower the price?', 10002, '2026-04-23 12:00:00', 0, '2026-04-23 12:00:00', '2026-04-23 12:00:00'
    )
ON DUPLICATE KEY UPDATE
    product_id = VALUES(product_id),
    buyer_id = VALUES(buyer_id),
    seller_id = VALUES(seller_id),
    product_title = VALUES(product_title),
    product_cover_image_url = VALUES(product_cover_image_url),
    last_message_preview = VALUES(last_message_preview),
    last_sender_id = VALUES(last_sender_id),
    last_message_time = VALUES(last_message_time),
    archived = VALUES(archived),
    updated_at = VALUES(updated_at);

INSERT INTO ct_message (
    message_id, conversation_id, sender_id, receiver_id, message_type, content, is_read, sent_at, read_at
)
VALUES
    (80001, 40001, 10001, 20001, 'TEXT', 'Hi, is this still available?', 0, '2026-04-23 10:00:00', NULL),
    (80002, 40001, 20001, 10001, 'TEXT', 'Yes, still available.', 0, '2026-04-23 10:05:00', NULL),
    (80003, 40002, 10002, 20002, 'TEXT', 'Can you lower the price?', 0, '2026-04-23 12:00:00', NULL)
ON DUPLICATE KEY UPDATE
    conversation_id = VALUES(conversation_id),
    sender_id = VALUES(sender_id),
    receiver_id = VALUES(receiver_id),
    message_type = VALUES(message_type),
    content = VALUES(content),
    is_read = VALUES(is_read),
    sent_at = VALUES(sent_at),
    read_at = VALUES(read_at);
