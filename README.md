# AIOpsAgent

> 基于 Spring Boot + Spring AI Alibaba 的智能问答与运维系统

## 项目简介

企业级智能业务代理系统，包含两大核心模块：

### 1. RAG 智能问答
集成 Milvus 向量数据库和阿里云 DashScope，提供基于检索增强生成的智能问答能力，支持多轮对话和流式输出。

### 2. AIOps 智能运维
基于 Spring AI Alibaba Agent Framework 的自动化运维系统，采用 **Planner-Executor-Supervisor** 多 Agent 协作架构，实现告警分析、日志查询、智能诊断和报告生成。

### 3. 后台管理系统
提供管理员登录、会话管理、统计分析等功能，支持数据持久化和趋势分析。

## 核心特性

- **RAG 问答**: 向量检索 + 多轮对话 + 流式输出
- **AIOps 运维**: 多 Agent 协作 + 智能诊断 + 自动报告
- **后台管理**: 登录认证 + 会话管理 + 统计分析
- **数据持久化**: MySQL 存储 + Spring Data JPA
- **工具集成**: 文档检索、Prometheus 告警查询、日志分析、时间工具
- **会话管理**: 上下文维护、历史消息管理、自动清理
- **Web 界面**: 用户对话界面 + 管理后台 + SSE 流式交互
- **Mock 模式**: 支持 Prometheus 和日志服务的 Mock 模式，便于测试

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 开发语言 |
| Spring Boot | 3.2.0 | 应用框架 |
| Spring AI | 1.1.0 | AI Agent 框架 |
| Spring AI Alibaba | 1.1.0.0-RC2 | 阿里云 AI 扩展 |
| DashScope SDK | 2.17.0 | 阿里云 AI 服务 |
| Milvus SDK | 2.6.10 | 向量数据库 |
| Spring Data JPA | - | 数据持久化 |
| MySQL | - | 关系型数据库 |
| Lombok | 1.18.30 | 代码简化 |

## 核心模块

```
OnCall-Agent/
├── src/main/java/org/example/
│   ├── Main.java                         # 应用入口
│   ├── frontend/                         # 前台模块（用户对话）
│   │   ├── controller/
│   │   │   └── ChatController.java       # 对话接口控制器
│   │   └── service/
│   │       └── ChatService.java          # 对话服务
│   ├── backend/                          # 后台模块（管理功能）
│   │   ├── controller/
│   │   │   ├── AdminController.java      # 后台管理控制器
│   │   │   └── LoginController.java      # 登录控制器
│   │   ├── service/
│   │   │   ├── AdminService.java         # 后台管理服务
│   │   │   ├── LoginService.java         # 登录服务
│   │   │   ├── ChatHistoryService.java   # 聊天历史服务
│   │   │   ├── StatsService.java         # 统计服务
│   │   │   └── AdminInitializer.java     # 管理员初始化
│   │   └── config/
│   │       └── AdminInitializer.java     # 管理员初始化配置
│   ├── entity/                           # JPA 实体类
│   │   ├── User.java                     # 用户实体
│   │   ├── ChatSession.java              # 会话实体
│   │   ├── ChatMessage.java              # 消息实体
│   │   └── DailyStats.java               # 每日统计实体
│   ├── repository/                       # JPA 仓库
│   │   ├── UserRepository.java           # 用户仓库
│   │   ├── ChatSessionRepository.java    # 会话仓库
│   │   ├── ChatMessageRepository.java    # 消息仓库
│   │   └── DailyStatsRepository.java     # 统计仓库
│   ├── service/
│   │   ├── AiOpsService.java             # AIOps 多 Agent 服务
│   │   ├── RagService.java               # RAG 服务
│   │   ├── VectorIndexService.java       # 向量索引服务
│   │   ├── VectorSearchService.java      # 向量搜索服务
│   │   ├── VectorEmbeddingService.java   # 向量嵌入服务
│   │   └── DocumentChunkService.java     # 文档分片服务
│   ├── agent/tool/                       # Agent 工具集
│   │   ├── DateTimeTools.java            # 时间工具
│   │   ├── InternalDocsTools.java        # RAG 文档检索工具
│   │   ├── QueryMetricsTools.java        # Prometheus 告警查询工具
│   │   └── QueryLogsTools.java           # 日志查询工具 (MCP)
│   ├── tool/
│   │   └── DropCollection.java           # Milvus 集合清理工具
│   ├── config/                           # 配置类
│   │   ├── DashScopeConfig.java          # DashScope 配置
│   │   ├── MilvusConfig.java             # Milvus 配置
│   │   ├── MilvusProperties.java         # Milvus 属性配置
│   │   ├── MilvusClientFactory.java      # Milvus 客户端工厂
│   │   ├── DocumentChunkConfig.java      # 文档分片配置
│   │   ├── FileUploadConfig.java         # 文件上传配置
│   │   ├── WebConfig.java                # Web 配置
│   │   └── WebMvcConfig.java             # MVC 配置
│   ├── controller/
│   │   ├── FileUploadController.java     # 文件上传控制器
│   │   └── MilvusCheckController.java    # Milvus 健康检查
│   ├── dto/                              # 数据传输对象
│   │   ├── AIOpsRequest.java             # AIOps 请求
│   │   ├── DocumentChunk.java            # 文档分片
│   │   └── FileUploadRes.java            # 上传响应
│   └── constant/                         # 常量定义
│       └── MilvusConstants.java          # Milvus 常量
├── src/main/resources/
│   ├── static/                           # Web 界面
│   │   ├── index.html                    # 用户对话界面
│   │   ├── app.js                        # 对话界面脚本
│   │   ├── styles.css                    # 对话样式
│   │   ├── login.html                    # 管理员登录页
│   │   ├── login.js                      # 登录脚本
│   │   ├── login.css                     # 登录样式
│   │   └── admin/                        # 后台管理界面
│   │       ├── index.html                # 管理后台主页
│   │       ├── admin.js                  # 后台脚本
│   │       └── admin.css                 # 后台样式
│   └── application.yml                   # 应用配置
├── aiops-docs/                           # 运维文档库 (自动向量化)
│   ├── cpu_high_usage.md                 # CPU 高使用率处理方案
│   ├── memory_high_usage.md              # 内存高使用率处理方案
│   ├── disk_high_usage.md                # 磁盘高使用率处理方案
│   ├── service_unavailable.md            # 服务不可用处理方案
│   └── slow_response.md                  # 响应慢处理方案
├── Makefile                              # 自动化脚本
├── vector-database.yml                   # Milvus Docker Compose
└── pom.xml                               # Maven 配置
```

## 核心接口

### 1. 前台对话接口

**流式对话 (推荐)**
```bash
POST /api/chat_stream
Content-Type: application/json

{
  "Id": "session-123",
  "Question": "什么是向量数据库？"
}
```
支持 SSE 流式输出、自动工具调用、多轮对话。

**普通对话**
```bash
POST /api/chat
Content-Type: application/json

{
  "Id": "session-123",
  "Question": "当前系统有哪些告警？"
}
```
一次性返回完整结果，支持 ReactAgent 自动工具调用。

### 2. AIOps 智能运维接口

```bash
POST /api/ai_ops
```
自动执行多 Agent 协作告警分析流程，生成运维报告（SSE 流式输出）。

流程架构：
- **Supervisor Agent**: 调度 Planner 与 Executor
- **Planner Agent**: 分析告警、规划步骤、生成报告
- **Executor Agent**: 执行具体查询任务

### 3. 会话管理

- `POST /api/chat/clear` - 清空会话历史
- `GET /api/chat/session/{sessionId}` - 获取会话信息

### 4. 后台管理接口

**登录认证**
```bash
POST /admin/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}
```

**会话管理**
- `GET /admin/sessions` - 获取所有会话列表（分页）
- `GET /admin/sessions/active` - 获取活跃会话列表
- `GET /admin/sessions/{sessionId}` - 获取会话详情
- `GET /admin/sessions/{sessionId}/messages` - 获取会话消息
- `DELETE /admin/sessions/{sessionId}` - 删除会话

**统计分析**
- `GET /admin/stats` - 获取总体统计信息
- `GET /admin/stats/sessions` - 获取会话统计
- `GET /admin/stats/daily` - 获取每日统计数据
- `GET /admin/stats/trend` - 获取趋势数据

### 5. 文件管理

- `POST /api/upload` - 上传运维文档并自动向量化
- `GET /milvus/health` - Milvus 健康检查

## Agent 工具集

| 工具 | 说明 |
|------|------|
| `DateTimeTools` | 获取当前时间，用于确定日志查询时间范围 |
| `InternalDocsTools` | RAG 文档检索，从运维知识库获取处理方案 |
| `QueryMetricsTools` | 查询 Prometheus 活动告警 |
| `QueryLogsTools` | 通过 MCP 查询日志服务 |

## 核心配置

### application.yml

```yaml
server:
  port: 9900

# MySQL 数据源
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/oncall_agent?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  # JPA 配置
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false

  # 阿里云 DashScope
  ai:
    dashscope:
      api-key: ${ALIYUN_API_KEY}
      chat:
        options:
          timeout: 180000
      retry:
        max-attempts: 3
    
    # MCP 客户端配置
    mcp:
      client:
        enabled: true
        name: tencent-mcp-server
        version: 1.0.0
        request-timeout: 60s
        type: ASYNC
        sse:
          connections:
            tencent-cls:
              url: https://mcp-api.tencent-cloud.com
              sse-endpoint: /sse/xxx

# Milvus 向量数据库
milvus:
  host: localhost
  port: 19530
  database: default
  timeout: 10000

# 阿里云 DashScope Embedding API
dashscope:
  api:
    key: ${ALIYUN_API_KEY}
  embedding:
    model: text-embedding-v4

# 文档分片
document:
  chunk:
    max-size: 800
    overlap: 100

# RAG 配置
rag:
  top-k: 3
  model: "qwen3-max"

# Prometheus 告警
prometheus:
  base-url: http://localhost:9090
  timeout: 10
  mock-enabled: true  # Mock 模式用于测试

# 日志服务
cls:
  mock-enabled: true  # Mock 模式返回模拟日志

# 文件上传
file:
  upload:
    path: ./uploads
    allowed-extensions: txt,md
```

### 环境变量

```bash
export ALIYUN_API_KEY=your-api-key
```

## 快速开始

### 1. 环境准备

```bash
# 安装依赖
- Java 17+
- Maven 3.6+
- Docker (用于 Milvus)
- MySQL 8.0+

# 设置 API Key
export ALIYUN_API_KEY=your-api-key

# 创建 MySQL 数据库
mysql -u root -p
CREATE DATABASE oncall_agent CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 启动应用

**方法一：一键启动**
```bash
make init  # 自动启动向量数据库并上传运维文档
```

**方法二：手动启动**
```bash
# 1. 启动向量数据库
docker compose -f vector-database.yml up -d

# 2. 启动 MySQL（如果未运行）
# 确保 MySQL 服务已启动且创建了 oncall_agent 数据库

# 3. 构建并启动服务
mvn clean install
mvn spring-boot:run
```

### 3. 使用示例

**Web 界面**
```
用户对话界面: http://localhost:9900
管理员登录页: http://localhost:9900/login.html
管理后台: http://localhost:9900/admin/index.html
```

**命令行**
```bash
# 上传运维文档
curl -X POST http://localhost:9900/api/upload \
  -F "file=@aiops-docs/cpu_high_usage.md"

# 智能问答（支持工具调用）
curl -X POST http://localhost:9900/api/chat \
  -H "Content-Type: application/json" \
  -d '{"Id":"test","Question":"当前有哪些告警？"}'

# AIOps 告警分析
curl -X POST http://localhost:9900/api/ai_ops

# 管理员登录
curl -X POST http://localhost:9900/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 获取会话统计
curl -X GET http://localhost:9900/admin/stats

# 健康检查
curl http://localhost:9900/milvus/health
```

## 运维文档库

`aiops-docs/` 目录包含标准运维处理方案，系统启动时可通过 `make upload` 自动向量化：

| 文档 | 员警类型 | 说明 |
|------|----------|------|
| cpu_high_usage.md | HighCPUUsage | CPU 使用率 >80% 处理方案 |
| memory_high_usage.md | HighMemoryUsage | 内存使用率 >85% 处理方案 |
| disk_high_usage.md | HighDiskUsage | 磁盘使用率 >90% 处理方案 |
| service_unavailable.md | ServiceUnavailable | 服务不可用处理方案 |
| slow_response.md | SlowResponse | 响应时间 >3s 处理方案 |

## Mock 模式

系统支持 Mock 模式，无需真实 Prometheus 和日志服务即可测试：

```yaml
prometheus:
  mock-enabled: true  # 返回模拟告警数据

cls:
  mock-enabled: true  # 返回模拟日志数据
```

Mock 员警数据包含：
- HighCPUUsage: payment-service CPU 92%
- HighMemoryUsage: order-service 内存 91%
- SlowResponse: user-service P99 响应 4.2s

## Makefile 命令

```bash
make help     # 显示帮助信息
make init     # 一键初始化（启动 Docker → 启动服务 → 上传文档）
make up       # 启动 Docker Compose（Milvus）
make down     # 停止 Docker Compose
make status   # 查看 Docker 容器状态
make start    # 启动 Spring Boot 服务
make stop     # 停止 Spring Boot 服务
make restart  # 重启 Spring Boot 服务
make check    # 检查服务器是否运行
make upload   # 上传 aiops-docs 目录下的所有文档
make clean    # 清理临时文件
```

## 数据库实体

| 实体 | 说明 |
|------|------|
| User | 用户信息，包含用户名、昵称等 |
| ChatSession | 会话信息，包含会话ID、标题、状态、时间等 |
| ChatMessage | 聊天消息，包含用户消息和AI回复 |
| DailyStats | 每日统计数据，用于趋势分析 |

---
**版本**: v2.0.0
**作者**: zz
**许可证**: MIT