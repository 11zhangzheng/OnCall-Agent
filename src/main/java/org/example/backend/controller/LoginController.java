package org.example.backend.controller;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.service.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 登录控制器
 * 提供管理员登录验证接口
 */
@RestController
@RequestMapping("/admin")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    /**
     * 管理员登录接口
     * @param request 登录请求（包含用户名和密码）
     * @return 登录结果
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            logger.info("收到登录请求 - Username: {}", request.getUsername());

            // 参数校验
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("账号不能为空"));
            }

            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return ResponseEntity.ok(ApiResponse.error("密码不能为空"));
            }

            // 执行登录验证
            boolean success = loginService.login(request.getUsername(), request.getPassword());

            if (success) {
                logger.info("登录成功 - Username: {}", request.getUsername());
                LoginResponse response = new LoginResponse();
                response.setSuccess(true);
                response.setUsername(request.getUsername());
                response.setMessage("登录成功");
                return ResponseEntity.ok(ApiResponse.success(response));
            } else {
                logger.warn("登录失败 - Username: {}", request.getUsername());
                return ResponseEntity.ok(ApiResponse.error("账号或密码错误"));
            }

        } catch (Exception e) {
            logger.error("登录处理失败", e);
            return ResponseEntity.ok(ApiResponse.error("系统错误，请稍后重试"));
        }
    }

    /**
     * 检查登录状态
     * @return 登录状态信息
     */
    @GetMapping("/check-login")
    public ResponseEntity<ApiResponse<LoginStatusResponse>> checkLogin() {
        LoginStatusResponse response = new LoginStatusResponse();
        response.setLoggedIn(true); // 简化处理，实际应用中需要验证session/token
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 登出接口
     * @return 操作结果
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        logger.info("用户登出");
        return ResponseEntity.ok(ApiResponse.success("已退出登录"));
    }

    // ==================== 内部类 ====================

    @Getter
    @Setter
    public static class LoginRequest {
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class LoginResponse {
        private boolean success;
        private String username;
        private String message;
    }

    @Getter
    @Setter
    public static class LoginStatusResponse {
        private boolean loggedIn;
        private String username;
    }

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