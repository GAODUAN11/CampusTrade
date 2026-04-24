CREATE TABLE IF NOT EXISTS ct_favorite (
    favorite_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    UNIQUE KEY uk_ct_favorite_user_product (user_id, product_id),
    INDEX idx_ct_favorite_user_created_at (user_id, created_at)
);

CREATE TABLE IF NOT EXISTS ct_favorite_product_snapshot (
    product_id BIGINT PRIMARY KEY,
    title VARCHAR(128) NOT NULL,
    cover_image_url VARCHAR(255) NULL,
    price DECIMAL(12, 2) NOT NULL,
    status VARCHAR(32) NOT NULL,
    updated_at DATETIME NOT NULL
);
