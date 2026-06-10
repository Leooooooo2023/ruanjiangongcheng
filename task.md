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

---

## 📋 停车位地图可视化

### 需求概述

将停车位管理页面改造为**图形化、类地图展示**效果，使用真实停车场车位布局，支持拖拽移动，每个停车位可点击交互。

### 核心功能

#### 1. 停车场地图可视化

- [ ] 使用真实停车场车位布局（模拟地下停车场或地面停车场平面图）
- [ ] 车位以矩形格形式排列在地图上，模拟真实车位形状（2.5m × 5m 比例）
- [ ] 地图支持**拖拽移动**（鼠标拖拽平移整个地图）
- [ ] 地图支持**滚轮缩放**（放大/缩小，0.5x ~ 3x）
- [ ] 绘制车道（车位之间的通道）、柱子、出入口标识等辅助元素
- [ ] 停车位颜色状态：
  - 🟢 **绿色**：空闲车位（status = 0，未被分配）
  - 🔴 **红色**：已占用车位（status = 1，已被分配）
- [ ] 鼠标悬停时车位**浮起/高亮**效果（阴影加深、轻微放大）

#### 2. 车位点击交互（统一弹窗）

点击某个车位 → 弹出**信息小窗口**（悬浮卡片），窗口内容根据当前用户角色和车位状态动态展示：

**管理员视角：**
- 车位号（number）、位置描述（location）
- 当前状态（空闲/占用/申请中）
- 如已分配：业主账号、业主姓名、租期起止时间
- 如申请中：申请人姓名、账号、申请时间 + **【通过】/【拒绝】按钮**
- 空闲车位：可直接手动分配

**业主视角（核心交互）：**
- 车位号、位置描述
- 当前状态（空闲/占用/申请中）
- **能否申请的判断逻辑（由后端返回）：**

| 车位状态 | 业主情况 | 窗口显示 |
|----------|----------|----------|
| 🟢 空闲 | 无车位、无待审核申请 | 显示 **【申请车位】按钮**，可点击申请 |
| 🟢 空闲 | 已有车位 | 提示"您已拥有车位" ❌ 不可申请 |
| 🟢 空闲 | 已有待审核申请 | 提示"您已有申请正在审核中" ❌ 不可申请 |
| 🔴 已占用 | 该车位属于该业主 | 显示自己的车位详情（租期等） |
| 🔴 已占用 | 该车位属于他人 | 提示"该车位已被占用" ❌ 不可申请 |
| 🟡 申请中 | 该业主本人申请 | 显示"审核中，请等待管理员审核" |
| 🟡 申请中 | 他人申请 | 提示"该车位已被他人申请" ❌ 不可申请 |

- 点击 **【申请车位】按钮** → 直接提交（无需额外表单，系统自动记录申请人+申请时间）
- 点击地图空白区域关闭弹窗

#### 3. 业主端 — 我的车位

- [ ] 业主门户停车位页面展示停车场地图（全视图，可拖拽缩放）
- [ ] 业主直接点击地图上的车位查看详情和可申请状态
- [ ] 页面顶部/侧边显示业主当前车位状态摘要（已有车位/审核中/无车位）
- [ ] 申请状态流转：点击申请 → 待审核 → 管理员通过（已分配）/ 管理员拒绝

#### 4. 管理员端 — 审核分配

- [ ] 管理后台停车位页面新增"车位申请审核"入口
- [ ] 审核列表展示：申请人姓名、账号、申请车位号、申请时间
- [ ] 管理员可在列表直接**通过/拒绝**，或点击车位进入地图审核
- [ ] 地图模式下：点击申请中的车位（黄色/橙色标识）→ 弹窗显示申请详情
- [ ] 审核通过 → 车位自动分配给申请人（状态变红）
- [ ] 审核拒绝 → 车位恢复空闲（状态保持绿色）
- [ ] 管理员亦可直接在空闲车位手动分配（保留现有分配功能）

### 技术方案

#### 前端 — 地图渲染

采用 **Canvas** 或 **SVG** 方案绘制停车场平面图：

| 方案 | 优势 | 劣势 |
|------|------|------|
| Canvas | 性能好，适合大量图形 | 需手动处理事件命中 |
| SVG + CSS | 事件处理方便，样式灵活 | 数量多时性能略差 |
| **Konva.js**（推荐） | Canvas 性能 + 事件系统完善 + 拖拽/缩放内置 | 需引入依赖库 |

**推荐方案：Konva.js** (`konva` + `vue-konva`)
- 内置 Stage 拖拽、缩放
- 事件系统完善（click、hover、drag）
- 图层概念契合停车场场景（车位层、车道层、文字层）

#### 停车场布局数据结构

```js
// 停车场布局配置
const parkingLayout = {
  name: 'B1层停车场',
  rows: 4,           // 车位排数
  cols: 8,           // 每排车位数
  spotWidth: 50,     // 车位宽度（px）
  spotHeight: 100,   // 车位长度（px）
  laneWidth: 80,     // 车道宽度（px）
  // 每个车位的坐标由计算得出，编号可自定义
}

// 后端返回车位数据
// GET /api/parkings → [{ id, number, location, status, ownerId, ownerUsername, ownerName, startDate, endDate }]
```

#### 后端新增

| 文件/端点 | 说明 |
|-----------|------|
| `entity/ParkingApplication.java` | 车位申请表（id, parkingId, ownerId, status, createTime, updateTime） |
| `dto/ParkingApplicationDTO.java` | 申请请求 DTO |
| `controller/ParkingApplicationController.java` | `/api/parking-applications` CRUD |
| `service/ParkingApplicationService.java` | 申请业务逻辑 |
| `GET /api/parkings` | 已有接口，前端改造为地图展示 |
| `POST /api/parking-applications/apply` | 业主提交申请 |
| `GET /api/parking-applications` | 管理员查看申请列表 |
| `PUT /api/parking-applications/{id}/approve` | 管理员审核通过 |
| `PUT /api/parking-applications/{id}/reject` | 管理员审核拒绝 |
| `GET /api/owner/parking-applications` | 业主查看自己的申请状态 |

#### 前端改造

| 文件 | 变更 |
|------|------|
| `property-admin/src/views/Parkings.vue` | 从表格视图 → 地图+表格切换视图 |
| `property-portal/src/views/Parking.vue` | 从单一描述页 → 地图交互+申请功能 |
| `property-admin/src/components/ParkingMap.vue` | 新增：停车场地图组件（konva） |
| `property-portal/src/components/ParkingMap.vue` | 新增：停车场地图组件（konva） |
| `property-admin/src/api/parking.js` | 新增申请相关 API |
| `property-portal/src/api/parking.js` | 新增申请 API |

### 验收标准

1. ☐ 停车场地图正确渲染，车位布局合理
2. ☐ 地图可拖拽移动、滚轮缩放
3. ☐ 车位颜色正确（空闲绿色、占用红色、申请中橙色）
4. ☐ 鼠标悬停有浮起效果，点击弹出信息窗口
5. ☐ 业主可查看地图并申请空闲车位
6. ☐ 管理员可在地图上审核车位申请
7. ☐ 地图模式与表格模式可切换（管理后台保留表格方便批量操作）

---

## 📋 待实现

### 新增车位在地图上显示

- [ ] 管理员新增车位时，校验车位号必须匹配地图现有位置（A-D排, 1-8号），不匹配则拒绝
- [ ] 服务端新增车位时校验车位号唯一性，不允许同一地图位置重复入库
- [ ] 或者：地图支持动态扩展车位（不限于32个固定位置），新增的车位自动出现在对应区域
