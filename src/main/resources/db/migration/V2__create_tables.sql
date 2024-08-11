CREATE TABLE IF NOT EXISTS account (
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

CREATE TABLE IF NOT EXISTS image (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    converted_filename VARCHAR(225) NOT NULL,
                                    original_filename VARCHAR(255) NOT NULL,
                                    full_file_path VARCHAR(255) NOT NULL,
                                    file_type VARCHAR(50) NOT NULL,
                                    size BIGINT NOT NULL,
                                    user_id BIGINT NOT NULL,
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS domain_image (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            domain_type VARCHAR(50) NOT NULL,
                                            domain_id BIGINT NOT NULL,
                                            image_id BIGINT NOT NULL,
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
                                    email VARCHAR(50) UNIQUE NOT NULL,
                                    club_id BIGINT,
                                    profile_image BIGINT,
                                    area VARCHAR(255),
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                    FOREIGN KEY (account_id) REFERENCES account(id),
                                    FOREIGN KEY (profile_image) REFERENCES domain_image(id)
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
                                                 FOREIGN KEY (user_id) REFERENCES user(id)
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
