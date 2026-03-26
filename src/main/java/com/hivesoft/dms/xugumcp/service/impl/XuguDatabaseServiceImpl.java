package com.hivesoft.dms.xugumcp.service.impl;

import com.hivesoft.dms.xugumcp.config.XuGuConfig;
import com.hivesoft.dms.xugumcp.service.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;

/**
 * 虚谷数据库服务实现
 */
public class XuguDatabaseServiceImpl implements DatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(XuguDatabaseServiceImpl.class);

    private final XuGuConfig config;
    private Connection connection;

    public XuguDatabaseServiceImpl(XuGuConfig config) {
        this.config = config;
        initConnection();
    }

    private void initConnection() {
        try {
            Class.forName(config.getDriver());
            Properties props = new Properties();
            props.setProperty("user", config.getUsername());
            props.setProperty("password", config.getPassword());
            // 设置字符编码为 UTF-8
            props.setProperty("characterEncoding", "UTF-8");
            props.setProperty("useUnicode", "true");
            this.connection = DriverManager.getConnection(config.getUrl(), props);
            logger.info("Database connection established with UTF-8 encoding");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver not found: " + config.getDriver(), e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database", e);
        }
    }

    private Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initConnection();
        }
        return connection;
    }

    @Override
    public List<Map<String, Object>> executeQuery(String sql) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnLabel(i), rs.getObject(i));
                }
                results.add(row);
            }
            logger.info("Query executed successfully, returned {} rows", results.size());
        } catch (SQLException e) {
            logger.error("Query execution failed: {}", sql, e);
            throw new RuntimeException("Query failed: " + e.getMessage(), e);
        }
        return results;
    }

    @Override
    public int executeUpdate(String sql) {
        try (Statement stmt = getConnection().createStatement()) {
            int affected = stmt.executeUpdate(sql);
            logger.info("Update executed successfully, affected {} rows", affected);
            return affected;
        } catch (SQLException e) {
            logger.error("Update execution failed: {}", sql, e);
            throw new RuntimeException("Update failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean createTable(String sql) {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sql);
            logger.info("Table created successfully");
            return true;
        } catch (SQLException e) {
            logger.error("Create table failed: {}", sql, e);
            throw new RuntimeException("Create table failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean alterTable(String sql) {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute(sql);
            logger.info("Table altered successfully");
            return true;
        } catch (SQLException e) {
            logger.error("Alter table failed: {}", sql, e);
            throw new RuntimeException("Alter table failed: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean dropTable(String tableName) {
        try (Statement stmt = getConnection().createStatement()) {
            stmt.execute("DROP TABLE " + tableName);
            logger.info("Table dropped successfully: {}", tableName);
            return true;
        } catch (SQLException e) {
            logger.error("Drop table failed: {}", tableName, e);
            throw new RuntimeException("Drop table failed: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> getTableSchema(String tableName) {
        List<Map<String, Object>> results = new ArrayList<>();
        try {
            DatabaseMetaData metaData = getConnection().getMetaData();
            // 虚谷数据库的 catalog 和 schema
            try (ResultSet rs = metaData.getColumns(null, null, tableName, null)) {
                while (rs.next()) {
                    Map<String, Object> column = new LinkedHashMap<>();
                    column.put("COLUMN_NAME", rs.getString("COLUMN_NAME"));
                    column.put("DATA_TYPE", rs.getString("DATA_TYPE"));
                    column.put("TYPE_NAME", rs.getString("TYPE_NAME"));
                    column.put("COLUMN_SIZE", rs.getInt("COLUMN_SIZE"));
                    column.put("IS_NULLABLE", rs.getString("IS_NULLABLE"));
                    column.put("COLUMN_DEF", rs.getString("COLUMN_DEF"));
                    column.put("REMARKS", rs.getString("REMARKS"));
                    results.add(column);
                }
            }
            logger.info("Get table schema for {}, returned {} columns", tableName, results.size());
        } catch (SQLException e) {
            logger.error("Failed to get table schema: {}", tableName, e);
            throw new RuntimeException("Failed to get table schema: " + e.getMessage(), e);
        }
        return results;
    }

    @Override
    public List<String> listTables() {
        String sql = "SELECT TABLE_NAME FROM USER_TABLES";
        List<Map<String, Object>> results = executeQuery(sql);
        List<String> tables = new ArrayList<>();
        for (Map<String, Object> row : results) {
            tables.add((String) row.get("TABLE_NAME"));
        }
        return tables;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed");
            }
        } catch (SQLException e) {
            logger.error("Failed to close connection", e);
        }
    }
}
