INSERT INTO ct_user (user_id, username, nickname, avatar_url, phone, email, school, campus, bio, verified, status, created_at, updated_at)
VALUES
    (10001, 'alice01', 'Alice', 'https://example.com/avatar/alice.png', '13800000001', 'alice@campus.edu', 'Campus University', 'Main Campus', 'Love electronics and books.', 1, 'ACTIVE', '2025-04-01 10:00:00', '2026-04-20 09:20:00'),
    (10002, 'bob02', 'Bob', 'https://example.com/avatar/bob.png', '13800000002', 'bob@campus.edu', 'Campus University', 'North Campus', 'Sports gear reseller.', 0, 'INACTIVE', '2025-08-12 11:00:00', '2026-04-10 18:00:00'),
    (10003, 'charlie03', 'Charlie', 'https://example.com/avatar/charlie.png', '13800000003', 'charlie@campus.edu', 'Campus University', 'Main Campus', 'Prefer face-to-face trade.', 1, 'BANNED', '2024-07-03 08:20:00', '2026-03-15 12:00:00')
ON DUPLICATE KEY UPDATE
    username = VALUES(username),
    nickname = VALUES(nickname),
    avatar_url = VALUES(avatar_url),
    phone = VALUES(phone),
    email = VALUES(email),
    school = VALUES(school),
    campus = VALUES(campus),
    bio = VALUES(bio),
    verified = VALUES(verified),
    status = VALUES(status),
    updated_at = VALUES(updated_at);

INSERT INTO ct_seller_profile (user_id, credit_score, product_count, sold_count, online, joined_at, updated_at)
VALUES
    (10001, 96, 18, 57, 0, '2025-04-01 10:00:00', '2026-04-20 09:20:00'),
    (10002, 78, 4, 12, 0, '2025-08-12 11:00:00', '2026-04-10 18:00:00'),
    (10003, 42, 2, 3, 0, '2024-07-03 08:20:00', '2026-03-15 12:00:00')
ON DUPLICATE KEY UPDATE
    credit_score = VALUES(credit_score),
    product_count = VALUES(product_count),
    sold_count = VALUES(sold_count),
    online = VALUES(online),
    joined_at = VALUES(joined_at),
    updated_at = VALUES(updated_at);

-- Default password for seeded users: Test@12345
INSERT INTO ct_user_credential (user_id, password_hash, created_at, updated_at)
VALUES
    (10001, 'pbkdf2$120000$grZKVVWdjwqwbvhcyIP7Xg==$6GPlZXJSI2r6COGlJaaoWZQvDEMTIFqyofFkvqbLi2A=', '2026-04-24 00:00:00', '2026-04-24 00:00:00'),
    (10002, 'pbkdf2$120000$F4d3ue8s2tSSbWvlg0KgaQ==$gTB1r2Q8aJoF8tEjLBlDseMCvHA3EAYtyH8lQxf7/8I=', '2026-04-24 00:00:00', '2026-04-24 00:00:00'),
    (10003, 'pbkdf2$120000$99uRgkMiQoVbv8yqxNj3iQ==$z7jdnE769oeBARjolojN3B9f/5iwRDG3qZYsaDrN0Q4=', '2026-04-24 00:00:00', '2026-04-24 00:00:00')
ON DUPLICATE KEY UPDATE
    password_hash = VALUES(password_hash),
    updated_at = VALUES(updated_at);
