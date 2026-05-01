package org.example.backend.config;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 管理员用户初始化配置
 * 在应用启动时自动创建管理员账号
 */
@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);

    @Autowired
    private UserRepository userRepository;

    /**
     * 默认管理员账号配置
     */
    private static final String DEFAULT_ADMIN_USERNAME = "admin";
    private static final String DEFAULT_ADMIN_PASSWORD = "123456"; // 密码在LoginService中验证
    private static final String DEFAULT_ADMIN_NICKNAME = "系统管理员";

    @Override
    public void run(String... args) throws Exception {
        initAdminUser();
    }

    /**
     * 初始化管理员用户
     */
    private void initAdminUser() {
        try {
            // 检查管理员用户是否已存在
            boolean exists = userRepository.existsByUsername(DEFAULT_ADMIN_USERNAME);

            if (!exists) {
                // 创建管理员用户
                User admin = new User();
                admin.setUsername(DEFAULT_ADMIN_USERNAME);
                admin.setNickname(DEFAULT_ADMIN_NICKNAME);
                // 注意：密码验证在 LoginService 中进行，User实体不存储密码

                userRepository.save(admin);

                logger.info("========================================");
                logger.info("管理员用户已初始化完成");
                logger.info("账号: {}", DEFAULT_ADMIN_USERNAME);
                logger.info("密码: {}", DEFAULT_ADMIN_PASSWORD);
                logger.info("========================================");
            } else {
                logger.info("管理员用户已存在，跳过初始化");
            }

        } catch (Exception e) {
            logger.error("初始化管理员用户失败", e);
        }
    }
}