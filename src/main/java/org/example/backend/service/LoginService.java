package org.example.backend.service;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 登录服务
 * 提供管理员登录验证功能
 */
@Service
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * 管理员登录验证
     * @param username 用户名
     * @param password 密码
     * @return 是否登录成功
     */
    public boolean login(String username, String password) {
        try {
            // 查询用户
            Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                logger.warn("用户不存在: {}", username);
                return false;
            }

            User user = userOptional.get();

            // 验证密码（简单密码匹配，实际应用中应使用加密）
            // 这里使用简单的密码比对，生产环境建议使用 BCrypt 等加密方式
            if (verifyPassword(password, user)) {
                logger.info("用户登录验证成功: {}", username);
                return true;
            } else {
                logger.warn("密码验证失败: {}", username);
                return false;
            }

        } catch (Exception e) {
            logger.error("登录验证异常: {}", username, e);
            return false;
        }
    }

    /**
     * 验证密码
     * 当前使用简单比对，生产环境建议使用加密存储
     */
    private boolean verifyPassword(String inputPassword, User user) {
        // 由于 User 实体当前没有密码字段，我们使用预设的管理员账号验证
        // 管理员账号: admin, 密码: 123456
        if ("admin".equals(user.getUsername()) && "123456".equals(inputPassword)) {
            return true;
        }
        return false;
    }

    /**
     * 检查用户是否存在
     * @param username 用户名
     * @return 是否存在
     */
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }
}