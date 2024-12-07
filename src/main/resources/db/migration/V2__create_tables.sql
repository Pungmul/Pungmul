CREATE TABLE IF NOT EXISTS account (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        login_id VARCHAR(50) UNIQUE NOT NULL,
                        password VARCHAR(255) NOT NULL,
                        withdraw BOOLEAN DEFAULT FALSE,
                        last_password_changed TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        enabled BOOLEAN DEFAULT FALSE,
                        account_expired BOOLEAN DEFAULT FALSE,
                        account_locked BOOLEAN DEFAULT FALSE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     role_name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS account_roles (
                                             account_id BIGINT NOT NULL,
                                             role_id BIGINT NOT NULL,
                                             PRIMARY KEY (account_id, role_id),
                                             FOREIGN KEY (account_id) REFERENCES account(id) ON DELETE CASCADE,
                                             FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE if not exists tokens (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        account_id BIGINT NOT NULL,
                        token VARCHAR(255) NOT NULL,
                        token_type VARCHAR(20) NOT NULL,  -- 'access' 또는 'refresh' 값으로 설정
                        expired BOOLEAN NOT NULL,
                        revoked BOOLEAN NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (account_id) REFERENCES account(id)
);


CREATE TABLE IF NOT EXISTS image (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        converted_filename VARCHAR(225) NOT NULL,
                        original_filename VARCHAR(255) NOT NULL,
                        full_file_path VARCHAR(255) NOT NULL,
                        file_type VARCHAR(50) NOT NULL,
                        size BIGINT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS domain_image (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        domain_type VARCHAR(50) NOT NULL,
                        domain_id BIGINT NOT NULL,
                        image_id BIGINT NOT NULL,
                        user_id BIGINT NOT NULL,
                        is_primary BOOLEAN DEFAULT FALSE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (image_id) REFERENCES image(id) ON DELETE CASCADE,
                        INDEX (domain_type, domain_id)
);

CREATE TABLE IF NOT EXISTS user (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        account_id BIGINT NOT NULL,
                        name VARCHAR(20) NOT NULL,
                        club_name VARCHAR(50),
                        birth DATE NOT NULL,
                        club_age INT,
                        gender ENUM('MALE', 'FEMALE') NOT NULL,
                        phone_number VARCHAR(20) NOT NULL,
                        email VARCHAR(255) UNIQUE NOT NULL,
                        club_id BIGINT,
                        profile_image BIGINT,
                        area VARCHAR(255),
                        expired BOOLEAN DEFAULT FALSE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (account_id) REFERENCES account(id),
                        FOREIGN KEY (profile_image) REFERENCES domain_image(id)
);

CREATE TABLE IF NOT EXISTS verification_tokens (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        token VARCHAR(255) NOT NULL,
                        expired_at TIMESTAMP NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES user(id)
);


CREATE TABLE IF NOT EXISTS club (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(50) UNIQUE NOT NULL,
                        school VARCHAR(50),
                        head_id BIGINT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (head_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS instrument_status (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        instrument ENUM('KKWAENGGWARI', 'JING', 'JANGGU', 'BUK', 'SOGO', 'TAEPYUNGSO') NOT NULL,
                        instrument_ability ENUM('UNSKILLED', 'BASIC', 'INTERMEDIATE', 'ADVANCED', 'EXPERT') NOT NULL,
                        major BOOLEAN DEFAULT FALSE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES user(id),
                        UNIQUE (user_id, instrument)  -- user_id와 instrument의 조합을 고유하게 설정

);

CREATE TABLE IF NOT EXISTS category (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        parent_id BIGINT DEFAULT NULL,
                        name VARCHAR(255) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (parent_id) REFERENCES category(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS post (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        category_id BIGINT NOT NULL,
                        view_count INT DEFAULT 0,
                        like_num INT DEFAULT 0,
                        deleted BOOLEAN DEFAULT FALSE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        hidden BOOLEAN DEFAULT FALSE,
                        FOREIGN KEY (category_id) REFERENCES category(id)
    );

CREATE TABLE IF NOT EXISTS content (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        post_id BIGINT NOT NULL,
                        anonymity BOOLEAN DEFAULT FALSE,
                        title VARCHAR(255),
                        text TEXT,
                        writer_id BIGINT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (post_id) REFERENCES post(id),
                        FOREIGN KEY (writer_id) REFERENCES user(id)
    );

CREATE TABLE IF NOT EXISTS post_likes (
                                post_id BIGINT NOT NULL,                -- 좋아요가 눌린 게시물의 ID (외래키로 post 테이블 참조)
                                user_id BIGINT NOT NULL,                -- 좋아요를 누른 사용자의 ID (외래키로 user 테이블 참조)
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 좋아요를 누른 시간
                                FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
                                FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
                                UNIQUE(post_id, user_id)                -- 같은 사용자가 동일한 게시물에 중복으로 좋아요를 누를 수 없도록 유니크 제약 조건 설정
);

CREATE TABLE IF NOT EXISTS comment (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,    -- 댓글의 고유 ID
                                post_id BIGINT NOT NULL,                 -- 댓글이 달린 게시글의 ID (외래키로 post 테이블 참조)
                                user_id BIGINT,                          -- 댓글 작성자의 사용자 ID (외래키로 user 테이블 참조)
                                parent_id BIGINT DEFAULT NULL,           -- 부모 댓글의 ID (NULL이면 최상위 댓글, 외래키로 comment 테이블의 id 참조)
                                content TEXT NOT NULL,                   -- 댓글의 내용
                                deleted BOOLEAN DEFAULT FALSE,
                                likedNum INT DEFAULT 0,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 댓글 작성 시간
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 댓글 수정 시간
                                FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE, -- 게시글이 삭제되면 댓글도 삭제
                                FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE SET NULL, -- 사용자가 탈퇴해도 댓글은 남기되, user_id는 NULL로 설정
                                FOREIGN KEY (parent_id) REFERENCES comment(id) -- 부모 댓글이 삭제되도 자식 댓글은 남음
    );

CREATE TABLE IF NOT EXISTS comment_likes (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,   -- 좋아요 기록의 고유 ID
                                comment_id BIGINT NOT NULL,             -- 좋아요가 눌린 댓글의 ID (외래키로 comment 테이블 참조)
                                user_id BIGINT NOT NULL,                -- 좋아요를 누른 사용자의 ID (외래키로 user 테이블 참조)
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 좋아요를 누른 시간
                                FOREIGN KEY (comment_id) REFERENCES comment(id) ON DELETE CASCADE,
                                FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE,
                                UNIQUE(comment_id, user_id)             -- 같은 사용자가 동일한 댓글에 중복으로 좋아요를 누를 수 없도록 유니크 제약 조건 설정
);

CREATE TABLE IF NOT EXISTS chat_room (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                room_uuid varchar(36) UNIQUE NOT NULL,
                                created_by varchar(36) NOT NULL,
                                last_message_id BIGINT,  -- 마지막 메시지 ID
                                last_message_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- 마지막 메시지 시간
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- 방 생성 시간
);

CREATE TABLE IF NOT EXISTS chat_room_members (
                                 chat_room_id varchar(36) NOT NULL,
                                 user_id BIGINT NOT NULL,
                                 PRIMARY KEY (chat_room_id, user_id),
                                 FOREIGN KEY (chat_room_id) REFERENCES chat_room(room_uuid) ON DELETE CASCADE,
                                 FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS chat_messages (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                sender_username VARCHAR(255) NOT NULL,
                                receiver_username VARCHAR(255) NOT NULL,
                                content TEXT NOT NULL,
                                timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                chat_room_uuid VARCHAR(36) NOT NULL ,
                                message_type VARCHAR(50),
                                image_url VARCHAR(255)
);
CREATE TABLE IF NOT EXISTS friends (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            sender_id BIGINT NOT NULL,
                            receiver_id BIGINT NOT NULL,
                            status ENUM('PENDING', 'ACCEPTED', 'DECLINED', 'BLOCK') NOT NULL DEFAULT 'PENDING',
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                            -- 가상 열 생성
                            least_id BIGINT AS (LEAST(sender_id, receiver_id)) STORED,
                            greatest_id BIGINT AS (GREATEST(sender_id, receiver_id)) STORED,

                            -- UNIQUE 제약 조건을 가상 열에 적용
                            CONSTRAINT uq_sender_receiver UNIQUE (least_id, greatest_id),

                            -- 송신자와 수신자가 같을 수 없다는 제약 조건
                            CONSTRAINT check_sender_receiver_diff CHECK (sender_id <> receiver_id),

                            CONSTRAINT fk_friends_sender_id FOREIGN KEY (sender_id) REFERENCES user(id),
                            CONSTRAINT fk_friends_receiver_id FOREIGN KEY (receiver_id) REFERENCES user(id)
);



CREATE TABLE IF NOT EXISTS meeting (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         meeting_name VARCHAR(20) NOT NULL UNIQUE,
                         meeting_description VARCHAR(100),
                         is_public BOOLEAN DEFAULT TRUE,
                         meeting_status ENUM('CREATED', 'ACTIVE', 'DORMANT', 'CLOSED') NOT NULL,
                         founder_user_id BIGINT NOT NULL,
                         member_num INT DEFAULT 1,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         FOREIGN KEY (founder_user_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS meeting_invitation (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        meeting_id BIGINT NOT NULL,
                        founder_id BIGINT NOT NULL,
                        receiver_id BIGINT NOT NULL,
                        invitation_status ENUM('PENDING', 'ACCEPTED', 'DECLINED', 'DEFERRED') DEFAULT 'PENDING',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (meeting_id) REFERENCES meeting(id),
                        FOREIGN KEY (founder_id) REFERENCES user(id),
                        FOREIGN KEY (receiver_id) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS meeting_participant (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        meeting_id BIGINT NOT NULL,
                        user_id BIGINT NOT NULL,
                        joined_at DATE NOT NULL, -- 참가한 날짜
                        is_host BOOLEAN NOT NULL DEFAULT FALSE, -- 모임 주최 여부
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성일
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정일
                        FOREIGN KEY (meeting_id) REFERENCES meeting(id), -- 모임 ID 외래키
                        FOREIGN KEY (user_id) REFERENCES user(id) -- 사용자 ID 외래키
);

CREATE TABLE IF NOT EXISTS lightning_meeting (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 기본 키
                        meeting_name VARCHAR(255) NOT NULL, -- 모임 이름
                        meeting_description TEXT, -- 모임 설명
                        recruitment_end_time DATETIME NOT NULL, -- 모임 인원 모집 마감 시간
                        start_time DATETIME NOT NULL, -- 모임 시작 시간
                        end_time DATETIME NOT NULL, -- 모임 종료 시간
                        min_person_num INT NOT NULL, -- 최소 인원
                        max_person_num INT NOT NULL, -- 최대 인원
                        organizer_id BIGINT, -- 주최자 ID
                        meeting_type ENUM('CLASSICPAN', 'FREEPAN', 'PRACTICE', 'PLAY') NOT NULL, -- 모임 유형
                        latitude DOUBLE NOT NULL, -- 위도
                        longitude DOUBLE NOT NULL, -- 경도
                        created_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- 생성 시간
                        updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, -- 수정 시간

                        -- 외래 키 제약 조건
                        CONSTRAINT fk_lightning_meeting_organizer
                        FOREIGN KEY (organizer_id) REFERENCES user (id)
                        ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS lightning_meeting_participant (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        meeting_id BIGINT NOT NULL,
                        user_id BIGINT NOT NULL,
                        username VARCHAR(255),
                        instrument_assigned ENUM('KKWAENGGWARI', 'JING', 'JANGGU', 'BUK', 'SOGO', 'TAEPYUNGSO'), -- Enum Instrument (nullable if not assigned)
                        organizer BOOLEAN DEFAULT FALSE, -- Is this participant the organizer
                        latitude DOUBLE,
                        longitude DOUBLE,
                        status ENUM('ACTIVE', 'INACTIVE'),
                        FOREIGN KEY (meeting_id) REFERENCES lightning_meeting (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS instrument_assignment (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        meeting_id BIGINT NOT NULL,
                        instrument ENUM('KKWAENGGWARI',  'JING', 'JANGGU', 'BUK', 'SOGO', 'TAEPYUNGSO') NOT NULL, -- Enum Instrument
                        min_participants INT DEFAULT 0,
                        max_participants INT NOT NULL,
                        current_participants INT DEFAULT 0 NOT NULL,
                        FOREIGN KEY (meeting_id) REFERENCES lightning_meeting (id) ON DELETE CASCADE,
                        UNIQUE (meeting_id, instrument)
);

CREATE TABLE IF NOT EXISTS report_post (
                                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                           post_id BIGINT NOT NULL,
                                           user_id BIGINT NOT NULL,
                                           report_reason ENUM('INCITING_TROUBLE', 'PORNOGRAPHY', 'SPAM', 'DEFAMATION', 'OFF_TOPIC', 'OTHER') NOT NULL,
                                           report_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                                           UNIQUE KEY unique_report (post_id, user_id),
                                           FOREIGN KEY (post_id) REFERENCES post(id) ON DELETE CASCADE,
                                           FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_limit (
                                          user_id BIGINT NOT NULL PRIMARY KEY,
                                          post_count INT DEFAULT 0, -- 현재 게시물 작성 횟수
                                          last_reset_time DATETIME DEFAULT CURRENT_TIMESTAMP -- 마지막 초기화 시간
);

CREATE TABLE IF NOT EXISTS account_ban (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 밴 정보 고유 ID
                          username varchar(255) NOT NULL,              -- 밴 대상 사용자 ID (FK)
                          ban_reason TEXT NOT NULL,     -- 밴 사유
                          ban_start_time DATETIME DEFAULT CURRENT_TIMESTAMP,     -- 밴 시작 시간
                          ban_end_time DATETIME,                -- 밴 해제 시간 (NULL일 경우 무기한)
                          is_active BOOLEAN DEFAULT TRUE,       -- 밴 활성화 여부
                          created_at DATETIME DEFAULT CURRENT_TIMESTAMP, -- 생성 시각
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 수정 시각

);


