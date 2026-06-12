# 在线考试系统

## 📋 项目简介

本项目是一个基于 **Spring Boot + Vue 3** 的全栈在线考试系统，支持**学生、教师、管理员**三种角色的协同工作。系统实现了从用户注册登录、题库管理、考试创建到学生参加考试、成绩管理的完整业务流程。

该项目为大学三年级 Java 课程大作业，旨在综合运用 Java Web 开发、数据库设计、前后端分离架构等知识点。

---

## 🧩 功能模块

### 1. 用户认证模块
- 用户注册（学生 / 教师 / 管理员，支持三种角色）
- 用户登录（JWT 令牌认证 + 拦截器强制校验，有效期 24 小时）
- 用户登出（Token 加入 Redis 黑名单，即时失效）
- 密码 BCrypt 加密存储
- 个人信息查询与修改（含密码修改）
- 用户删除
- 管理员查看全部用户列表

### 2. 考试管理模块
- 考试创建（设置名称、起止时间、状态）
- 考试编辑与删除
- 按 ID 查询单个考试
- 查询全部考试列表
- 按教师 ID 筛选考试

### 3. 题库管理模块
- 新增题目（选择题 / 填空题）
- 修改题目内容及答案
- 删除题目
- 按 ID 查询题目
- 查询全部题目列表

### 4. 考试-题目关联模块
- 将题目加入考试（指定题号顺序）
- 移除考试中的题目
- 调整题目排序
- 查询某场考试的全部题目（按序号排序）

### 5. 考试-学生关联模块（成绩管理）
- 学生报名参加考试
- 取消报名
- 教师录入 / 修改学生成绩
- 查询学生参加的所有考试及成绩
- 查询某场考试的所有学生成绩
- 查询某学生在某场考试的成绩

### 6. 前端页面
- 登录 / 注册页面（切换式）
- **学生端**：首页、考试列表
- **教师端**：首页、考试管理、成绩查看
- **管理员端**：首页、系统设置、用户管理

---

## 🏗️ 技术架构

| 层级 | 技术选型 | 版本 |
|------|---------|------|
| **后端框架** | Spring Boot | 2.5.14 |
| **持久层框架** | MyBatis (mybatis-spring-boot-starter) | 2.2.2 |
| **数据库** | MySQL | 5.7+ |
| **数据库驱动** | mysql-connector-java | 8.0.33 |
| **JWT 认证** | jjwt (api + impl + jackson) | 0.11.5 |
| **前端框架** | Vue 3 (Composition API) | 3.5.x |
| **UI 组件库** | Element Plus | 2.11.x |
| **构建工具** | Vite | 7.x |
| **语言** | TypeScript | 5.9.x |
| **HTTP 客户端** | Axios | 1.13.x |
| **路由** | Vue Router | 4.x |
| **密码加密** | BCrypt (spring-security-crypto) | — |
| **缓存** | Redis 7 (Docker) + Spring Data Redis | — |
| **Java 版本** | JDK 1.8 | — |

---

## 📁 项目结构

```
在线考试系统大作业/
├── webZbx/                          # 后端项目（Spring Boot）
│   ├── pom.xml                      # Maven 依赖配置
│   └── src/
│       ├── main/
│       │   ├── java/com/example/project/
│       │   │   ├── Application.java              # Spring Boot 启动类
│       │   │   ├── config/
│       │   │   │   ├── CorsConfig.java           # CORS 跨域配置 + JWT 拦截器注册
│       │   │   │   ├── JwtInterceptor.java       # JWT 认证拦截器（含黑名单检查）
│       │   │   │   ├── GlobalExceptionHandler.java # 全局异常处理器
│       │   │   │   └── RedisConfig.java          # Redis 序列化配置
│       │   │   ├── utils/
│       │   │   │   └── JwtUtils.java             # JWT 生成/验证工具类
│       │   │   ├── entity/                       # 实体类
│       │   │   │   ├── User.java                 # 用户实体
│       │   │   │   ├── Exam.java                 # 考试实体
│       │   │   │   ├── Question.java             # 题目基类
│       │   │   │   ├── ChoiceQuestion.java       # 选择题（继承 Question）
│       │   │   │   ├── FillQuestion.java         # 填空题（继承 Question）
│       │   │   │   ├── ExamQuestion.java         # 考试-题目关联实体
│       │   │   │   └── ExamStudent.java          # 考试-学生关联实体
│       │   │   ├── dto/
│       │   │   │   └── LoginResponse.java        # 登录响应 DTO
│       │   │   ├── controller/                   # 控制器层（REST API）
│       │   │   │   ├── AutoController.java       # 用户认证接口
│       │   │   │   ├── ExamController.java       # 考试管理接口
│       │   │   │   ├── QuestionController.java   # 题目管理接口
│       │   │   │   ├── ExamQuestionController.java  # 考试-题目接口
│       │   │   │   └── ExamStudentController.java   # 考试-学生接口
│       │   │   ├── service/                      # 业务逻辑层
│       │   │   │   ├── UserService.java
│       │   │   │   ├── ExamService.java
│       │   │   │   ├── QuestionService.java
│       │   │   │   ├── ExamQuestionService.java
│       │   │   │   ├── ExamStudentService.java
│       │   │   │   └── TokenBlacklistService.java # Token 黑名单（Redis）
│       │   │   └── mapper/                       # MyBatis 映射层
│       │   │       ├── UserMapper.java
│       │   │       ├── ExamMapper.java
│       │   │       ├── QuestionMapper.java
│       │   │       ├── ChoiceQuestionMapper.java
│       │   │       ├── FillQuestionMapper.java
│       │   │       ├── ExamQuestionMapper.java
│       │   │       └── ExamStudentMapper.java
│       │   └── resources/
│       │       ├── application.properties        # 应用配置
│       │       └── mapper/                       # MyBatis XML 映射文件
│       └── test/                                 # 测试代码
├── user-auth-frontend/              # 前端项目（Vue 3 + Element Plus）
│   ├── package.json                 # Node 依赖配置
│   ├── vite.config.ts               # Vite 构建配置（含代理配置）
│   ├── index.html                   # 入口 HTML
│   └── src/
│       ├── main.ts                  # Vue 应用入口
│       ├── App.vue                  # 根组件（登录/注册切换 + 布局）
│       ├── style.css                # 全局样式
│       ├── router/
│       │   └── index.ts             # 路由配置（学生/教师/管理员路由）
│       ├── api/
│       │   ├── auth.vue             # 认证相关 API 封装
│       │   └── user.vue             # 用户相关 API 封装
│       ├── components/
│       │   ├── LoginForm.vue        # 登录表单组件
│       │   ├── RegisterForm.vue     # 注册表单组件
│       │   ├── Home.vue             # 通用首页组件
│       │   ├── Info.vue             # 信息展示组件
│       │   └── ExamRegistration.vue # 考试报名组件
│       ├── views/
│       │   ├── student/             # 学生端视图
│       │   │   ├── StudentLayout.vue   # 学生端布局
│       │   │   ├── StudentHome.vue     # 学生首页
│       │   │   └── ExamList.vue        # 考试列表
│       │   ├── teacher/             # 教师端视图
│       │   │   ├── TeacherLayout.vue   # 教师端布局
│       │   │   ├── TeacherHome.vue     # 教师首页
│       │   │   ├── ExamManage.vue      # 考试管理
│       │   │   └── ScoreCheck.vue      # 成绩查看
│       │   └── admin/               # 管理员端视图
│       │       ├── AdminLayout.vue     # 管理员布局
│       │       ├── AdminHome.vue       # 管理员首页
│       │       ├── SystemSetting.vue   # 系统设置
│       │       └── UserManage.vue      # 用户管理
│       ├── stores/                  # Pinia 状态管理
│       └── utils/                   # 工具函数
├── online_exam1.sql                 # 数据库建表脚本（含示例数据）
├── 项目组实验报告.docx               # 课程实验报告
├── 实验报告补充.docx                 # 实验报告补充材料
├── 在线考试系统.pptx                 # 项目演示 PPT
└── 演示视频.mp4                     # 系统演示视频
```

---

## 🗄️ 数据库设计

### E-R 关系概览

```
┌──────────┐     ┌──────────────┐     ┌──────────┐
│  users   │────→│ exam_student │←────│  exams   │
│ (用户表) │     │  (考试-学生)  │     │ (考试表) │
└──────────┘     └──────────────┘     └──────────┘
                        │                      │
                        │                      ▼
                        │             ┌──────────────┐
                        │             │ exam_question│
                        │             │ (考试-题目)   │
                        │             └──────┬───────┘
                        │                    │
                        ▼                    ▼
                 ┌────────────────────────────────┐
                 │         questions (题目表)      │
                 └────────────┬───────────────────┘
                              │
                 ┌────────────┴────────────┐
                 ▼                         ▼
        ┌────────────────┐    ┌────────────────┐
        │choice_questions│    │ fill_questions │
        │  (选择题表)     │    │  (填空题表)     │
        └────────────────┘    └────────────────┘
```

### 表结构说明（8 张表）

| 表名 | 说明 | 核心字段 |
|------|------|---------|
| `users` | 用户表 | `user_id`(PK), `username`(UNIQUE), `password`(MD5), `email`, `user_type`(student/teacher/admin) |
| `exams` | 考试表 | `exam_id`(PK), `title`, `teacher_id`, `start_time`, `end_time`, `status`(draft/published/closed) |
| `questions` | 题目公共表 | `question_id`(PK), `question_content`, `score`, `type`(CQ/FB/EQ) |
| `choice_questions` | 选择题表 | `question_id`(PK/FK→questions), `optionA~D`, `answer` |
| `fill_questions` | 填空题表 | `question_id`(PK/FK→questions), `answer` |
| `exam_question` | 考试-题目关联 | `question_id`+`exam_id`(联合PK), `order_num` |
| `exam_student` | 考试-学生关联 | `student_id`+`exam_id`(联合PK), `score` |
| `students` | 学生扩展信息 | `student_num`(PK), `user_id`(FK), `student_name` |
| `teachers` | 教师扩展信息 | `teacher_num`(PK), `user_id`(FK), `teacher_name` |

> **设计说明**：题目采用"父表 + 子表"的继承映射策略。`questions` 存储公共字段（内容、分值、类型），`choice_questions` 和 `fill_questions` 通过外键关联并存储各自的特有字段（选项、答案等）。

---

## 🔌 API 接口文档

### 基础信息
- **Base URL**: `http://localhost:8080/api`
- **请求格式**: `application/x-www-form-urlencoded`（GET/POST 简单参数）或 `application/json`（RequestBody）
- **响应格式**: `application/json`
- **跨域**: 后端已配置全局 CORS，允许所有来源

---

### 一、用户认证模块 `/api/auth`

| 接口 | 方法 | 路径 | 参数 | 返回值 | 说明 |
|------|------|------|------|--------|------|
| 注册 | POST | `/register` | `username`, `password`, `email`(可选), `type`(默认student) | `"注册成功！"` 或 `"注册失败：用户名已存在"` | 密码自动 MD5 加密 |
| 登录 | POST | `/login` | `username`, `password` | `LoginResponse {success, message, token, user}` | 成功返回 JWT 令牌及用户信息 |
| 获取用户信息 | GET | `/info` | `userName` | `User` 对象 | 按用户名查询 |
| 更新用户 | PATCH | `/update` | `User`(JSON Body) | 更新后的 `User` | 含密码时会重新加密 |
| 删除用户 | DELETE | `/delete` | `userName` | `"删除成功！"` | 按用户名删除 |
| 获取全部用户 | GET | `/getAllUser` | — | `ArrayList<User>` | 管理员功能 |
| 登出 | POST | `/logout` | `Authorization` Header | `{success, message}` | Token 加入 Redis 黑名单 |

### 二、考试管理模块 `/api/exam`

| 接口 | 方法 | 路径 | 参数 | 返回值 | 说明 |
|------|------|------|------|--------|------|
| 添加考试 | POST | `/add` | `title`, `teacherId`, `startTime`, `endTime`, `status` | `boolean` | 时间格式: `yyyy-MM-dd'T'HH:mm` |
| 查询考试 | GET | `/info` | `examId` | `Exam` 对象 | |
| 更新考试 | PATCH | `/update` | `Exam`(JSON Body) | 更新后的 `Exam` | |
| 删除考试 | DELETE | `/delete` | `examId` | `"删除成功！"` | |
| 查询全部 | GET | `/selectAll` | — | `ArrayList<Exam>` | |
| 按教师查询 | GET | `/selectByTeacherId` | `teacherId` | `ArrayList<Exam>` | 筛选指定教师的考试 |

### 三、题目管理模块 `/api/question`

| 接口 | 方法 | 路径 | 参数 | 返回值 | 说明 |
|------|------|------|------|--------|------|
| 添加选择题 | POST | `/addChoice` | `ChoiceQuestion`(JSON Body) | `boolean` | |
| 添加填空题 | POST | `/addFill` | `FillQuestion`(JSON Body) | `boolean` | |
| 更新选择题 | PATCH | `/updateChoice` | `ChoiceQuestion`(JSON Body) | `ChoiceQuestion` | |
| 更新填空题 | PATCH | `/updateFill` | `FillQuestion`(JSON Body) | `FillQuestion` | |
| 删除选择题 | DELETE | `/deleteChoice` | `questionId` | `200 OK` | |
| 删除填空题 | DELETE | `/deleteFill` | `questionId` | `200 OK` | |
| 查询选择题 | GET | `/selectChoice` | `questionId` | `ChoiceQuestion` | |
| 查询填空题 | GET | `/selectFill` | `questionId` | `FillQuestion` | |
| 查询全部题目 | GET | `/selectAll` | — | `ArrayList<Question>` | 自动识别题目类型 |

### 四、考试-题目关联模块 `/api/examQuestion`

| 接口 | 方法 | 路径 | 参数 | 返回值 | 说明 |
|------|------|------|------|--------|------|
| 添加题目到考试 | POST | `/insert` | `examId`, `questionId`, `orderNum` | `boolean` | |
| 移除题目 | DELETE | `/delete` | `examId`, `questionId` | `boolean` | |
| 更新排序 | PATCH | `/update` | `ExamQuestion`(JSON Body) | `ExamQuestion` | |
| 查询单个关联 | GET | `/select` | `examId`, `questionId` | `ExamQuestion` | |
| 查询考试全部题目 | GET | `/selectQuestionsByExamId` | `examId` | `ArrayList<Question>` | 按 `order_num` 排序 |

### 五、考试-学生关联模块 `/api/examStudent`

| 接口 | 方法 | 路径 | 参数 | 返回值 | 说明 |
|------|------|------|------|--------|------|
| 报名考试 | POST | `/insert` | `examId`, `studentId` | `boolean` | |
| 取消报名 | DELETE | `/delete` | `examId`, `studentId` | `boolean` | |
| 更新成绩 | PATCH | `/updateScore` | `ExamStudent`(JSON Body) | `boolean` | 教师录入分数 |
| 按学生查询 | GET | `/selectByStudent` | `studentId` | `ArrayList<ExamStudent>` | |
| 按考试查询 | GET | `/selectByExam` | `examId` | `ArrayList<ExamStudent>` | |
| 查询单个成绩 | GET | `/selectByExamAndStudent` | `examId`, `studentId` | `ExamStudent` | |

---

## 🚀 环境部署指南

### 1. 环境要求

| 工具 | 版本要求 |
|------|---------|
| JDK | 1.8+ |
| Maven | 3.6+ |
| MySQL | 5.7+ |
| Node.js | 20.19+ / 22.12+ |
| npm | 随 Node.js 安装 |

### 2. 后端部署

```bash
# 1. 克隆 / 进入后端项目目录
cd webZbx

# 2. 导入数据库
# 使用 MySQL 客户端执行 online_exam1.sql：
mysql -u root -p < ../online_exam1.sql

# 3. 修改数据库连接配置
# 编辑 src/main/resources/application.properties
# 设置数据库用户名和密码

# 4. 使用 Maven 启动
mvn spring-boot:run

# 后端服务运行在 http://localhost:8080
```

### 3. 前端部署

```bash
# 1. 进入前端项目目录
cd user-auth-frontend

# 2. 安装依赖
npm install

# 3. 启动开发服务器
npm run dev

# 前端开发服务器运行在 http://localhost:3000
# API 请求自动代理到后端 http://localhost:8080
```

> **注意**：前端 Vite 已配置 `/api` 路径代理，开发时无需手动处理跨域。后端 CORS 配置允许所有来源，生产环境请收敛。

### 4. 测试账号（数据库预置）

| 账号 | 密码 | 角色 | 说明 |
|------|------|------|------|
| `admin` | `123456` | 管理员 | 可管理用户和系统设置 |
| `test01` | `123456` | 教师 | 可创建考试、管理题目 |
| `test02` | `123456` | 学生 | 可查看考试、参加考试 |

> 密码 "123456" 的 MD5 值为 `e10adc3949ba59abbe56e057f20f883e`。

---

## 🔐 安全设计

### 密码加密流程

```
用户输入密码 → BCrypt(BCryptPasswordEncoder) → 存储密文到数据库
```

- **注册时**：使用 BCrypt 对密码进行哈希后存入 `users.password`
- **登录时**：`BCryptPasswordEncoder.matches()` 验证密码
- **更新时**：如传入新密码，自动 BCrypt 加密后更新

### JWT + Redis 认证流程

```
1. 用户登录成功 → 服务端生成 JWT Token（HS512 签名，24h 有效期）
2. 前端存储 Token → 后续请求自动携带 Authorization: Bearer <token>
3. JwtInterceptor 拦截 /api/** 所有请求 → 校验 Token 有效性 → 查 Redis 黑名单
4. 用户登出 → Token 写入 Redis 黑名单（24h 自动过期）
```

### 题目查询缓存

```
查询题目 → 先查 Redis → 命中直接返回 → 未命中查 DB 并写入 Redis（30min TTL）
增/删/改题目 → 自动清除对应缓存
```

### 其他安全措施
- **CORS 跨域配置**：开发环境开放了全部来源，生产环境需限制具体域名
- **用户名唯一性**：数据库 `username` 字段设置了 UNIQUE 约束
- **角色校验**：注册时限制 `user_type` 只能为 `student`、`teacher`、`admin` 三者之一
- **SQL 注入防护**：使用 MyBatis 参数化查询（`#{}` 占位符），天然防注入
- **全局异常处理**：GlobalExceptionHandler 统一返回 JSON 错误响应

---

## 📐 架构设计要点

### 后端分层架构

```
┌─────────────────────────────────────┐
│         Controller 层（控制层）       │  ← 接收 HTTP 请求，参数校验，返回响应
├─────────────────────────────────────┤
│          Service 层（业务层）         │  ← 核心业务逻辑，事务管理
├─────────────────────────────────────┤
│          Mapper 层（持久层）          │  ← MyBatis 映射，数据库 CRUD
├─────────────────────────────────────┤
│          Entity 层（实体层）          │  ← 数据模型，ORM 映射
└─────────────────────────────────────┘
```

### 题目继承体系

```
Question (抽象基类)
├── ChoiceQuestion  (type="CQ")  — 选择题
└── FillQuestion    (type="FB")  — 填空题
```

`QuestionService` 通过判断 `type` 字段分发到对应的子表 Mapper，实现了**多态持久化**。

### 前端路由设计

| 路径 | 组件 | 权限 |
|------|------|------|
| `/student/home` | StudentHome | 学生 |
| `/student/exams` | ExamList | 学生 |
| `/teacher/home` | TeacherHome | 教师 |
| `/teacher/exams` | ExamManage | 教师 |
| `/teacher/scores` | ScoreCheck | 教师 |
| `/admin/home` | AdminHome | 管理员 |
| `/admin/setting` | SystemSetting | 管理员 |
| `/admin/teachers` | UserManage | 管理员 |

---

## 📊 项目交付物清单

| 文件 | 说明 |
|------|------|
| `webZbx/` | 后端源代码（Spring Boot + MyBatis） |
| `user-auth-frontend/` | 前端源代码（Vue 3 + Element Plus） |
| `online_exam1.sql` | 数据库建表脚本（含示例数据） |
| `项目组实验报告.docx` | 课程实验报告 |
| `实验报告补充.docx` | 实验报告补充材料 |
| `在线考试系统.pptx` | 项目演示 PPT |
| `演示视频.mp4` | 系统功能演示视频 |
| `README.md` | 本文档（项目文档） |

---

## 👥 作者

本项目为大学三年级 Java 课程大作业项目组作品。

---

## 📄 许可证

本项目仅用于课程学习与交流目的。
