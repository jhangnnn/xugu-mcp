package com.hivesoft.dms.xugumcp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 虚谷数据库配置类
 * 支持环境变量配置
 *
 * 配置方式（二选一）：
 * - 方式一：XUGU_URL + XUGU_USERNAME + XUGU_PASSWORD
 * - 方式二：XUGU_HOST + XUGU_PORT + XUGU_DATABASE + XUGU_USERNAME + XUGU_PASSWORD
 *
 * 可选的环境变量：
 * - XUGU_DRIVER   : JDBC 驱动类名 (默认: com.xugu.cloudjdbc.Driver)
 * - XUGU_POOL_SIZE: 连接池大小 (默认: 5)
 */
public class XuGuConfig {

    private static final Logger logger = LoggerFactory.getLogger(XuGuConfig.class);

    private String url;
    private String username;
    private String password;
    private String driver;
    private int poolSize;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getDriver() { return driver; }
    public void setDriver(String driver) { this.driver = driver; }
    public int getPoolSize() { return poolSize; }
    public void setPoolSize(int poolSize) { this.poolSize = poolSize; }

    @Override
    public String toString() {
        // 隐藏密码
        return "XuGuConfig{url='" + url + "', username='" + username + "', driver='" + driver + "'}";
    }

    public static XuGuConfig load() {
        XuGuConfig config = new XuGuConfig();

        String url = System.getenv("XUGU_URL");
        String username = System.getenv("XUGU_USERNAME");
        String password = System.getenv("XUGU_PASSWORD");

        // 方式一：使用完整 URL
        if (url != null && !url.trim().isEmpty()
            && username != null && !username.trim().isEmpty()
            && password != null && !password.trim().isEmpty()) {
            config.setUrl(url);
            config.setUsername(username);
            config.setPassword(password);
        }
        // 方式二：使用分散的组件参数
        else {
            String host = System.getenv("XUGU_HOST");
            String port = System.getenv("XUGU_PORT");
            String database = System.getenv("XUGU_DATABASE");

            if ((host == null || host.trim().isEmpty())
                || (port == null || port.trim().isEmpty())
                || (database == null || database.trim().isEmpty())
                || (username == null || username.trim().isEmpty())
                || (password == null || password.trim().isEmpty())) {

                StringBuilder missing = new StringBuilder();
                if (url == null || url.trim().isEmpty()) missing.append("XUGU_URL, ");
                if (host == null || host.trim().isEmpty()) missing.append("XUGU_HOST, ");
                if (port == null || port.trim().isEmpty()) missing.append("XUGU_PORT, ");
                if (database == null || database.trim().isEmpty()) missing.append("XUGU_DATABASE, ");
                if (username == null || username.trim().isEmpty()) missing.append("XUGU_USERNAME, ");
                if (password == null || password.trim().isEmpty()) missing.append("XUGU_PASSWORD, ");

                missing.setLength(missing.length() - 2);
                throw new IllegalStateException(
                    "缺少必需的环境变量: " + missing + ". " +
                    "请设置 (XUGU_URL + XUGU_USERNAME + XUGU_PASSWORD) 或 " +
                    "(XUGU_HOST + XUGU_PORT + XUGU_DATABASE + XUGU_USERNAME + XUGU_PASSWORD)"
                );
            }

            config.setUrl("jdbc:xugu://" + host + ":" + port + "/" + database);
            config.setUsername(username);
            config.setPassword(password);
        }

        // 可选参数使用默认值
        config.setDriver(getEnvOrDefault("XUGU_DRIVER", "com.xugu.cloudjdbc.Driver"));
        config.setPoolSize(Integer.parseInt(getEnvOrDefault("XUGU_POOL_SIZE", "5")));

        logger.info("Database config loaded successfully");
        return config;
    }

    private static String getEnvOrDefault(String envName, String defaultValue) {
        String value = System.getenv(envName);
        return (value != null && !value.trim().isEmpty()) ? value : defaultValue;
    }
}
