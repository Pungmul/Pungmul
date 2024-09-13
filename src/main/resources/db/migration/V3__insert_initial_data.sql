--  역할 초기값
INSERT INTO roles (role_name) SELECT 'USER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'USER');
INSERT INTO roles (role_name) SELECT 'REP' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'REP');
INSERT INTO roles (role_name) SELECT 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'ADMIN');
INSERT INTO roles (role_name) SELECT 'GUEST' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'GUEST');

-- 카테고리 초기 값 삽입
INSERT IGNORE INTO category (id, parent_id, name)
VALUES
(1, NULL, '자유게시판'),
(2, NULL, '학교 게시판'),
(3, NULL, '악기 게시판'),
(4, 3, '쇠'),
(5, 3, '장구'),
(6, 3, '북'),
(7, 3, '소고');
