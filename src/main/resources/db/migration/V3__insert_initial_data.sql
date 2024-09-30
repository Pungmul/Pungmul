--  역할 초기값
INSERT INTO roles (role_name) SELECT 'USER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'USER');
INSERT INTO roles (role_name) SELECT 'REP' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'REP');
INSERT INTO roles (role_name) SELECT 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'ADMIN');
INSERT INTO roles (role_name) SELECT 'GUEST' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'GUEST');

-- 기본 이미지
INSERT INTO image (converted_filename, original_filename, full_file_path, file_type, size, created_at)
VALUES ('admin/ce6cfc73ef62f44510a64bc62937328f.jpg', 'anonymous_image.jpg', 'https://pungmul-s3-bucket.s3.ap-northeast-2.amazonaws.com/admin/ce6cfc73ef62f44510a64bc62937328f.jpg', 'image/jpeg', 2966, NOW())
ON DUPLICATE KEY UPDATE full_file_path = VALUES(full_file_path);


