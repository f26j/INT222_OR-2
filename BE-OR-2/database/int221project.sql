-- Database
CREATE DATABASE IF NOT EXISTS int221project;

-- Use
USE int221project;

-- Drop
DROP TABLE IF EXISTS task;

-- Table
CREATE TABLE IF NOT EXISTS task
(
    id              INT PRIMARY KEY AUTO_INCREMENT,
    taskTitle       VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    taskDescription VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    taskAssignees   VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci  NULL,
    taskStatus      ENUM ('NO_STATUS', 'TO_DO', 'DOING', 'DONE')                  NOT NULL DEFAULT 'NO_STATUS',
    createdOn       DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedOn       DATETIME                                                      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert
INSERT INTO task (taskTitle, taskDescription, taskAssignees, taskStatus, createdOn, updatedOn)
VALUES ('TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0',
        'Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5',
        'Assignees1Assignees2Assignees3', 'NO_STATUS', '2024-04-22 09:00:00', '2024-04-22 09:00:00');
INSERT INTO task (taskTitle, taskDescription, taskAssignees, taskStatus, createdOn, updatedOn)
VALUES ('Repository', '', '', 'TO_DO', '2024-04-22 09:05:00', '2024-04-22 14:00:00');
INSERT INTO task (taskTitle, taskDescription, taskAssignees, taskStatus, createdOn, updatedOn)
VALUES ('ดาต้าเบส', 'ສ້າງຖານຂໍ້ມູນ', 'あなた、彼、彼女 (私ではありません)', 'DOING', '2024-04-22 09:10:00',
        '2024-04-25 00:00:00');
INSERT INTO task (taskTitle, taskDescription, taskAssignees, taskStatus, createdOn, updatedOn)
VALUES ('_Infrastructure_', '_Setup containers_', 'ไก่งวง กับ เพนกวิน', 'DONE', '2024-04-22 09:15:00',
        '2024-04-22 10:00:00');
