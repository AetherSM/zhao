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

-- =====================
-- Seed data (roles + admin)
-- Demo password: 123456 (生产环境请改为 BCrypt 存储)
-- =====================

INSERT INTO sys_role (id, role_name, role_code, description)
VALUES
  (1, '超级管理员', 'SUPER_ADMIN', '系统所有功能权限'),
  (2, '机构管理员', 'ORG_ADMIN', '机构业务管理'),
  (3, '教师', 'TEACHER', '教师端权限'),
  (4, '财务', 'FINANCE', '财务端权限'),
  (5, '学员/家长', 'STUDENT', '学员端权限')
ON DUPLICATE KEY UPDATE role_name=VALUES(role_name), role_code=VALUES(role_code), description=VALUES(description);

INSERT INTO sys_user (id, username, password, real_name, status)
VALUES
  (1, 'admin', '123456', '系统管理员', 1)
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name), status=VALUES(status);

INSERT INTO sys_user_role (id, user_id, role_id)
VALUES
  (1, 1, 1)
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), role_id=VALUES(role_id);

-- =====================
-- Test data (biz)
-- =====================

INSERT INTO course_category (id, category_name, sort)
VALUES
  (1001, '小学数学', 1),
  (1002, '初中英语', 2),
  (1003, '高中物理', 3)
ON DUPLICATE KEY UPDATE category_name=VALUES(category_name), sort=VALUES(sort);

INSERT INTO teacher (id, teacher_name, gender, age, teach_years, subject, phone)
VALUES
  (2001, '李老师', 1, 32, 8, '数学', '13800138001'),
  (2002, '王老师', 2, 29, 5, '英语', '13800138002'),
  (2003, '张老师', 1, 35, 10, '物理', '13800138003')
ON DUPLICATE KEY UPDATE teacher_name=VALUES(teacher_name), subject=VALUES(subject), phone=VALUES(phone);

INSERT INTO course (id, course_name, category_id, price, total_hours, apply_grade, description)
VALUES
  (3001, '小学数学培优班', 1001, 80.00, 40, '小学 3-4 年级', '数学培优'),
  (3002, '初中英语提分班', 1002, 90.00, 36, '初中 1-2 年级', '英语提分'),
  (3003, '高中物理冲刺班', 1003, 120.00, 30, '高中 2-3 年级', '物理冲刺')
ON DUPLICATE KEY UPDATE course_name=VALUES(course_name), price=VALUES(price), total_hours=VALUES(total_hours);

INSERT INTO student (id, student_name, gender, age, grade, phone, address, enroll_time)
VALUES
  (4001, '王小宝', 1, 9, '小学 3 年级', '13900139001', 'XX小区1号', '2024-01-10'),
  (4002, '赵小雨', 2, 13, '初中 1 年级', '13900139002', 'XX小区2号', '2024-02-05'),
  (4003, '陈同学', 1, 17, '高中 2 年级', '13900139003', 'XX小区3号', '2024-03-01')
ON DUPLICATE KEY UPDATE student_name=VALUES(student_name), grade=VALUES(grade), phone=VALUES(phone);

INSERT INTO student_course (id, student_id, course_id, sign_time)
VALUES
  (5001, 4001, 3001, '2024-01-12 10:00:00'),
  (5002, 4002, 3002, '2024-02-10 10:00:00'),
  (5003, 4003, 3003, '2024-03-02 10:00:00')
ON DUPLICATE KEY UPDATE sign_time=VALUES(sign_time);

INSERT INTO course_schedule (id, course_id, teacher_id, classroom, schedule_date, class_period)
VALUES
  (6001, 3001, 2001, '101 教室', '2024-03-15', '上午 1-2 节'),
  (6002, 3002, 2002, '102 教室', '2024-03-16', '下午 3-4 节'),
  (6003, 3003, 2003, '201 教室', '2024-03-17', '晚上 1-2 节')
ON DUPLICATE KEY UPDATE classroom=VALUES(classroom);

INSERT INTO payment (id, student_id, course_id, amount, pay_time, pay_type, remark)
VALUES
  (7001, 4001, 3001, 800.00, '2024-03-10 10:00:00', '微信', '预缴一部分'),
  (7002, 4002, 3002, 3240.00, '2024-03-11 10:00:00', '支付宝', '全额缴费')
ON DUPLICATE KEY UPDATE amount=VALUES(amount), pay_time=VALUES(pay_time);

INSERT INTO student_attendance (id, student_id, schedule_id, attendance_status, attendance_time, remark)
VALUES
  (8001, 4001, 6001, 1, '2024-03-15 09:00:00', '正常'),
  (8002, 4002, 6002, 2, '2024-03-16 15:05:00', '迟到5分钟')
ON DUPLICATE KEY UPDATE attendance_status=VALUES(attendance_status), remark=VALUES(remark);

-- extra demo users
INSERT INTO sys_user (id, username, password, real_name, status)
VALUES
  (2, 'orgadmin', '123456', '机构管理员', 1),
  (3, 'teacher1', '123456', '李老师(账号)', 1),
  (4, 'finance1', '123456', '财务(账号)', 1),
  (5, 'student1', '123456', '王小宝(账号)', 1)
ON DUPLICATE KEY UPDATE real_name=VALUES(real_name), status=VALUES(status);

INSERT INTO sys_user_role (id, user_id, role_id)
VALUES
  (2, 2, 2),
  (3, 3, 3),
  (4, 4, 4),
  (5, 5, 5)
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), role_id=VALUES(role_id);

INSERT INTO sys_user_teacher (id, user_id, teacher_id)
VALUES
  (1, 3, 2001)
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), teacher_id=VALUES(teacher_id);

INSERT INTO sys_user_student (id, user_id, student_id)
VALUES
  (1, 5, 4001)
ON DUPLICATE KEY UPDATE user_id=VALUES(user_id), student_id=VALUES(student_id);
