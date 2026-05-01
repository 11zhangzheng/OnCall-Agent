package org.example.backend.service;

import org.example.entity.DailyStats;
import org.example.repository.ChatMessageRepository;
import org.example.repository.ChatSessionRepository;
import org.example.repository.DailyStatsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * 统计分析服务
 * 提供每日统计、热门问题等分析功能
 */
@Service
public class StatsService {

    private static final Logger logger = LoggerFactory.getLogger(StatsService.class);

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private DailyStatsRepository dailyStatsRepository;

    /**
     * 获取总体统计信息
     */
    public Map<String, Object> getOverallStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSessions", chatSessionRepository.countTotalSessions());
        stats.put("totalMessages", chatMessageRepository.countTotalMessages());
        stats.put("activeSessions", chatSessionRepository.findByStatus("active").size());
        stats.put("totalUsers", chatSessionRepository.countDistinctUsers());

        // 今日统计
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        stats.put("todaySessions", chatSessionRepository.countByCreateTimeBetween(todayStart, todayEnd));
        stats.put("todayMessages", chatMessageRepository.countByCreateTimeBetween(todayStart, todayEnd));

        return stats;
    }

    /**
     * 获取每日统计数据（最近N天）
     */
    public List<DailyStats> getDailyStats(int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        List<DailyStats> result = new ArrayList<>();
        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            Optional<DailyStats> stats = dailyStatsRepository.findByStatDate(date);
            LocalDate finalDate = date;
            result.add(stats.orElseGet(() -> createEmptyStats(finalDate)));
        }
        return result;
    }

    /**
     * 更新每日统计
     */
    @Transactional
    public void updateDailyStats(LocalDate date) {
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

        long sessions = chatSessionRepository.countByCreateTimeBetween(dayStart, dayEnd);
        long messages = chatMessageRepository.countByCreateTimeBetween(dayStart, dayEnd);
        long users = chatSessionRepository.countDistinctUsers();

        DailyStats stats = dailyStatsRepository.findByStatDate(date)
                .orElseGet(() -> {
                    DailyStats newStats = new DailyStats();
                    newStats.setStatDate(date);
                    return newStats;
                });

        stats.setTotalSessions((int) sessions);
        stats.setTotalMessages((int) messages);
        stats.setTotalUsers((int) users);

        if (sessions > 0) {
            stats.setAvgSessionLength((float) messages / sessions);
        }

        dailyStatsRepository.save(stats);
        logger.info("每日统计已更新 - Date: {}, Sessions: {}, Messages: {}", date, sessions, messages);
    }

    /**
     * 获取最近N天的趋势数据
     */
    public Map<String, Object> getTrendData(int days) {
        Map<String, Object> trendData = new HashMap<>();
        List<Map<String, Object>> sessionTrend = new ArrayList<>();
        List<Map<String, Object>> messageTrend = new ArrayList<>();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);

        for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

            long sessions = chatSessionRepository.countByCreateTimeBetween(dayStart, dayEnd);
            long messages = chatMessageRepository.countByCreateTimeBetween(dayStart, dayEnd);

            final String dateStr = date.toString();
            final long sessionCount = sessions;
            final long messageCount = messages;

            sessionTrend.add(Map.of("date", dateStr, "count", sessionCount));
            messageTrend.add(Map.of("date", dateStr, "count", messageCount));
        }

        trendData.put("sessionTrend", sessionTrend);
        trendData.put("messageTrend", messageTrend);
        return trendData;
    }

    /**
     * 创建空的统计数据
     */
    private DailyStats createEmptyStats(LocalDate date) {
        DailyStats stats = new DailyStats();
        stats.setStatDate(date);
        stats.setTotalSessions(0);
        stats.setTotalMessages(0);
        stats.setTotalUsers(0);
        stats.setAvgSessionLength(0f);
        return stats;
    }
}