package com.hivesoft.dms.xugumcp.service;

import java.util.List;
import java.util.Map;

/**
 * 数据库服务接口
 */
public interface DatabaseService {

    /**
     * 执行查询语句
     */
    List<Map<String, Object>> executeQuery(String sql);

    /**
     * 执行更新语句（INSERT/UPDATE/DELETE）
     */
    int executeUpdate(String sql);

    /**
     * 创建表
     */
    boolean createTable(String sql);

    /**
     * 修改表
     */
    boolean alterTable(String sql);

    /**
     * 删除表
     */
    boolean dropTable(String tableName);

    /**
     * 获取表结构
     */
    List<Map<String, Object>> getTableSchema(String tableName);

    /**
     * 获取表列表
     */
    List<String> listTables();
}
