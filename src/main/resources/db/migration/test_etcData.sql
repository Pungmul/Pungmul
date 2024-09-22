-- 패 초기값 삽입
INSERT IGNORE INTO club (name, school, head_id,created_at, updated_at)
VALUES
    ('어흥', '상명대',null, NOW(), NOW()),
    ('하날다래', '이화여대', null, NOW(), NOW()),
    ('악반', '홍대', null, NOW(), NOW()),
    ('떼', '연대', null, NOW(), NOW()),
    ('푸른소래', '고대', null, NOW(), NOW());

-- 카테고리 초기 값 삽입
INSERT IGNORE INTO category (id, parent_id, name)
VALUES
(1, NULL, '자유 게시판'),
(2, NULL, '학교 게시판'),
(3, NULL, '악기 게시판'),
(4, 3, '쇠'),
(5, 3, '장구'),
(6, 3, '북'),
(7, 3, '소고');
