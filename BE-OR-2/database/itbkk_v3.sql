SET FOREIGN_KEY_CHECKS = 0;

-- Database
CREATE DATABASE IF NOT EXISTS itbkk_v3;

-- Use
USE itbkk_v3;

-- Drop
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS statuses;
DROP TABLE IF EXISTS boards;
DROP TABLE IF EXISTS users;

-- Table
CREATE TABLE IF NOT EXISTS users
(
    oid               CHAR(36) PRIMARY KEY                                          NOT NULL UNIQUE,
    name              VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    username          VARCHAR(50)                                                   NOT NULL,
    email             VARCHAR(50)                                                   NOT NULL,
    created_on        DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on        DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS boards
(
    id                CHAR(10) PRIMARY KEY                                          NOT NULL UNIQUE,
    name              VARCHAR(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    owner_id          CHAR(36)                                                      NOT NULL,
    created_on        DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on        DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES users (oid) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS statuses
(
    id                INT PRIMARY KEY AUTO_INCREMENT,
    board_id          CHAR(10)                                                      NOT NULL,
    name              VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL UNIQUE,
    description       VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    created_on        DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on        DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_board_id_2 FOREIGN KEY (board_id) REFERENCES boards (id)
);

CREATE TABLE IF NOT EXISTS tasks
(
    id                INT PRIMARY KEY AUTO_INCREMENT,
    board_id          CHAR(10)                                                      NOT NULL,
    title             VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    description       VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    assignees         VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL,
    status_id         INT                                                           NOT NULL DEFAULT 1,
    created_on        DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on        DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_status_id FOREIGN KEY (status_id) REFERENCES statuses (id),
    CONSTRAINT fk_board_id_1 FOREIGN KEY (board_id) REFERENCES boards (id)
);

-- Insert
ALTER TABLE statuses MODIFY board_id CHAR(10) NOT NULL DEFAULT '';

INSERT INTO statuses (name, description)
VALUES ('No Status', 'The default status');
INSERT INTO statuses (name, description)
VALUES ('To Do', null);
INSERT INTO statuses (name, description)
VALUES ('Doing', 'Being worked on');
INSERT INTO statuses (name, description)
VALUES ('Done', 'Finished');

ALTER TABLE tasks MODIFY board_id CHAR(10) NOT NULL DEFAULT '';

INSERT INTO tasks (title, description, assignees, status_id, created_on, updated_on)
VALUES ('TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0',
        'Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5',
        'Assignees1Assignees2Assignees3', 1, '2024-04-22 09:00:00+00:00', '2024-04-22 09:00:00+00:00');
INSERT INTO tasks (title, description, assignees, status_id, created_on, updated_on)
VALUES ('Repository', null, null, 2, '2024-04-22 09:05:00+00:00', '2024-04-22 14:00:00+00:00');
INSERT INTO tasks (title, description, assignees, status_id, created_on, updated_on)
VALUES ('ดาต้าเบส', 'ສ້າງຖານຂໍ້ມູນ', 'あなた、彼、彼女 (私ではありません)', 3, '2024-04-22 09:10:00+00:00',
        '2024-04-25 00:00:00+00:00');
INSERT INTO tasks (title, description, assignees, status_id, created_on, updated_on)
VALUES ('_Infrastructure_', '_Setup containers_', 'ไก่งวง กับ เพนกวิน', 4, '2024-04-22 09:15:00+00:00',
        '2024-04-22 10:00:00+00:00');
        
SET FOREIGN_KEY_CHECKS = 1;
