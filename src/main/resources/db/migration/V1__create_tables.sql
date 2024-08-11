CREATE TABLE if not exists account  (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         login_id VARCHAR(50) UNIQUE NOT NULL,
                         password VARCHAR(255) NOT NULL,
                         withdraw BOOLEAN DEFAULT TRUE,
                         last_password_changed TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                         enabled BOOLEAN DEFAULT TRUE,
                         account_expired BOOLEAN DEFAULT FALSE,
                         account_locked BOOLEAN DEFAULT FALSE,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

);

CREATE TABLE if not exists user (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        account_id BIGINT NOT NULL,
                        name VARCHAR(20) NOT NULL,
                        club_name VARCHAR(50),
                        birth DATE NOT NULL,
                        club_age INT,
                        gender ENUM('MALE', 'FEMALE') NOT NULL,
                        phone_number VARCHAR(20) NOT NULL,
                        email VARCHAR(50) UNIQUE NOT NULL,
                        club_id BIGINT,
                        profile_image BIGINT,
                        area VARCHAR(255),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (account_id) REFERENCES account(id)
);

CREATE TABLE if not exists club (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(50) UNIQUE NOT NULL,
                      school VARCHAR(50),
                      head_id BIGINT,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      FOREIGN KEY (head_id) REFERENCES user(id)
);

CREATE TABLE if not exists instrument_status (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        instrument ENUM('KKWAENGGWARI', 'JING', 'JANGGU', 'BUK', 'SOGO', 'TAEPYUNGSO') NOT NULL,
                        instrument_ability ENUM('UNSKILLED', 'BASIC', 'INTERMEDIATE', 'ADVANCED', 'EXPERT') NOT NULL,
                        major BOOLEAN DEFAULT FALSE,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (user_id) REFERENCES user(id)
);

CREATE TABLE if not exists post (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            writer_id BIGINT NOT NULL,
                            category_id BIGINT NOT NULL,
                            title VARCHAR(255) NOT NULL,
                            view_count INT DEFAULT 0,
                            like_num INT DEFAULT 0,
                            deleted BOOLEAN DEFAULT FALSE,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (writer_id) REFERENCES user(id),
                            FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE if not exists content (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            post_id BIGINT NOT NULL,
                            anonymity BOOLEAN DEFAULT FALSE,
                            title VARCHAR(255),
                            text TEXT,
                            writer_id BIGINT NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            FOREIGN KEY (post_id) REFERENCES post(id)
                    );

CREATE TABLE if not exists image (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        converted_filename VARCHAR(225) NOT NULL,
                        original_filename VARCHAR(255) NOT NULL,
                        full_file_path VARCHAR(255) NOT NULL,
                        file_type VARCHAR(50) NOT NULL,
                        size BIGINT NOT NULL,
                        userId BIGINT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (userId) REFERENCES user(id)
);

CREATE TABLE IF NOT EXISTS domain_image (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        domain_type VARCHAR(50) NOT NULL,  -- 도메인 타입
                        domain_id BIGINT NOT NULL,         -- 도메인 객체의 ID
                        image_id BIGINT NOT NULL,          -- 이미지 ID
                        is_primary BOOLEAN DEFAULT FALSE,  -- 기본 이미지 여부
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        FOREIGN KEY (image_id) REFERENCES image(id) ON DELETE CASCADE,
                        INDEX (domain_type, domain_id)     -- 조회 성능을 위한 복합 인덱스
);

CREATE TABLE IF NOT EXISTS category (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        parent_id BIGINT DEFAULT NULL,  -- 대분류의 경우 NULL, 소분류는 대분류의 ID를 참조
                                        name VARCHAR(255) NOT NULL,     -- 카테고리 이름
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                        FOREIGN KEY (parent_id) REFERENCES category(id) ON DELETE SET NULL
);


