package org.example.repository;

import org.example.entity.ChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 会话 Repository
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findBySessionId(String sessionId);

    List<ChatSession> findByStatus(String status);

    Page<ChatSession> findByStatus(String status, Pageable pageable);

    @Query("SELECT COUNT(s) FROM ChatSession s WHERE s.createTime >= :startTime AND s.createTime < :endTime")
    Long countByCreateTimeBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT s FROM ChatSession s ORDER BY s.updateTime DESC")
    Page<ChatSession> findAllOrderByUpdateTimeDesc(Pageable pageable);

    @Query("SELECT COUNT(s) FROM ChatSession s")
    Long countTotalSessions();

    @Query("SELECT COUNT(DISTINCT s.userId) FROM ChatSession s WHERE s.userId IS NOT NULL")
    Long countDistinctUsers();
}