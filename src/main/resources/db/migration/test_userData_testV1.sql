INSERT INTO account (login_id, password, withdraw, enabled, account_expired, account_locked, created_at, updated_at)
VALUES
    ('user1@example.com', 'password123', FALSE, TRUE, FALSE, FALSE, NOW(), NOW()),
    ('user2@example.com', 'password123', FALSE, TRUE, FALSE, FALSE, NOW(), NOW()),
    ('user3@example.com', 'password123', FALSE, TRUE, FALSE, FALSE, NOW(), NOW()),
    ('user4@example.com', 'password123', FALSE, TRUE, FALSE, FALSE, NOW(), NOW()),
    ('user5@example.com', 'password123', FALSE, TRUE, FALSE, FALSE, NOW(), NOW()),
    ('user6@example.com', 'password123', FALSE, TRUE, FALSE, FALSE, NOW(), NOW()),
    ('user7@example.com', 'password123', FALSE, TRUE, FALSE, FALSE, NOW(), NOW()),
    ('user8@example.com', 'password123', FALSE, TRUE, FALSE, FALSE, NOW(), NOW()),
    ('user9@example.com', 'password123', FALSE, TRUE, FALSE, FALSE, NOW(), NOW()),
    ('user10@example.com', 'password123', FALSE, TRUE, FALSE, FALSE, NOW(), NOW());

INSERT INTO user (account_id, name, club_name, birth, club_age, gender, phone_number, email, club_id, area, created_at, updated_at)
VALUES
    (1, '홍길동', '어흥', '1995-05-15', 5, 'MALE', '010-1234-5678', 'user1@example.com', 6, 'Seoul', NOW(), NOW()),
    (2, '이순신', '하날다래', '1994-11-05', 4, 'MALE', '010-2234-5678', 'user2@example.com', 7, 'Incheon', NOW(), NOW()),
    (3, '유관순', '악반', '1997-08-25', 3, 'FEMALE', '010-3234-5678', 'user3@example.com', 8, 'Busan', NOW(), NOW()),
    (4, '장보고', '떼', '1993-03-14', 6, 'MALE', '010-4234-5678', 'user4@example.com', 9, 'Gwangju', NOW(), NOW()),
    (5, '신사임당', '푸른소래', '1996-07-17', 2, 'FEMALE', '010-5234-5678', 'user5@example.com', 10, 'Daejeon', NOW(), NOW()),
    (6, '정약용', '어흥', '1995-12-21', 5, 'MALE', '010-6234-5678', 'user6@example.com', 6, 'Seoul', NOW(), NOW()),
    (7, '안중근', '하날다래', '1997-02-03', 4, 'MALE', '010-7234-5678', 'user7@example.com', 7, 'Incheon', NOW(), NOW()),
    (8, '김유신', '악반', '1994-10-11', 3, 'MALE', '010-8234-5678', 'user8@example.com', 8, 'Busan', NOW(), NOW()),
    (9, '세종대왕', '떼', '1992-06-12', 6, 'MALE', '010-9234-5678', 'user9@example.com', 9, 'Gwangju', NOW(), NOW()),
    (10, '이성계', '푸른소래', '1990-09-09', 2, 'MALE', '010-0334-5678', 'user10@example.com', 10, 'Daejeon', NOW(), NOW());

INSERT INTO account_roles (account_id, role_id)
VALUES
    (1, 1), -- USER
    (2, 1), -- USER
    (3, 4), -- GUEST
    (4, 2), -- REP
    (5, 1), -- USER
    (6, 3), -- ADMIN
    (7, 1), -- USER
    (8, 2), -- REP
    (9, 1), -- USER
    (10, 1); -- USER

INSERT INTO instrument_status (user_id, instrument, instrument_ability, major, created_at, updated_at)
VALUES
    (1, 'JANGGU', 'INTERMEDIATE', FALSE, NOW(), NOW()),
    (2, 'BUK', 'ADVANCED', TRUE, NOW(), NOW()),
    (3, 'KKWAENGGWARI', 'BASIC', FALSE, NOW(), NOW()),
    (4, 'JING', 'EXPERT', TRUE, NOW(), NOW()),
    (5, 'TAEPYUNGSO', 'BASIC', FALSE, NOW(), NOW()),
    (6, 'SOGO', 'INTERMEDIATE', FALSE, NOW(), NOW()),
    (7, 'JING', 'ADVANCED', TRUE, NOW(), NOW()),
    (8, 'KKWAENGGWARI', 'EXPERT', TRUE, NOW(), NOW()),
    (9, 'BUK', 'BASIC', FALSE, NOW(), NOW()),
    (10, 'TAEPYUNGSO', 'INTERMEDIATE', TRUE, NOW(), NOW());
