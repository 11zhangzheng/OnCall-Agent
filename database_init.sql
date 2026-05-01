-- ===========================================
-- OnCall Agent 后台管理模块数据库初始化脚本
-- 数据库: oncall_agent
-- 创建日期: 2024
-- ===========================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS oncall_agent
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE oncall_agent;

-- ===========================================
-- 1. 用户表 (users)
-- ===========================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户唯一标识',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名（唯一）',
    nickname VARCHAR(100) COMMENT '用户昵称',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ===========================================
-- 2. 会话表 (chat_sessions)
-- ===========================================
CREATE TABLE IF NOT EXISTS chat_sessions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话记录标识',
    session_id VARCHAR(100) UNIQUE NOT NULL COMMENT '业务会话编号',
    user_id BIGINT COMMENT '关联用户ID',
    title VARCHAR(200) COMMENT '会话标题（从首条消息提取）',
    status VARCHAR(20) DEFAULT 'active' COMMENT '会话状态（active/closed）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

    -- 外键约束（关联用户表）
    CONSTRAINT fk_session_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话会话表';

-- 会话状态索引（用于活跃会话筛选）
CREATE INDEX idx_session_status ON chat_sessions(status);
-- 创建时间索引（用于统计查询）
CREATE INDEX idx_session_create_time ON chat_sessions(create_time);
-- 更新时间索引（用于排序查询）
CREATE INDEX idx_session_update_time ON chat_sessions(update_time);

-- ===========================================
-- 3. 消息表 (chat_messages)
-- ===========================================
CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息记录标识',
    session_id VARCHAR(100) NOT NULL COMMENT '关联会话ID',
    role VARCHAR(20) NOT NULL COMMENT '消息角色（user/assistant）',
    content TEXT NOT NULL COMMENT '消息内容',
    token_count INT DEFAULT 0 COMMENT 'Token计数估算',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    -- 外键约束（关联会话表）
    CONSTRAINT fk_message_session FOREIGN KEY (session_id) REFERENCES chat_sessions(session_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对话消息表';

-- 会话ID索引（用于按会话查询消息）
CREATE INDEX idx_message_session_id ON chat_messages(session_id);
-- 创建时间索引（用于消息排序和统计）
CREATE INDEX idx_message_create_time ON chat_messages(create_time);
-- 角色索引（用于按角色筛选）
CREATE INDEX idx_message_role ON chat_messages(role);

-- ===========================================
-- 4. 每日统计表 (daily_stats)
-- ===========================================
CREATE TABLE IF NOT EXISTS daily_stats (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计记录标识',
    stat_date DATE UNIQUE NOT NULL COMMENT '统计日期（唯一）',
    total_sessions INT DEFAULT 0 COMMENT '当日会话总数',
    total_messages INT DEFAULT 0 COMMENT '当日消息总数',
    total_users INT DEFAULT 0 COMMENT '当日用户总数',
    avg_session_length FLOAT DEFAULT 0 COMMENT '平均会话长度（消息数/会话数）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日统计汇总表';

-- 统计日期索引（用于日期查询）
CREATE INDEX idx_stat_date ON daily_stats(stat_date);

-- ===========================================
-- 初始化数据（可选）
-- ===========================================

-- 插入测试用户（可选）
-- INSERT INTO users (username, nickname) VALUES ('admin', '管理员');

-- ===========================================
-- 查询验证
-- ===========================================

-- 查看所有表结构
SHOW TABLES;

-- 查看各表字段结构
DESCRIBE users;
DESCRIBE chat_sessions;
DESCRIBE chat_messages;
DESCRIBE daily_stats;