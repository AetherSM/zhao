# 基于 SpringBoot + Vue 的辅导机构管理系统（完整搭建版）

## 技术栈

- **后端**：Spring Boot **2.7.18**、MyBatis-Plus **3.5.6**、MySQL 8.0、Spring Security + JWT
- **前端**：Vue 3 + Vite + Element Plus + Pinia + Axios + ECharts

## 快速运行（后端 + 前端）

### 1) 初始化数据库

在 MySQL 里执行：

- `src/main/resources/sql/schema.sql`

会创建库 `tutoring_manage_system` 并写入初始化账号：

- 用户名：`admin`
- 密码：`123456`

### 2) 修改后端配置

编辑 `src/main/resources/application.yml`，按你的本机 MySQL 改 `username/password`。

> 重要：`app.jwt.secret` 请改成你自己的长随机字符串。

### 3) 启动后端

```bash
cd /Users/develop/final_college/zhao
./mvnw spring-boot:run
```

后端接口文档（Swagger/OpenAPI）：

- Swagger UI：`http://localhost:8080/swagger-ui.html`
- OpenAPI JSON：`http://localhost:8080/v3/api-docs`

### 4) 启动前端

```bash
cd /Users/develop/final_college/zhao/frontend
npm install
npm run dev
```

前端默认地址：

- `http://localhost:5173`

前端已配置开发代理（见 `frontend/vite.config.js`）：

- `/api` -> `http://localhost:8080`

## 默认登录

- 用户名：`admin`
- 密码：`123456`

更多测试账号（同密码 `123456`）：

- 机构管理员：`orgadmin`
- 教师：`teacher1`
- 财务：`finance1`

## 权限说明（接口级）

系统按角色限制接口访问（见 `src/main/java/org/example/zhao/security/SecurityConfig.java`）：

- `TEACHER`：仅可访问排课/考勤相关接口（`/api/v1/schedule/**`、`/api/v1/attendance/**`）
- `FINANCE`：仅可访问缴费/营收统计相关接口（`/api/v1/payment/**`、`/api/v1/statistics/revenue`）
- `ADMIN`：管理员（辅导机构业务与教务管理相关接口）

