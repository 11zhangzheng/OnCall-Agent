package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 每日统计实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "daily_stats")
public class DailyStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private LocalDate statDate;

    @Column(name = "total_sessions")
    private Integer totalSessions = 0;

    @Column(name = "total_messages")
    private Integer totalMessages = 0;

    @Column(name = "total_users")
    private Integer totalUsers = 0;

    @Column(name = "avg_session_length")
    private Float avgSessionLength = 0f;

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @PrePersist
    public void onCreate() {
        createTime = LocalDateTime.now();
        if (totalSessions == null) totalSessions = 0;
        if (totalMessages == null) totalMessages = 0;
        if (totalUsers == null) totalUsers = 0;
        if (avgSessionLength == null) avgSessionLength = 0f;
    }
}