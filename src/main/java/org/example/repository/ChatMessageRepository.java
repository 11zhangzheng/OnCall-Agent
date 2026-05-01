package org.example.repository;

import org.example.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话消息 Repository
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findBySessionIdOrderByCreateTimeAsc(String sessionId);

    Page<ChatMessage> findBySessionIdOrderByCreateTimeAsc(String sessionId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.sessionId = :sessionId")
    Long countBySessionId(@Param("sessionId") String sessionId);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.createTime >= :startTime AND m.createTime < :endTime")
    Long countByCreateTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT SUM(m.tokenCount) FROM ChatMessage m WHERE m.sessionId = :sessionId")
    Long sumTokenCountBySessionId(@Param("sessionId") String sessionId);

    @Query("SELECT m FROM ChatMessage m WHERE m.role = 'user' ORDER BY m.createTime DESC")
    Page<ChatMessage> findUserMessagesOrderByCreateTimeDesc(Pageable pageable);

    void deleteBySessionId(String sessionId);

    @Query("SELECT COUNT(m) FROM ChatMessage m")
    Long countTotalMessages();
}