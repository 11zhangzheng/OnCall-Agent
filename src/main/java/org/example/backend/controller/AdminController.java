package org.example.backend.controller;

import lombok.Getter;
import lombok.Setter;
import org.example.entity.ChatMessage;
import org.example.entity.ChatSession;
import org.example.backend.service.AdminService;
import org.example.backend.service.StatsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 后台管理控制器
 * 提供会话管理、统计分析等 API
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private StatsService statsService;

    /**
     * 获取所有会话列表（分页）
     */
    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<Page<ChatSession>>> getAllSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            logger.info("获取会话列表 - Page: {}, Size: {}", page, size);
            Page<ChatSession> sessions = adminService.getAllSessions(page, size);
            return ResponseEntity.ok(ApiResponse.success(sessions));
        } catch (Exception e) {
            logger.error("获取会话列表失败", e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取活跃会话列表
     */
    @GetMapping("/sessions/active")
    public ResponseEntity<ApiResponse<Page<ChatSession>>> getActiveSessions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<ChatSession> sessions = adminService.getActiveSessions(page, size);
            return ResponseEntity.ok(ApiResponse.success(sessions));
        } catch (Exception e) {
            logger.error("获取活跃会话列表失败", e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取会话详情
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<ChatSession>> getSessionById(@PathVariable String sessionId) {
        try {
            logger.info("获取会话详情 - SessionId: {}", sessionId);
            Optional<ChatSession> session = adminService.getSessionById(sessionId);
            if (session.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success(session.get()));
            } else {
                return ResponseEntity.ok(ApiResponse.error("会话不存在"));
            }
        } catch (Exception e) {
            logger.error("获取会话详情失败", e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取会话的所有消息
     */
    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessage>>> getSessionMessages(@PathVariable String sessionId) {
        try {
            logger.info("获取会话消息 - SessionId: {}", sessionId);
            List<ChatMessage> messages = adminService.getSessionMessages(sessionId);
            return ResponseEntity.ok(ApiResponse.success(messages));
        } catch (Exception e) {
            logger.error("获取会话消息失败", e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取会话消息（分页）
     */
    @GetMapping("/sessions/{sessionId}/messages/page")
    public ResponseEntity<ApiResponse<Page<ChatMessage>>> getSessionMessagesPage(
            @PathVariable String sessionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<ChatMessage> messages = adminService.getSessionMessages(sessionId, page, size);
            return ResponseEntity.ok(ApiResponse.success(messages));
        } catch (Exception e) {
            logger.error("获取会话消息分页失败", e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 删除会话
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<ApiResponse<String>> deleteSession(@PathVariable String sessionId) {
        try {
            logger.info("删除会话 - SessionId: {}", sessionId);
            boolean success = adminService.deleteSession(sessionId);
            if (success) {
                return ResponseEntity.ok(ApiResponse.success("会话已删除"));
            } else {
                return ResponseEntity.ok(ApiResponse.error("删除失败"));
            }
        } catch (Exception e) {
            logger.error("删除会话失败", e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取总体统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getOverallStats() {
        try {
            logger.info("获取总体统计信息");
            Map<String, Object> stats = statsService.getOverallStats();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            logger.error("获取统计信息失败", e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取会话统计信息
     */
    @GetMapping("/stats/sessions")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSessionStats() {
        try {
            Map<String, Object> stats = adminService.getSessionStats();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            logger.error("获取会话统计失败", e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取每日统计数据
     */
    @GetMapping("/stats/daily")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getDailyStats(
            @RequestParam(defaultValue = "7") int days) {
        try {
            logger.info("获取每日统计 - Days: {}", days);
            List<Map<String, Object>> trends = (List<Map<String, Object>>) statsService.getTrendData(days).get("sessionTrend");
            return ResponseEntity.ok(ApiResponse.success(trends));
        } catch (Exception e) {
            logger.error("获取每日统计失败", e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取趋势数据
     */
    @GetMapping("/stats/trend")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTrendData(
            @RequestParam(defaultValue = "7") int days) {
        try {
            Map<String, Object> trendData = statsService.getTrendData(days);
            return ResponseEntity.ok(ApiResponse.success(trendData));
        } catch (Exception e) {
            logger.error("获取趋势数据失败", e);
            return ResponseEntity.ok(ApiResponse.error(e.getMessage()));
        }
    }

    // ==================== 内部类 ====================

    @Getter
    @Setter
    public static class ApiResponse<T> {
        private int code;
        private String message;
        private T data;

        public static <T> ApiResponse<T> success(T data) {
            ApiResponse<T> response = new ApiResponse<>();
            response.setCode(200);
            response.setMessage("success");
            response.setData(data);
            return response;
        }

        public static <T> ApiResponse<T> error(String message) {
            ApiResponse<T> response = new ApiResponse<>();
            response.setCode(500);
            response.setMessage(message);
            return response;
        }
    }
}