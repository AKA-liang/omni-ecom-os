# OPC 智能体控制中心

数字员工（AI Agent）信息管理系统，用于集中管理、监控和调配各类 AI 智能体。

---

## 功能特性

- **智能体总览** — 卡片式展示所有智能体状态（工作中/在线/离线/忙碌），实时统计在线数、平均准确率
- **分类管理** — 按业务线分组：AI 电商系列 / AI 编程系列 / AI 培训板块
- **增删改查** — 完整的智能体 CRUD，支持头像上传
- **搜索筛选** — 按名称、分类、状态多维过滤
- **分页列表** — 表格模式查看所有智能体详情
- **站点配置** — 管理员档案、品牌配置等全局设置

---

## 技术栈

| 层级 | 技术 |
|------|------|
| 前端框架 | Vue 3 (Composition API) + Vite |
| 图标 | Lucide Icons |
| 图表 | ECharts |
| 后端框架 | Spring Boot (Java 17) |
| 数据库 | MySQL 8.0 |
| 包管理 | npm / Maven |

---

## 项目结构

```
omni-ecom-os-v1.0/
├── src/                        # Vue 前端源码
│   ├── components/             # 可复用组件
│   │   ├── EmployeeCard.vue          # 智能体卡片
│   │   ├── EmployeeDetail.vue         # 智能体详情弹窗
│   │   ├── EmployeeForm.vue           # 智能体表单（新建/编辑）
│   │   ├── EmployeeAvatarUpload.vue   # 头像上传组件
│   │   ├── CategoryFilter.vue          # 分类筛选
│   │   ├── BossProfile.vue            # 管理员档案
│   │   └── SiteConfigForm.vue         # 站点配置表单
│   ├── views/                  # 页面视图
│   │   ├── Dashboard.vue              # 首页仪表盘（卡片总览）
│   │   ├── AgentManagement.vue        # 智能体管理（表格分页）
│   │   ├── DataAnalytics.vue          # 数据分析
│   │   └── SystemSettings.vue         # 系统设置
│   ├── data/
│   │   ├── employees.js               # 智能体模拟数据（13个智能体）
│   │   └── boss.js                     # 管理员数据
│   ├── services/api.js        # API 请求封装
│   ├── router/index.js        # 路由配置
│   └── styles/global.css      # 全局样式
├── backend/                    # Spring Boot 后端源码
│   └── src/main/java/com/digital/employee/
│       ├── controller/               # REST API 控制器
│       │   ├── BossController.java         # 管理员信息
│       │   ├── EmployeeManagementController.java  # 智能体 CRUD
│       │   ├── CategoryController.java      # 分类管理
│       │   ├── SiteConfigController.java    # 站点配置
│       │   └── FileController.java          # 文件上传/访问
│       ├── entity/                 # 数据实体
│       ├── repository/            # 数据访问层
│       └── service/               # 业务逻辑层
└── images/                      # README 截图
```

---

## 快速启动

### 环境要求

- Node.js 18+
- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 第一步：启动 MySQL

通过 phpStudy 或手动启动 MySQL 服务，创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS digital_employee
  DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

初始化表结构和默认数据：
```sql
SOURCE C:/liangtao/opencode/omni-ecom-os-v1.0/backend/src/main/resources/data.sql;
```

> MySQL 用户名/密码：`root / root`

### 第二步：启动后端（端口 10004）

```bash
cd backend
mvn spring-boot:run
```

验证后端启动：
```powershell
Invoke-RestMethod -Uri 'http://localhost:10004/api/boss' -Method Get
```

### 第三步：启动前端（端口 10003）

```bash
cd omni-ecom-os-v1.0
npm install      # 首次需要安装依赖
npm run dev
```

访问 http://localhost:10003

---

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/boss` | GET/POST | 管理员信息 |
| `/api/employees` | GET | 获取智能体列表（支持 `?category=` 筛选） |
| `/api/employees` | POST | 创建智能体 |
| `/api/employees/{id}` | PUT | 更新智能体 |
| `/api/employees/{id}` | DELETE | 删除智能体 |
| `/api/categories` | GET | 获取分类列表 |
| `/api/site-config` | GET/POST | 站点配置 |
| `/api/files/upload` | POST | 上传文件 |
| `/api/files/{filename}` | GET | 访问文件 |

---

## 智能体分类

| 分类 | 包含角色 |
|------|---------|
| **AI 电商系列** | 选品专家、财务专家、推广专家、运营专家、客服专员、交付专家 |
| **AI 编程系列** | 架构师、前端工程师、后端工程师、运维工程师、运营开发 |
| **AI 培训板块** | 培训专家、学习顾问 |

---

## License

MIT