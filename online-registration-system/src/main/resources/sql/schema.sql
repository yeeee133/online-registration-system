CREATE DATABASE IF NOT EXISTS registration_system DEFAULT CHARACTER SET utf8mb4;
USE registration_system;

DROP TABLE IF EXISTS registration;
DROP TABLE IF EXISTS activity;
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role VARCHAR(20) NOT NULL
);

CREATE TABLE activity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    max_people INT NOT NULL,
    current_people INT NOT NULL DEFAULT 0,
    created_time DATETIME NOT NULL
);

CREATE TABLE registration (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    activity_id BIGINT NOT NULL,
    student_name VARCHAR(50) NOT NULL,
    created_time DATETIME NOT NULL,
    CONSTRAINT fk_registration_activity FOREIGN KEY(activity_id) REFERENCES activity(id)
);

CREATE INDEX idx_registration_activity ON registration(activity_id);
CREATE INDEX idx_registration_student ON registration(student_name);
