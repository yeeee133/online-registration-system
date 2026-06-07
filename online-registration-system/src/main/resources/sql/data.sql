USE registration_system;

INSERT INTO sys_user(username, password, role) VALUES
('admin', '123456', 'ADMIN'),
('teacher', '123456', 'TEACHER'),
('student', '123456', 'STUDENT');

INSERT INTO activity(name, max_people, current_people, created_time) VALUES
('Java在线报名性能测试实验', 100, 0, NOW()),
('软件测试训练营', 60, 0, NOW()),
('毕业设计交流会', 80, 0, NOW());
