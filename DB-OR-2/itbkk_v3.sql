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
    visibility        ENUM('PRIVATE', 'PUBLIC')                                     NOT NULL DEFAULT 'PRIVATE',
    owner_id          CHAR(36)                                                      NOT NULL,
    created_on        DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on        DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_owner_id FOREIGN KEY (owner_id) REFERENCES users (oid) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS statuses
(
    id                INT PRIMARY KEY AUTO_INCREMENT,
    board_id          CHAR(10)                                                      ,
    name              VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NOT NULL,
    description       VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    created_on        DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on        DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_board_id_2 FOREIGN KEY (board_id) REFERENCES boards (id) ON DELETE CASCADE
    );

CREATE TABLE IF NOT EXISTS tasks
(
    id                INT PRIMARY KEY AUTO_INCREMENT,
    board_id          CHAR(10)                                                      NOT NULL,
    title             VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    description       VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    assignees         VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL,
    status_id         INT                                                           NOT NULL,
    created_on        DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_on        DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_status_id FOREIGN KEY (status_id) REFERENCES statuses (id),
    CONSTRAINT fk_board_id_1 FOREIGN KEY (board_id) REFERENCES boards (id) ON DELETE CASCADE
);

INSERT INTO statuses (board_id, name, description)
VALUES (null, 'No Status', 'Default status for new boards'),
       (null, 'To Do', 'Tasks that are yet to be started'),
       (null, 'Doing', 'Tasks that are in progress'),
       (null, 'Done', 'Tasks that have been completed');

SET FOREIGN_KEY_CHECKS = 1;
