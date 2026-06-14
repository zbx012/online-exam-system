# 在线考试系统 — 项目文档

Spring Boot 2.5 + Vue 3 + Element Plus 全栈在线考试系统，支持学生/教师/管理员三角色。

## 速查

| 项目 | 值 |
|------|-----|
| 后端端口 | 8080 |
| 前端端口 | 3000 (Vite) |
| 数据库 | MySQL `online_exam`，root / ih6ftbuwbu |
| Redis | localhost:6379 (Docker) |
| 测试账号 | admin / 123456 |
| 前端代理 | `/api` → `localhost:8080` |

## 启动

```bash
# 后端：IDEA 打开 webZbx → Run Application.java
# 前端：
cd user-auth-frontend && npm run dev
# Redis（如未启动）：
docker start redis
```

## 架构红线

- Controller → Service → Mapper 三层，不要跨层调用
- 所有 `/api/**` 需 JWT Token（登录/注册除外），JwtInterceptor 统一校验
- 前端 API 请求统一用 `@/utils/request`，不要直接用 axios
- 密码必须 BCrypt，不要用 MD5
- 题目类型 `CQ`=选择题 `FB`=填空题，通过 type 字段分发到子表

## 关键文件

- `webZbx/src/main/java/com/example/project/config/JwtInterceptor.java` — JWT + 黑名单
- `webZbx/src/main/java/com/example/project/config/GlobalExceptionHandler.java` — 全局异常
- `webZbx/src/main/java/com/example/project/service/TokenBlacklistService.java` — Redis 黑名单
- `user-auth-frontend/src/utils/request.ts` — Axios 实例（自动带 Token）
- `user-auth-frontend/src/router/index.ts` — 前端路由

## 深入文档

- `README.md` — 完整项目文档（架构、API、数据库、部署）
