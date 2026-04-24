CREATE TABLE IF NOT EXISTS ct_user (
    user_id BIGINT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    nickname VARCHAR(64) NOT NULL,
    avatar_url VARCHAR(255) NULL,
    phone VARCHAR(32) NULL UNIQUE,
    email VARCHAR(128) NULL UNIQUE,
    school VARCHAR(128) NULL,
    campus VARCHAR(128) NULL,
    bio VARCHAR(500) NULL,
    verified TINYINT(1) NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS ct_seller_profile (
    user_id BIGINT PRIMARY KEY,
    credit_score INT NOT NULL DEFAULT 60,
    product_count INT NOT NULL DEFAULT 0,
    sold_count INT NOT NULL DEFAULT 0,
    online TINYINT(1) NOT NULL DEFAULT 0,
    joined_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_seller_profile_user FOREIGN KEY (user_id) REFERENCES ct_user (user_id)
);

CREATE TABLE IF NOT EXISTS ct_user_credential (
    user_id BIGINT PRIMARY KEY,
    password_hash VARCHAR(512) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_user_credential_user FOREIGN KEY (user_id) REFERENCES ct_user (user_id)
);

CREATE TABLE IF NOT EXISTS ct_user_session (
    token VARCHAR(128) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    remember_me TINYINT(1) NOT NULL DEFAULT 0,
    issued_at DATETIME NOT NULL,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_user_session_user_id (user_id),
    INDEX idx_user_session_expires_at (expires_at),
    CONSTRAINT fk_user_session_user FOREIGN KEY (user_id) REFERENCES ct_user (user_id)
);
