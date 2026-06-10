# 小区物业管理系统 — 开发任务清单

## 任务概述

在现有系统基础上新增：注册功能、登录锁定、图形验证码。

## 任务列表

### ✅ 已完成

- [x] 系统部署运行（后端 8088，管理后台 5173，业主门户 5174）
- [x] 数据库 schema 初始化

#### 1. 注册功能
- [x] 后端：RegisterDTO、AuthService.register()、AuthController `/api/auth/register`
- [x] 前端（业主门户）：Register.vue 注册页面、路由配置
- [x] WebConfig 放开 `/api/auth/register` 拦截

#### 2. 登录锁定（5次失败锁定30分钟）
- [x] 数据库：admin/owner 表新增 `login_fail_count`、`lock_until` 字段
- [x] 实体：Admin.java / Owner.java 新增对应字段
- [x] 业务：AuthServiceImpl 增加失败计数、锁定判断、成功重置
- [x] 前端：登录失败时显示剩余尝试次数和锁定提示（后端返回对应消息）

#### 3. 图形验证码
- [x] 后端：CaptchaUtil 验证码生成工具（Java AWT，无需额外依赖）
- [x] 后端：CaptchaVO 返回对象（key + base64 图片）
- [x] 后端：AuthController `/api/auth/captcha` 端点
- [x] 后端：LoginDTO 新增 captchaKey/captchaCode 字段
- [x] 后端：AuthServiceImpl 验证码校验逻辑（验证码错误直接返回 400）
- [x] 前端（管理后台+业主门户）：Login.vue 增加验证码展示和点击刷新
- [x] WebConfig 放开 `/api/auth/captcha` 拦截

## 技术实现细节

### 新增文件
| 文件 | 说明 |
|------|------|
| `task.md` | 本任务清单 |
| `property-server/.../util/CaptchaUtil.java` | 验证码生成工具，ConcurrentHashMap 存储，5分钟过期 |
| `property-server/.../dto/CaptchaVO.java` | 验证码返回 VO |
| `property-server/.../dto/RegisterDTO.java` | 注册请求 DTO（含校验注解） |
| `property-portal/src/views/Register.vue` | 业主注册页面 |

### 修改文件
| 文件 | 变更内容 |
|------|----------|
| `entity/Admin.java` | 新增 loginFailCount, lockUntil |
| `entity/Owner.java` | 新增 loginFailCount, lockUntil |
| `dto/LoginDTO.java` | 新增 captchaKey, captchaCode |
| `service/AuthService.java` | 新增 register(), getCaptcha() |
| `service/impl/AuthServiceImpl.java` | 注册、锁定、验证码逻辑 |
| `controller/AuthController.java` | 新增 /register, /captcha 端点 |
| `config/WebConfig.java` | 排除新端点 JWT 拦截 |
| `property-admin/src/views/Login.vue` | 新增验证码区域 |
| `property-admin/src/api/auth.js` | 新增 getCaptcha() |
| `property-portal/src/views/Login.vue` | 新增验证码区域 + 注册链接 |
| `property-portal/src/api/auth.js` | 新增 getCaptcha(), register() |
| `property-portal/src/router/index.js` | 新增 /register 路由，免登录访问 |

## 服务地址

| 服务 | 地址 |
|------|------|
| 后端 API | http://localhost:8088 |
| 管理后台 | http://localhost:5173 |
| 业主门户 | http://localhost:5174 |
| 业主注册 | http://localhost:5174/register |
