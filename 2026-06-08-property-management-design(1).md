# 小区物业管理系统 — 设计文档

> 日期：2026-06-08
> 版本：v3（Spring Boot + MyBatis-Plus）

---

## 1. 项目概述

基于 Spring Boot + Vue 3 前后端分离的小区物业管理系统，实现管理员后台和业主前台双角色功能。

### 1.1 核心功能

**管理员后台：**
- 账号登录/修改密码/安全退出
- 楼栋管理 CRUD
- 物业员工管理 CRUD
- 业主管理 CRUD
- 停车位管理 CRUD（分配/收回）
- 报修管理（查看/分配员工/处理状态流转）
- 留言投诉管理（查看/回复）

**业主前台：**
- 登录/修改个人信息/修改密码
- 报修：提交（支持图片上传）→ 查看进度 → 完工评价（1-5星）
- 留言投诉：提交 → 查看物业回复
- 我的车位（只读）
- 我的楼栋（只读）

### 1.2 设计原则

- 软删除：核心业务表 `is_deleted` 字段，MyBatis-Plus `@TableLogic` 注解自动处理，无需手写过滤条件
- BCrypt 密码加密：admin 和 owner 密码永不存明文
- JWT 无状态鉴权：Token 不入库，Payload 含 id + username + role
- 数据隔离：业主端查询严格按 owner_id/building_id 过滤
- 全局异常处理：后端异常统一转 JSON 返回，前端不白屏
- 声明式参数校验：DTO 注解校验，Service 层不写 if-null
- 文件虚拟路径映射：上传文件与 jar 包物理隔离
- MyBatis-Plus 降维：Mapper 继承 BaseMapper<T>，Service 继承 IService<T>，零手写基础 CRUD

---

## 2. 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 2.7.x（兼容 JDK 8，内嵌 Tomcat 9） |
| ORM | MyBatis-Plus | mybatis-plus-boot-starter 3.5.x |
| 数据库 | MySQL | 8.x |
| 连接池 | Druid | druid-spring-boot-starter 1.2.x |
| JWT | java-jwt (auth0) | 4.x |
| 加密 | jBCrypt | 0.4.x |
| 校验 | spring-boot-starter-validation | Spring Boot 自动集成 |
| JSON | Jackson | Spring Boot 自动集成 |
| 文件上传 | spring-boot-starter-web 内置 | — |
| 日志 | SLF4J + Logback | Spring Boot 自动集成 |
| 前端框架 | Vue 3 | 3.3+ |
| UI 组件库 | Element Plus | 2.x |
| 状态管理 | Pinia | 2.x |
| 路由 | Vue Router 4 | 4.x |
| HTTP | Axios | 1.x |
| 构建 | Vite | 5.x |
| JDK | JDK 8 | 1.8 |
| 构建工具 | Maven | 3.6+ |
| 构建产物 | 可执行 .jar | java -jar 运行 |

---

## 3. 系统架构

**经典单体架构，前后端分离：**

```
property-management/
├── property-admin/          ← 管理员后台（Vue 3 + Element Plus）
│   ├── src/
│   │   ├── views/           登录、仪表盘、楼栋、员工、业主、车位、报修、投诉、个人信息
│   │   ├── components/      公共组件
│   │   ├── router/          路由配置 + 嵌套在 AdminLayout 下
│   │   ├── store/           Pinia 状态管理
│   │   ├── api/             Axios 封装 + 按模块分 API
│   │   └── utils/           JWT 存取、路由守卫
│   └── package.json
│
├── property-portal/         ← 业主前台（Vue 3 + 轻量卡片化 + Mobile-First）
│   ├── src/
│   │   ├── views/           首页、报修、投诉、个人信息、车位、楼栋
│   │   ├── components/      公共组件
│   │   ├── router/          路由配置 + 嵌套在 PortalLayout 下
│   │   ├── store/           Pinia
│   │   ├── api/             Axios 封装
│   │   └── utils/           JWT 存取、路由守卫
│   └── package.json
│
└── property-server/         ← Spring Boot 后端（Maven 项目，可执行 .jar）
    ├── src/main/java/com/property/
    │   ├── PropertyApplication.java  ← @SpringBootApplication + @MapperScan 启动类
    │   ├── controller/      REST API 控制器
    │   ├── interceptor/     JWT + 角色拦截器
    │   ├── service/         业务逻辑层（继承 IService<T>）
    │   │   └── impl/        Service 实现类（继承 ServiceImpl<Mapper, T>）
    │   ├── dao/             MyBatis-Plus Mapper 接口（继承 BaseMapper<T>）
    │   ├── entity/          实体类（@TableId + @TableLogic 注解）
    │   ├── dto/             数据传输对象 + 声明式校验注解
    │   ├── exception/       全局异常处理 + 自定义业务异常
    │   ├── util/            JwtUtil、BcryptUtil
    │   └── config/          WebConfig（跨域+拦截器+虚拟路径，三合一）
    ├── src/main/resources/
    │   ├── mapper/          MyBatis-Plus XML（仅复杂查询需要）
    │   ├── db/              SQL 建表脚本 + 初始数据
    │   └── application.yml  全局唯一配置文件
    └── pom.xml
```

---

## 4. 数据库设计

### 4.1 ER 关系

- building 1 ← N owner（一栋楼有多个业主）
- owner 1 ← N parking（业主可租多个车位）
- owner 1 ← N repair（业主可提交多条报修）
- owner 1 ← N complaint（业主可提交多条投诉）
- employee 1 ← N repair（员工可处理多条报修）

### 4.2 表结构

**admin 管理员**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT PK | 自增主键 |
| username | VARCHAR(50) | 登录账号，唯一 |
| password | VARCHAR(100) | BCrypt 加密 |
| name | VARCHAR(50) | 姓名 |
| phone | VARCHAR(20) | 手机号 |
| email | VARCHAR(100) | 邮箱 |
| is_deleted | TINYINT DEFAULT 0 | 软删除标记 |
| create_time | DATETIME | 创建时间 |

**building 楼栋**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT PK | 自增主键 |
| name | VARCHAR(50) | 楼栋名称 |
| units | INT | 单元数 |
| floors | INT | 楼层数 |
| description | VARCHAR(200) | 描述 |
| is_deleted | TINYINT DEFAULT 0 | 软删除标记 |
| create_time | DATETIME | 创建时间 |

**owner 业主**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT PK | 自增主键 |
| username | VARCHAR(50) | 登录账号，唯一 |
| password | VARCHAR(100) | BCrypt 加密 |
| name | VARCHAR(50) | 姓名 |
| phone | VARCHAR(20) | 手机号 |
| email | VARCHAR(100) | 邮箱 |
| building_id | INT FK | 关联 building.id |
| unit | VARCHAR(10) | 单元号 |
| room | VARCHAR(10) | 房号 |
| is_deleted | TINYINT DEFAULT 0 | 软删除标记 |
| create_time | DATETIME | 创建时间 |

**employee 员工**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT PK | 自增主键 |
| name | VARCHAR(50) | 姓名 |
| phone | VARCHAR(20) | 手机号 |
| position | VARCHAR(50) | 职位 |
| department | VARCHAR(50) | 部门 |
| is_deleted | TINYINT DEFAULT 0 | 软删除标记 |
| create_time | DATETIME | 创建时间 |

**parking 停车位**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT PK | 自增主键 |
| number | VARCHAR(20) | 车位号 |
| location | VARCHAR(100) | 位置 |
| status | TINYINT | 0空闲/1占用 |
| owner_id | INT FK | 关联 owner.id，可为空 |
| start_date | DATE | 租期开始 |
| end_date | DATE | 租期结束 |
| create_time | DATETIME | 创建时间 |

**repair 报修**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT PK | 自增主键 |
| owner_id | INT FK | 关联 owner.id |
| title | VARCHAR(100) | 标题 |
| content | TEXT | 内容描述 |
| image | VARCHAR(200) | 图片路径 |
| status | TINYINT | 0待处理/1维修中/2已完成 |
| employee_id | INT FK | 关联 employee.id，可为空 |
| rating | TINYINT | 1-5星评价，可为空 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

**complaint 留言投诉**

| 字段 | 类型 | 说明 |
|------|------|------|
| id | INT PK | 自增主键 |
| owner_id | INT FK | 关联 owner.id |
| title | VARCHAR(100) | 标题 |
| content | TEXT | 内容 |
| reply | TEXT | 物业回复，可为空 |
| status | TINYINT | 0待回复/1已回复 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

---

## 5. RESTful API 设计

### 5.1 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

分页响应：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,
    "list": []
  }
}
```

### 5.2 接口列表

**认证模块 AuthController**

| 方法 | 路径 | 说明 | 角色 |
|------|------|------|------|
| POST | /api/auth/login | 登录，返回 JWT | 公开 |
| POST | /api/auth/logout | 退出登录 | 已登录 |
| PUT | /api/auth/password | 修改密码 | 已登录 |
| GET | /api/auth/info | 获取当前用户信息 | 已登录 |

**楼栋管理 BuildingController**

| 方法 | 路径 | 说明 | 角色 |
|------|------|------|------|
| GET | /api/buildings | 分页查询楼栋列表 | 管理员 |
| GET | /api/buildings/{id} | 查询楼栋详情 | 管理员 |
| POST | /api/buildings | 新增楼栋 | 管理员 |
| PUT | /api/buildings/{id} | 修改楼栋 | 管理员 |
| DELETE | /api/buildings/{id} | 删除楼栋（软删除） | 管理员 |

**员工管理 EmployeeController**

| 方法 | 路径 | 说明 | 角色 |
|------|------|------|------|
| GET | /api/employees | 分页查询员工列表 | 管理员 |
| GET | /api/employees/{id} | 查询员工详情 | 管理员 |
| POST | /api/employees | 新增员工 | 管理员 |
| PUT | /api/employees/{id} | 修改员工 | 管理员 |
| DELETE | /api/employees/{id} | 删除员工（软删除） | 管理员 |

**业主管理 OwnerController**

| 方法 | 路径 | 说明 | 角色 |
|------|------|------|------|
| GET | /api/owners | 分页查询业主列表 | 管理员 |
| GET | /api/owners/{id} | 查询业主详情 | 管理员 |
| POST | /api/owners | 新增业主 | 管理员 |
| PUT | /api/owners/{id} | 修改业主 | 管理员 |
| DELETE | /api/owners/{id} | 删除业主（软删除） | 管理员 |

**停车位管理 ParkingController**

| 方法 | 路径 | 说明 | 角色 |
|------|------|------|------|
| GET | /api/parkings | 分页查询车位列表 | 管理员 |
| GET | /api/parkings/{id} | 查询车位详情 | 管理员 |
| POST | /api/parkings | 新增车位 | 管理员 |
| PUT | /api/parkings/{id} | 修改车位 | 管理员 |
| DELETE | /api/parkings/{id} | 删除车位 | 管理员 |
| PUT | /api/parkings/{id}/assign | 分配车位给业主 | 管理员 |
| PUT | /api/parkings/{id}/revoke | 收回车位 | 管理员 |

**报修管理 RepairController**

| 方法 | 路径 | 说明 | 角色 |
|------|------|------|------|
| GET | /api/repairs | 分页查询报修列表 | 管理员 |
| GET | /api/repairs/{id} | 查询报修详情 | 管理员/业主(限自己) |
| PUT | /api/repairs/{id}/assign | 分配维修员工 | 管理员 |
| PUT | /api/repairs/{id}/status | 更新报修状态 | 管理员 |
| GET | /api/owner/repairs | 查询我的报修 | 业主 |
| POST | /api/owner/repairs | 提交报修 | 业主 |
| PUT | /api/owner/repairs/{id}/rate | 完工评价 | 业主 |

**留言投诉 ComplaintController**

| 方法 | 路径 | 说明 | 角色 |
|------|------|------|------|
| GET | /api/complaints | 分页查询投诉列表 | 管理员 |
| GET | /api/complaints/{id} | 查询投诉详情 | 管理员/业主(限自己) |
| PUT | /api/complaints/{id}/reply | 回复投诉 | 管理员 |
| GET | /api/owner/complaints | 查询我的投诉 | 业主 |
| POST | /api/owner/complaints | 提交投诉 | 业主 |

**文件上传 FileController**

| 方法 | 路径 | 说明 | 角色 |
|------|------|------|------|
| POST | /api/files/upload | 上传图片，返回 URL | 已登录 |

---

## 6. 前端设计

### 6.1 管理员后台（property-admin）

**路由（嵌套在 AdminLayout 下）：**

| 路径 | 页面 |
|------|------|
| /login | 登录页（独立，无 Layout） |
| /dashboard | 首页仪表盘 |
| /buildings | 楼栋管理 |
| /employees | 员工管理 |
| /owners | 业主管理 |
| /parkings | 停车位管理 |
| /repairs | 报修管理 |
| /complaints | 留言投诉管理 |
| /profile | 个人信息/修改密码 |

**布局：** AdminLayout — 左侧菜单栏 + 顶部导航条 + 右侧内容区 `<router-view/>`

### 6.2 业主前台（property-portal）

**路由（嵌套在 PortalLayout 下）：**

| 路径 | 页面 |
|------|------|
| /login?redirect=xxx | 登录页（独立，无 Layout） |
| /home | 首页 |
| /repairs | 我的报修 |
| /repairs/create | 提交报修 |
| /complaints | 我的投诉 |
| /complaints/create | 提交投诉 |
| /profile | 个人信息/修改密码 |
| /parking | 我的车位（只读） |
| /building | 我的楼栋（只读） |

**布局：** PortalLayout — 顶部导航栏 + 卡片化内容区 `<router-view/>`

**Mobile-First：** Flexbox/Grid 响应式，卡片移动端满宽，按钮最小 44px 触控区，报修支持手机拍照上传

### 6.3 路由守卫

- 未登录访问任意页 → `/login?redirect=原始路径`，登录成功后自动跳回
- JWT `role=0` → 只能访问管理员路由
- JWT `role=1` → 只能访问业主路由
- Token 过期 → 清除存储，跳 `/login?redirect=当前路径`
- Layout 组件不随页面切换重渲染

---

## 7. 后端架构

### 7.1 分层结构

```
com.property
├── controller/
│   ├── AuthController
│   ├── BuildingController
│   ├── EmployeeController
│   ├── OwnerController
│   ├── ParkingController
│   ├── RepairController
│   ├── ComplaintController
│   └── FileController
│
├── interceptor/
│   ├── JwtInterceptor         校验 Token 有效性
│   └── RoleInterceptor        校验角色权限
│
├── service/
│   ├── AuthService            登录验证、BCrypt、JWT 生成
│   ├── BuildingService        楼栋业务（继承 IService<Building>）
│   ├── EmployeeService        员工业务（继承 IService<Employee>）
│   ├── OwnerService           业主业务 + 数据隔离（继承 IService<Owner>）
│   ├── ParkingService         车位业务 + 分配/收回（继承 IService<Parking>）
│   ├── RepairService          报修业务 + 状态流转 + 评价（继承 IService<Repair>）
│   ├── ComplaintService       投诉业务 + 回复（继承 IService<Complaint>）
│   └── FileService            文件上传存储
│
├── dao/
│   ├── AdminMapper            继承 BaseMapper<Admin>
│   ├── BuildingMapper         继承 BaseMapper<Building>
│   ├── EmployeeMapper         继承 BaseMapper<Employee>
│   ├── OwnerMapper            继承 BaseMapper<Owner>
│   ├── ParkingMapper          继承 BaseMapper<Parking>
│   ├── RepairMapper           继承 BaseMapper<Repair>
│   └── ComplaintMapper        继承 BaseMapper<Complaint>
│
├── entity/                    @TableId 主键 + @TableLogic 逻辑删除注解
│
├── dto/
│   ├── LoginDTO               @NotBlank username, @NotBlank password
│   ├── BuildingDTO            @NotBlank name, @NotNull units, @NotNull floors
│   ├── OwnerDTO               @NotBlank name, @Pattern phone
│   ├── RepairDTO              @NotBlank title, @NotBlank content
│   ├── ComplaintDTO           @NotBlank title, @NotBlank content
│   ├── PasswordDTO            @NotBlank oldPassword, @NotBlank newPassword
│   ├── PageQuery              page, size
│   ├── PageResult<T>          total, list
│   └── Result<T>              code, message, data
│
├── exception/
│   ├── GlobalExceptionHandler @RestControllerAdvice，捕获异常转 Result<T>
│   └── BusinessException      自定义业务异常
│
├── util/
│   ├── JwtUtil                生成/解析/校验 JWT
│   └── BcryptUtil             加密/验证密码
│
└── config/
    └── WebConfig              三合一：CORS + 拦截器注册 + 虚拟路径映射
```

### 7.2 关键配置

**application.yml（全局唯一配置文件）：**
- server.port: 8080
- 数据源（Druid 连接池 + MySQL 8）
- MyBatis-Plus 配置：mapper 位置、别名包、驼峰映射、逻辑删除全局值
- 文件上传（max-file-size、max-request-size）
- 自定义属性（JWT secret、过期时间、上传路径）

**WebConfig（三合一配置类，实现 WebMvcConfigurer）：**
- addCorsMappings：允许 `http://localhost:5173` 跨域，允许携带 Authorization Header
- addInterceptors：注册 JwtInterceptor + RoleInterceptor
- addResourceHandlers：`/uploads/**` → 物理路径（读取 application.yml 中的配置），文件与 jar 包物理隔离

**PropertyApplication.java（启动类）：**
- @SpringBootApplication
- @MapperScan("com.property.dao")

**Spring Boot 自动配置已覆盖：**
- multipartResolver（内嵌 Tomcat 自动处理）
- Jackson JSON 序列化
- hibernate-validator 自动集成
- SLF4J + Logback 日志

### 7.3 JWT 鉴权流程

1. 用户 POST `/api/auth/login`，后端查库 + BCrypt 校验
2. 校验通过，JwtUtil 生成 Token（Payload: id, username, role, exp=24h）
3. 返回 Token 给前端，前端存入 localStorage + Pinia
4. 后续请求 Axios 拦截器自动加 `Authorization: Bearer <token>`
5. 后端 JwtInterceptor 校验 Token，将用户信息放入 request attribute
6. RoleInterceptor 根据 `/api/admin/**` 或 `/api/owner/**` 校验角色

### 7.4 软删除实现（MyBatis-Plus 自动处理）

- Entity 的 `is_deleted` 字段加 `@TableLogic` 注解
- application.yml 配置全局逻辑删除值：`mybatis-plus.global-config.db-config.logic-delete-value=1`，`logic-not-delete-value=0`
- 所有 BaseMapper 查询自动追加 `WHERE is_deleted = 0`，无需手写
- 删除操作自动转为 `UPDATE SET is_deleted = 1`
- 关联查询（如业主查楼栋）同样自动过滤

### 7.5 全局异常处理

- `BusinessException`：业务逻辑主动抛出（密码错误、权限不足、数据不存在）
- `MethodArgumentNotValidException`：校验失败，返回 400 + 具体字段错误信息
- 其他异常：统一返回 `{ code: 500, message: "系统异常" }`，不泄露堆栈

---

## 8. 数据隔离策略

| 场景 | 隔离方式 |
|------|----------|
| 业主查看自己的报修 | WHERE owner_id = 当前用户id |
| 业主查看自己的投诉 | WHERE owner_id = 当前用户id |
| 业主查看自己的车位 | WHERE owner_id = 当前用户id |
| 业主查看自己的楼栋 | 通过 owner.building_id 关联查询，只返回自己所在楼栋 |
| 管理员查看所有数据 | 无隔离，可看全量 |
