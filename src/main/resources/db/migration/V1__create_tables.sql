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
                      status ENUM('DRAFT', 'PUBLISH', 'ARCHIVE', 'DELETE') NOT NULL,
                      like_num INT DEFAULT 0,
                      anonymity BOOLEAN DEFAULT FALSE,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                      FOREIGN KEY (writer_id) REFERENCES user(id)
    -- FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE if not exists content (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         post_id BIGINT NOT NULL,
                         text TEXT,
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

CREATE TABLE if not exists content_image (
                               content_id BIGINT NOT NULL,
                               image_id BIGINT NOT NULL,
                               PRIMARY KEY (content_id, image_id),
                               FOREIGN KEY (content_id) REFERENCES content(id),
                               FOREIGN KEY (image_id) REFERENCES image(id)
);
