package org.example.backend.service;

import org.example.entity.ChatMessage;
import org.example.entity.ChatSession;
import org.example.repository.ChatMessageRepository;
import org.example.repository.ChatSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 后台管理服务
 * 提供会话查询、删除等管理功能
 */
@Service
public class AdminService {

    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    /**
     * 获取所有会话列表（分页）
     */
    public Page<ChatSession> getAllSessions(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updateTime"));
        return chatSessionRepository.findAllOrderByUpdateTimeDesc(pageRequest);
    }

    /**
     * 获取活跃会话列表（分页）
     */
    public Page<ChatSession> getActiveSessions(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updateTime"));
        return chatSessionRepository.findByStatus("active", pageRequest);
    }

    /**
     * 获取会话详情
     */
    public Optional<ChatSession> getSessionById(String sessionId) {
        return chatSessionRepository.findBySessionId(sessionId);
    }

    /**
     * 获取会话的消息列表
     */
    public List<ChatMessage> getSessionMessages(String sessionId) {
        return chatMessageRepository.findBySessionIdOrderByCreateTimeAsc(sessionId);
    }

    /**
     * 获取会话的消息列表（分页）
     */
    public Page<ChatMessage> getSessionMessages(String sessionId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "createTime"));
        return chatMessageRepository.findBySessionIdOrderByCreateTimeAsc(sessionId, pageRequest);
    }

    /**
     * 删除会话（包括所有消息）
     */
    @Transactional
    public boolean deleteSession(String sessionId) {
        try {
            chatMessageRepository.deleteBySessionId(sessionId);
            ChatSession session = chatSessionRepository.findBySessionId(sessionId).orElse(null);
            if (session != null) {
                chatSessionRepository.delete(session);
            }
            logger.info("会话已删除 - SessionId: {}", sessionId);
            return true;
        } catch (Exception e) {
            logger.error("删除会话失败 - SessionId: {}", sessionId, e);
            return false;
        }
    }

    /**
     * 获取会话统计信息
     */
    public Map<String, Object> getSessionStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", chatSessionRepository.countTotalSessions());
        stats.put("totalMessages", chatMessageRepository.countTotalMessages());
        stats.put("activeSessions", chatSessionRepository.findByStatus("active").size());
        stats.put("totalUsers", chatSessionRepository.countDistinctUsers());
        return stats;
    }

    /**
     * 搜索会话（按标题）
     */
    public Page<ChatSession> searchSessions(String keyword, int page, int size) {
        return getAllSessions(page, size);
    }
}