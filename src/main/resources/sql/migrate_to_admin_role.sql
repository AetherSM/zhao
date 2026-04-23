-- 一次性迁移：将 SUPER_ADMIN、ORG_ADMIN 合并为 ADMIN（管理员）
-- 执行前请备份。全新安装请直接使用 schema.sql，无需执行本脚本。

USE tutoring_manage_system;

-- 将 id=1 角色更新为统一「管理员」
UPDATE sys_role
SET role_name = '管理员', role_code = 'ADMIN',
    description = '辅导机构业务与教务管理：学员、教师、课程、排课、考勤、缴费与统计等'
WHERE id = 1;

-- 原绑定在「机构管理员」(假设 id=2) 的用户改挂到管理员 id=1
UPDATE sys_user_role SET role_id = 1 WHERE role_id = 2;

-- 删除废弃角色「机构管理员」行（若 id=2 仍为 ORG_ADMIN）
DELETE FROM sys_role WHERE id = 2 AND role_code = 'ORG_ADMIN';

-- 若库中超级管理员角色码仍为 SUPER_ADMIN，合并到 ADMIN：
UPDATE sys_role SET role_name = '管理员', role_code = 'ADMIN' WHERE role_code = 'SUPER_ADMIN';
