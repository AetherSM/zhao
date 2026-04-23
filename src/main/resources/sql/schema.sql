-- Tutoring Manage System (MySQL 8.0)
-- charset: utf8mb4, collation: utf8mb4_unicode_ci

CREATE DATABASE IF NOT EXISTS tutoring_manage_system
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE tutoring_manage_system;

-- =====================
-- System tables
-- =====================

CREATE TABLE IF NOT EXISTS sys_user (
  id            BIGINT       NOT NULL PRIMARY KEY,
  username      VARCHAR(50)  NOT NULL UNIQUE COMMENT '用户名（唯一）',
  password      VARCHAR(100) NOT NULL COMMENT '密码（BCrypt）',
  real_name     VARCHAR(50)  NOT NULL COMMENT '真实姓名',
  phone         VARCHAR(20)  NULL COMMENT '手机号',
  email         VARCHAR(100) NULL COMMENT '邮箱',
  avatar        VARCHAR(255) NULL COMMENT '头像地址',
  status        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态（0 禁用 / 1 正常）',
  deleted       TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除（0 否 / 1 是）',
  create_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='用户表';

CREATE TABLE IF NOT EXISTS sys_role (
  id          BIGINT      NOT NULL PRIMARY KEY,
  role_name   VARCHAR(50) NOT NULL UNIQUE COMMENT '角色名称（唯一）',
  role_code   VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码（如 ADMIN）',
  description VARCHAR(255) NULL COMMENT '角色描述',
  deleted     TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除（0 否 / 1 是）',
  create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='角色表';

CREATE TABLE IF NOT EXISTS sys_user_role (
  id      BIGINT NOT NULL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  UNIQUE KEY uk_user_role (user_id, role_id),
  KEY idx_user_id (user_id),
  KEY idx_role_id (role_id)
) COMMENT='用户角色关联表';

CREATE TABLE IF NOT EXISTS sys_user_teacher (
  id         BIGINT NOT NULL PRIMARY KEY,
  user_id    BIGINT NOT NULL COMMENT '用户ID',
  teacher_id BIGINT NOT NULL COMMENT '教师ID',
  UNIQUE KEY uk_user (user_id),
  UNIQUE KEY uk_teacher (teacher_id),
  KEY idx_user_id (user_id),
  KEY idx_teacher_id (teacher_id)
) COMMENT='用户-教师绑定表';

CREATE TABLE IF NOT EXISTS sys_user_student (
  id         BIGINT NOT NULL PRIMARY KEY,
  user_id    BIGINT NOT NULL COMMENT '用户ID',
  student_id BIGINT NOT NULL COMMENT '学生ID',
  UNIQUE KEY uk_user (user_id),
  UNIQUE KEY uk_student (student_id),
  KEY idx_user_id (user_id),
  KEY idx_student_id (student_id)
) COMMENT='用户-学生绑定表';

-- =====================
-- Business tables
-- =====================

CREATE TABLE IF NOT EXISTS student (
  id            BIGINT      NOT NULL PRIMARY KEY,
  student_name  VARCHAR(50) NOT NULL COMMENT '学员姓名',
  gender        TINYINT     NOT NULL COMMENT '性别（1 男 / 2 女）',
  age           INT         NULL COMMENT '年龄',
  grade         VARCHAR(20) NOT NULL COMMENT '年级',
  phone         VARCHAR(20) NULL COMMENT '家长手机号',
  address       VARCHAR(255) NULL COMMENT '家庭地址',
  enroll_time   DATE        NOT NULL COMMENT '入学时间',
  deleted       TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除（0 否 / 1 是）',
  create_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_student_name (student_name),
  KEY idx_grade (grade)
) COMMENT='学员表';

CREATE TABLE IF NOT EXISTS teacher (
  id            BIGINT      NOT NULL PRIMARY KEY,
  teacher_name  VARCHAR(50) NOT NULL COMMENT '教师姓名',
  gender        TINYINT     NOT NULL COMMENT '性别（1 男 / 2 女）',
  age           INT         NULL COMMENT '年龄',
  teach_years   INT         NULL COMMENT '教龄（年）',
  subject       VARCHAR(50) NOT NULL COMMENT '擅长科目',
  phone         VARCHAR(20) NULL COMMENT '手机号',
  deleted       TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除（0 否 / 1 是）',
  create_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_teacher_name (teacher_name),
  KEY idx_subject (subject)
) COMMENT='教师表';

CREATE TABLE IF NOT EXISTS course_category (
  id            BIGINT      NOT NULL PRIMARY KEY,
  category_name VARCHAR(50) NOT NULL UNIQUE COMMENT '分类名称（唯一）',
  sort          INT         NOT NULL DEFAULT 0 COMMENT '排序（升序）',
  deleted       TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除（0 否 / 1 是）',
  create_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT='课程分类表';

CREATE TABLE IF NOT EXISTS course (
  id           BIGINT        NOT NULL PRIMARY KEY,
  course_name  VARCHAR(100)  NOT NULL COMMENT '课程名称',
  category_id  BIGINT        NOT NULL COMMENT '课程分类 ID',
  price        DECIMAL(10,2) NOT NULL COMMENT '课程单价（元 / 课时）',
  total_hours  INT           NOT NULL COMMENT '总课时数',
  apply_grade  VARCHAR(50)   NOT NULL COMMENT '适用年级',
  description  VARCHAR(255)  NULL COMMENT '课程描述',
  deleted      TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除（0 否 / 1 是）',
  create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_course_name (course_name),
  KEY idx_category_id (category_id)
) COMMENT='课程表';

CREATE TABLE IF NOT EXISTS student_course (
  id          BIGINT   NOT NULL PRIMARY KEY,
  student_id  BIGINT   NOT NULL COMMENT '学员 ID',
  course_id   BIGINT   NOT NULL COMMENT '课程 ID',
  sign_time   DATETIME NOT NULL COMMENT '报名时间',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_student_course (student_id, course_id),
  KEY idx_student_id (student_id),
  KEY idx_course_id (course_id)
) COMMENT='学员课程报名表';

CREATE TABLE IF NOT EXISTS course_schedule (
  id            BIGINT      NOT NULL PRIMARY KEY,
  course_id     BIGINT      NOT NULL COMMENT '课程 ID',
  teacher_id    BIGINT      NOT NULL COMMENT '教师 ID',
  classroom     VARCHAR(50) NOT NULL COMMENT '教室',
  schedule_date DATE        NOT NULL COMMENT '排课日期',
  class_period  VARCHAR(20) NOT NULL COMMENT '节次',
  deleted       TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除（0 否 / 1 是）',
  create_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_course_id (course_id),
  KEY idx_teacher_id (teacher_id),
  KEY idx_schedule_time (schedule_date, class_period),
  UNIQUE KEY uk_teacher_time (teacher_id, schedule_date, class_period, deleted),
  UNIQUE KEY uk_classroom_time (classroom, schedule_date, class_period, deleted)
) COMMENT='排课表';

CREATE TABLE IF NOT EXISTS payment (
  id          BIGINT        NOT NULL PRIMARY KEY,
  student_id  BIGINT        NOT NULL COMMENT '学员 ID',
  course_id   BIGINT        NOT NULL COMMENT '课程 ID',
  amount      DECIMAL(10,2) NOT NULL COMMENT '缴费金额',
  pay_time    DATETIME      NOT NULL COMMENT '缴费时间',
  pay_type    VARCHAR(20)   NOT NULL COMMENT '缴费方式',
  remark      VARCHAR(255)  NULL COMMENT '备注',
  create_time DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_student_id (student_id),
  KEY idx_course_id (course_id),
  KEY idx_pay_time (pay_time)
) COMMENT='缴费记录表';

CREATE TABLE IF NOT EXISTS student_attendance (
  id                BIGINT   NOT NULL PRIMARY KEY,
  student_id        BIGINT   NOT NULL COMMENT '学员 ID',
  schedule_id       BIGINT   NOT NULL COMMENT '排课 ID',
  attendance_status TINYINT  NOT NULL COMMENT '考勤状态（1 正常 / 2 迟到 / 3 早退 / 4 缺勤）',
  attendance_time   DATETIME NOT NULL COMMENT '考勤时间',
  remark            VARCHAR(255) NULL COMMENT '备注',
  create_time       DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_student_id (student_id),
  KEY idx_schedule_id (schedule_id)
) COMMENT='学员考勤表';

-- =====================
-- Special Features Tables
-- =====================

-- 1. Course Evaluation (Course Feedback)
CREATE TABLE IF NOT EXISTS course_review (
  id           BIGINT        NOT NULL PRIMARY KEY,
  student_id   BIGINT        NOT NULL COMMENT '学员 ID',
  course_id    BIGINT        NOT NULL COMMENT '课程 ID',
  teacher_id   BIGINT        NOT NULL COMMENT '教师 ID',
  rating       INT           NOT NULL COMMENT '评分（1-5 星）',
  comment      TEXT          NULL COMMENT '评价内容',
  create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted      TINYINT       NOT NULL DEFAULT 0,
  KEY idx_course_id (course_id),
  KEY idx_teacher_id (teacher_id)
) COMMENT='课程评价表';

-- 2. Learning Resources (Homework / Course Materials)
CREATE TABLE IF NOT EXISTS learning_resource (
  id           BIGINT        NOT NULL PRIMARY KEY,
  title        VARCHAR(100)  NOT NULL COMMENT '资源标题',
  type         VARCHAR(20)   NOT NULL COMMENT '类型（HOMEWORK/MATERIAL）',
  course_id    BIGINT        NOT NULL COMMENT '课程 ID',
  teacher_id   BIGINT        NULL COMMENT '发布教师 ID',
  content      TEXT          NULL COMMENT '资源描述或链接',
  create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted      TINYINT       NOT NULL DEFAULT 0,
  KEY idx_course_id (course_id),
  KEY idx_teacher_id (teacher_id)
) COMMENT='学习资源表';

-- 3. Chat Messages (Parent-Teacher Communication)
CREATE TABLE IF NOT EXISTS chat_message (
  id           BIGINT        NOT NULL PRIMARY KEY,
  from_user_id BIGINT        NOT NULL COMMENT '发送者ID',
  to_user_id   BIGINT        NOT NULL COMMENT '接收者ID',
  content      TEXT          NOT NULL COMMENT '消息内容',
  is_read      TINYINT       NOT NULL DEFAULT 0 COMMENT '是否已读（0 否 / 1 是）',
  create_time  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_from_user (from_user_id),
  KEY idx_to_user (to_user_id),
  KEY idx_create_time (create_time)
) COMMENT='聊天消息表';

-- =====================
-- Seed data (roles + admin)
-- =====================
-- Demo password: 123456 (BCrypt: $2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.TVuHOn2)
-- =====================

INSERT INTO sys_role (id, role_name, role_code, description)
VALUES
  (1, '管理员', 'ADMIN', '辅导机构业务与教务管理：学员、教师、课程、排课、考勤、缴费与统计等'),
  (2, '教师', 'TEACHER', '教师端权限'),
  (3, '财务', 'FINANCE', '财务端权限'),
  (4, '学员/家长', 'STUDENT', '学员端权限')
ON DUPLICATE KEY UPDATE role_name=VALUES(role_name), role_code=VALUES(role_code), description=VALUES(description);

INSERT INTO sys_user (id, username, password, real_name, status)
VALUES
  (1, 'admin', '123456', '管理员', 1),
  (2, 'orgadmin', '123456', '机构管理员', 1),
  (3, 'finance1', '123456', '财务', 1),
  -- 教师账号 (ID 2001-2006)
  (2001, 'teacher2001', '123456', '李老师', 1),
  (2002, 'teacher2002', '123456', '王老师', 1),
  (2003, 'teacher2003', '123456', '张老师', 1),
  (2004, 'teacher2004', '123456', '刘老师', 1),
  (2005, 'teacher2005', '123456', '陈老师', 1),
  (2006, 'teacher2006', '123456', '周老师', 1),
  -- 学员账号 (ID 4001-4006)
  (4001, 'student4001', '123456', '王小宝', 1),
  (4002, 'student4002', '123456', '赵小雨', 1),
  (4003, 'student4003', '123456', '陈同学', 1),
  (4004, 'student4004', '123456', '孙小涵', 1),
  (4005, 'student4005', '123456', '周子轩', 1),
  (4006, 'student4006', '123456', '吴思琪', 1)
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name), status=VALUES(status);

INSERT INTO sys_user_role (id, user_id, role_id)
VALUES
  (1, 1, 1),
  (2, 2, 1),
  (3, 3, 3),
  -- 教师角色
  (2001, 2001, 2), (2002, 2002, 2), (2003, 2003, 2), (2004, 2004, 2), (2005, 2005, 2), (2006, 2006, 2),
  -- 学员角色
  (4001, 4001, 4), (4002, 4002, 4), (4003, 4003, 4), (4004, 4004, 4), (4005, 4005, 4), (4006, 4006, 4)
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), role_id=VALUES(role_id);

-- =====================
-- Test data (biz)
-- =====================

INSERT INTO course_category (id, category_name, sort)
VALUES
  (1001, '小学数学', 1),
  (1002, '初中英语', 2),
  (1003, '高中物理', 3),
  (1004, '初中数学', 4),
  (1005, '小学语文', 5),
  (1006, '高中英语', 6)
ON DUPLICATE KEY UPDATE category_name=VALUES(category_name), sort=VALUES(sort);

INSERT INTO teacher (id, teacher_name, gender, age, teach_years, subject, phone)
VALUES
  (2001, '李老师', 1, 32, 8, '数学', '13800138001'),
  (2002, '王老师', 2, 29, 5, '英语', '13800138002'),
  (2003, '张老师', 1, 35, 10, '物理', '13800138003'),
  (2004, '刘老师', 1, 38, 12, '数学', '13800138004'),
  (2005, '陈老师', 2, 33, 6, '语文', '13800138005'),
  (2006, '周老师', 2, 31, 7, '英语', '13800138006')
ON DUPLICATE KEY UPDATE teacher_name=VALUES(teacher_name), subject=VALUES(subject), phone=VALUES(phone);

INSERT INTO sys_user_teacher (id, user_id, teacher_id)
VALUES
  (2001, 2001, 2001), (2002, 2002, 2002), (2003, 2003, 2003), (2004, 2004, 2004), (2005, 2005, 2005), (2006, 2006, 2006)
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), teacher_id=VALUES(teacher_id);

INSERT INTO course (id, course_name, category_id, price, total_hours, apply_grade, description)
VALUES
  (3001, '小学数学培优班', 1001, 80.00, 40, '小学 3-4 年级', '数学培优'),
  (3002, '初中英语提分班', 1002, 90.00, 36, '初中 1-2 年级', '英语提分'),
  (3003, '高中物理冲刺班', 1003, 120.00, 30, '高中 2-3 年级', '物理冲刺'),
  (3004, '初中数学提高班', 1004, 85.00, 32, '初中 2-3 年级', '代数几何综合'),
  (3005, '小学语文阅读与写作', 1005, 70.00, 30, '小学 3-6 年级', '阅读写作训练'),
  (3006, '高中英语冲刺班', 1006, 130.00, 28, '高中 1-3 年级', '高考英语专项'),
  (3007, '小学数学思维训练', 1001, 75.00, 36, '小学 4-6 年级', '奥数入门')
ON DUPLICATE KEY UPDATE course_name=VALUES(course_name), price=VALUES(price), total_hours=VALUES(total_hours);

INSERT INTO student (id, student_name, gender, age, grade, phone, address, enroll_time)
VALUES
  (4001, '王小宝', 1, 9, '小学 3 年级', '13900139001', 'XX小区1号', '2024-01-10'),
  (4002, '赵小雨', 2, 13, '初中 1 年级', '13900139002', 'XX小区2号', '2024-02-05'),
  (4003, '陈同学', 1, 17, '高中 2 年级', '13900139003', 'XX小区3号', '2024-03-01'),
  (4004, '孙小涵', 2, 14, '初中 2 年级', '13900139004', '阳光花园 5 栋', '2025-02-01'),
  (4005, '周子轩', 1, 11, '小学 5 年级', '13900139005', '学府路 18 号', '2025-02-18'),
  (4006, '吴思琪', 2, 16, '高中 1 年级', '13900139006', '滨江小区 2 期', '2025-03-05')
ON DUPLICATE KEY UPDATE student_name=VALUES(student_name), grade=VALUES(grade), phone=VALUES(phone);

INSERT INTO sys_user_student (id, user_id, student_id)
VALUES
  (4001, 4001, 4001), (4002, 4002, 4002), (4003, 4003, 4003), (4004, 4004, 4004), (4005, 4005, 4005), (4006, 4006, 4006)
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), student_id=VALUES(student_id);

INSERT INTO student_course (id, student_id, course_id, sign_time)
VALUES
  (5001, 4001, 3001, '2024-01-12 10:00:00'),
  (5002, 4002, 3002, '2024-02-10 10:00:00'),
  (5003, 4003, 3003, '2024-03-02 10:00:00'),
  (5004, 4004, 3004, '2025-02-05 14:00:00'),
  (5005, 4005, 3005, '2025-02-20 10:30:00'),
  (5006, 4006, 3006, '2025-03-08 09:00:00'),
  (5007, 4001, 3005, '2025-03-01 11:00:00'),
  (5008, 4002, 3006, '2025-03-10 15:00:00'),
  (5009, 4005, 3007, '2025-03-12 16:00:00')
ON DUPLICATE KEY UPDATE sign_time=VALUES(sign_time);

INSERT INTO course_schedule (id, course_id, teacher_id, classroom, schedule_date, class_period)
VALUES
  (6001, 3001, 2001, '101 教室', '2024-03-15', '上午 1-2 节'),
  (6002, 3002, 2002, '102 教室', '2024-03-16', '下午 3-4 节'),
  (6003, 3003, 2003, '201 教室', '2024-03-17', '晚上 1-2 节'),
  (6004, 3004, 2004, '103 教室', '2025-09-10', '上午 3-4 节'),
  (6005, 3005, 2005, '104 教室', '2025-09-11', '下午 1-2 节'),
  (6006, 3006, 2006, '202 教室', '2025-09-12', '晚上 1-2 节'),
  (6007, 3007, 2001, '101 教室', '2025-09-15', '上午 1-2 节'),
  (6008, 3002, 2002, '102 教室', '2025-09-16', '下午 3-4 节'),
  (6009, 3001, 2001, '101 教室', '2025-09-17', '上午 3-4 节')
ON DUPLICATE KEY UPDATE classroom=VALUES(classroom);

INSERT INTO payment (id, student_id, course_id, amount, pay_time, pay_type, remark)
VALUES
  (7001, 4001, 3001, 800.00, '2024-03-10 10:00:00', '微信', '预缴一部分'),
  (7002, 4002, 3002, 3240.00, '2024-03-11 10:00:00', '支付宝', '全额缴费'),
  (7003, 4004, 3004, 2720.00, '2025-02-28 11:20:00', '微信', '一期学费'),
  (7004, 4005, 3005, 2100.00, '2025-02-25 09:00:00', '支付宝', '全额'),
  (7005, 4006, 3006, 3640.00, '2025-03-15 14:00:00', '银行卡', '分期首笔'),
  (7006, 4001, 3005, 1400.00, '2025-03-02 10:00:00', '微信', '语文班'),
  (7007, 4002, 3006, 2000.00, '2025-03-11 12:00:00', '微信', '英语冲刺部分缴费')
ON DUPLICATE KEY UPDATE amount=VALUES(amount), pay_time=VALUES(pay_time);

INSERT INTO student_attendance (id, student_id, schedule_id, attendance_status, attendance_time, remark)
VALUES
  (8001, 4001, 6001, 1, '2024-03-15 09:00:00', '正常'),
  (8002, 4002, 6002, 2, '2024-03-16 15:05:00', '迟到5分钟'),
  (8003, 4004, 6004, 1, '2025-09-10 08:50:00', '正常'),
  (8004, 4005, 6005, 1, '2025-09-11 13:55:00', '正常'),
  (8005, 4006, 6006, 4, '2025-09-12 18:30:00', '病假缺勤'),
  (8006, 4001, 6007, 2, '2025-09-15 08:08:00', '迟到'),
  (8007, 4002, 6008, 1, '2025-09-16 14:50:00', '正常'),
  (8008, 4001, 6009, 1, '2025-09-17 08:02:00', '正常')
ON DUPLICATE KEY UPDATE attendance_status=VALUES(attendance_status), remark=VALUES(remark);

INSERT INTO course_review (id, student_id, course_id, teacher_id, rating, comment, create_time, deleted)
VALUES
  (9001, 4001, 3001, 2001, 5, '李老师讲课很清楚，孩子很喜欢。', '2025-08-20 19:30:00', 0),
  (9002, 4002, 3002, 2002, 4, '英语课互动多，作业量适中。', '2025-08-22 16:00:00', 0),
  (9003, 4003, 3003, 2003, 5, '物理实验演示很棒，提分明显。', '2025-08-25 10:15:00', 0),
  (9004, 4004, 3004, 2004, 4, '数学难度合适，希望增加习题课。', '2025-09-01 20:00:00', 0)
ON DUPLICATE KEY UPDATE rating=VALUES(rating), comment=VALUES(comment);

INSERT INTO learning_resource (id, title, type, course_id, teacher_id, content, create_time, deleted)
VALUES
  (9101, '第三单元课后练习', 'HOMEWORK', 3001, 2001, '完成练习册 P32-P35，拍照上传。', '2025-09-01 08:00:00', 0),
  (9102, '英语时态梳理讲义', 'MATERIAL', 3002, 2002, '一般现在时与现在完成时对比 PDF 已发班级群。', '2025-09-05 12:00:00', 0),
  (9103, '力学公式速记卡', 'MATERIAL', 3003, 2003, '牛顿三定律一页纸总结。', '2025-09-06 09:30:00', 0),
  (9104, '周末阅读一篇', 'HOMEWORK', 3005, 2005, '课外阅读《草房子》第 1-2 章，写 200 字读后感。', '2025-09-12 17:00:00', 0)
ON DUPLICATE KEY UPDATE title=VALUES(title), content=VALUES(content);

INSERT INTO chat_message (id, from_user_id, to_user_id, content, is_read, create_time)
VALUES
  (9201, 4001, 2001, '老师好，请问本周数学作业截止时间是？', 1, '2025-09-08 19:00:00'),
  (9202, 2001, 4001, '周五晚 21:00 前提交即可。', 1, '2025-09-08 19:15:00'),
  (9203, 4001, 2001, '收到，谢谢老师！', 0, '2025-09-08 19:16:00'),
  (9204, 1, 2001, '请李老师本周五前提交教研组月度小结。', 1, '2025-09-09 09:00:00')
ON DUPLICATE KEY UPDATE content=VALUES(content);
