--  역할 초기값
INSERT INTO roles (role_name) SELECT 'USER' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'USER');
INSERT INTO roles (role_name) SELECT 'REP' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'REP');
INSERT INTO roles (role_name) SELECT 'ADMIN' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'ADMIN');
INSERT INTO roles (role_name) SELECT 'GUEST' WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_name = 'GUEST');

