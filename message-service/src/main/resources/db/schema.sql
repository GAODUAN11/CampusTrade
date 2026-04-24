CREATE TABLE IF NOT EXISTS ct_message_product_snapshot (
    product_id BIGINT PRIMARY KEY,
    title VARCHAR(128) NOT NULL,
    cover_image_url VARCHAR(255) NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS ct_message_conversation (
    conversation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    product_id BIGINT NOT NULL,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    product_title VARCHAR(128) NOT NULL,
    product_cover_image_url VARCHAR(255) NULL,
    last_message_preview VARCHAR(255) NULL,
    last_sender_id BIGINT NULL,
    last_message_time DATETIME NOT NULL,
    archived TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_message_conversation_buyer (buyer_id),
    INDEX idx_message_conversation_seller (seller_id),
    INDEX idx_message_conversation_product (product_id),
    INDEX idx_message_conversation_last_time (last_message_time)
);

CREATE TABLE IF NOT EXISTS ct_message (
    message_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id BIGINT NOT NULL,
    sender_id BIGINT NOT NULL,
    receiver_id BIGINT NOT NULL,
    message_type VARCHAR(32) NOT NULL,
    content VARCHAR(1000) NOT NULL,
    is_read TINYINT(1) NOT NULL DEFAULT 0,
    sent_at DATETIME NOT NULL,
    read_at DATETIME NULL,
    CONSTRAINT fk_message_conversation FOREIGN KEY (conversation_id) REFERENCES ct_message_conversation (conversation_id),
    INDEX idx_message_conversation_time (conversation_id, sent_at),
    INDEX idx_message_receiver_read (receiver_id, is_read)
);
