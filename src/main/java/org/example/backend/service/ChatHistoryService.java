package org.example.backend.service;

import org.example.entity.ChatMessage;
import org.example.entity.ChatSession;
import org.example.repository.ChatMessageRepository;
import org.example.repository.ChatSessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 对话历史持久化服务
 * 负责将对话历史存储到数据库
 * 前台和后台共享此服务
 */
@Service
public class ChatHistoryService {

    private static final Logger logger = LoggerFactory.getLogger(ChatHistoryService.class);

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    /**
     * 创建或获取会话
     */
    @Transactional
    public ChatSession getOrCreateSession(String sessionId) {
        return chatSessionRepository.findBySessionId(sessionId)
                .orElseGet(() -> {
                    ChatSession session = new ChatSession();
                    session.setSessionId(sessionId);
                    session.setStatus("active");
                    return chatSessionRepository.save(session);
                });
    }

    /**
     * 保存消息到数据库
     */
    @Transactional
    public void saveMessage(String sessionId, String role, String content) {
        try {
            ChatSession session = getOrCreateSession(sessionId);

            if ("user".equals(role) && session.getTitle() == null) {
                String title = content.length() > 50 ? content.substring(0, 50) + "..." : content;
                session.setTitle(title);
                chatSessionRepository.save(session);
            }

            ChatMessage message = new ChatMessage();
            message.setSessionId(sessionId);
            message.setRole(role);
            message.setContent(content);
            message.setTokenCount(estimateTokenCount(content));
            chatMessageRepository.save(message);

            logger.info("消息已保存 - SessionId: {}, Role: {}, ContentLength: {}",
                sessionId, role, content.length());
        } catch (Exception e) {
            logger.error("保存消息失败 - SessionId: {}", sessionId, e);
        }
    }

    /**
     * 保存一对消息（用户问题 + AI回复）
     */
    @Transactional
    public void saveMessagePair(String sessionId, String userQuestion, String aiAnswer) {
        saveMessage(sessionId, "user", userQuestion);
        saveMessage(sessionId, "assistant", aiAnswer);
    }

    /**
     * 清空会话历史
     */
    @Transactional
    public void clearSessionHistory(String sessionId) {
        try {
            chatMessageRepository.deleteBySessionId(sessionId);

            ChatSession session = chatSessionRepository.findBySessionId(sessionId).orElse(null);
            if (session != null) {
                session.setTitle(null);
                chatSessionRepository.save(session);
            }

            logger.info("会话历史已清空 - SessionId: {}", sessionId);
        } catch (Exception e) {
            logger.error("清空会话历史失败 - SessionId: {}", sessionId, e);
        }
    }

    /**
     * 获取会话的所有消息
     */
    public List<ChatMessage> getSessionMessages(String sessionId) {
        return chatMessageRepository.findBySessionIdOrderByCreateTimeAsc(sessionId);
    }

    /**
     * 更新会话状态
     */
    @Transactional
    public void updateSessionStatus(String sessionId, String status) {
        ChatSession session = chatSessionRepository.findBySessionId(sessionId).orElse(null);
        if (session != null) {
            session.setStatus(status);
            chatSessionRepository.save(session);
        }
    }

    /**
     * 估算 Token 数量
     */
    private int estimateTokenCount(String content) {
        if (content == null) return 0;
        return (int) Math.ceil(content.length() / 2.0);
    }
}