-- Database
CREATE DATABASE IF NOT EXISTS int221project;

-- Use
USE int221project;

-- Drop
DROP TABLE IF EXISTS task;

-- Table
CREATE TABLE task (
    id INT PRIMARY KEY AUTO_INCREMENT,
    taskTitle VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    taskDescription VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    taskAssignees VARCHAR(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
    taskStatus ENUM('No Status', 'To Do', 'Doing', 'Done') NOT NULL DEFAULT 'No Status',
    createdOn DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updatedOn DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert
INSERT INTO task (taskTitle, taskDescription, taskAssignees, taskStatus)
VALUES ('TaskTitle1TaskTitle2TaskTitle3TaskTitle4TaskTitle5TaskTitle6TaskTitle7TaskTitle8TaskTitle9TaskTitle0', 'Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti1Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti2Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti3Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti4Descripti1Descripti2Descripti3Descripti4Descripti5Descripti6Descripti7Descripti8Descripti9Descripti5', 'Assignees1Assignees2Assignees3', 'No Status');
INSERT INTO task (taskTitle, taskDescription, taskAssignees, taskStatus)
VALUES ('Repository', ' ', 'Unassigned', 'To Do');
INSERT INTO task (taskTitle, taskDescription, taskAssignees, taskStatus)
VALUES ('ดาต้าเบส', 'ສ້າງຖານຂໍ້ມູນ', 'あなた、彼、彼女 (私ではありません)', 'Doing');
INSERT INTO task (taskTitle, taskDescription, taskAssignees, taskStatus)
VALUES (' _Infrastructure_ ', ' _Setup containers_ ', 'ไก่งวง กับ เพนกวิน', 'Done');
