package com.hivesoft.dms.xugumcp.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.hivesoft.dms.xugumcp.config.XuGuConfig;
import com.hivesoft.dms.xugumcp.domain.McpRequest;
import com.hivesoft.dms.xugumcp.domain.McpResponse;
import com.hivesoft.dms.xugumcp.domain.McpTool;
import com.hivesoft.dms.xugumcp.service.DatabaseService;
import com.hivesoft.dms.xugumcp.service.impl.XuguDatabaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * MCP 请求处理器
 */
public class McpRequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(McpRequestHandler.class);

    private static final String SERVER_NAME = "xugu";

    private final DatabaseService databaseService;

    public McpRequestHandler(XuGuConfig config) {
        this.databaseService = new XuguDatabaseServiceImpl(config);
    }

    public McpResponse handle(McpRequest request) {
        String method = request.getMethod();
        String id = request.getId();

        logger.info("Handling request: method={}, id={}", method, id);

        try {
            // 支持标准 MCP 工具调用格式: mcp__xugu__execute_query
            if (method.startsWith("mcp__" + SERVER_NAME + "__")) {
                String toolName = method.substring(("mcp__" + SERVER_NAME + "__").length());
                return handleDirectToolCall(toolName, request);
            }

            return switch (method) {
                case "initialize" -> handleInitialize(request);
                case "tools/list" -> handleListTools(request);
                case "tools/call" -> handleCallTool(request);
                default -> McpResponse.error(-32601, "Method not found: " + method, id);
            };
        } catch (Exception e) {
            logger.error("Error handling request: {}", method, e);
            return McpResponse.error(-32603, "Internal error: " + e.getMessage(), id);
        }
    }

    /**
     * 处理直接工具调用（Claude Code 风格）
     * 格式: mcp__xugu__execute_query
     */
    private McpResponse handleDirectToolCall(String toolName, McpRequest request) {
        logger.info("Direct tool call: toolName={}", toolName);

        JSONObject params = (JSONObject) request.getParams();
        JSONObject args = params != null ? params.getJSONObject("arguments") : new JSONObject();

        Object result;
        switch (toolName) {
            case "execute_query" -> {
                String sql = args.getString("sql");
                result = databaseService.executeQuery(sql);
            }
            case "execute_update" -> {
                String sql = args.getString("sql");
                int affected = databaseService.executeUpdate(sql);
                result = Map.of("affected_rows", affected);
            }
            case "create_table" -> {
                String sql = args.getString("sql");
                databaseService.createTable(sql);
                result = Map.of("success", true);
            }
            case "alter_table" -> {
                String sql = args.getString("sql");
                databaseService.alterTable(sql);
                result = Map.of("success", true);
            }
            case "drop_table" -> {
                String tableName = args.getString("table_name");
                databaseService.dropTable(tableName);
                result = Map.of("success", true);
            }
            case "get_table_schema" -> {
                String tableName = args.getString("table_name");
                result = databaseService.getTableSchema(tableName);
            }
            case "list_tables" -> {
                result = databaseService.listTables();
            }
            default -> {
                return McpResponse.error(-32602, "Unknown tool: " + toolName, request.getId());
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", List.of(Map.of(
            "type", "text",
            "text", JSON.toJSONString(result)
        )));
        return McpResponse.success(response, request.getId());
    }

    private McpResponse handleInitialize(McpRequest request) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("protocolVersion", "2024-11-05");
        result.put("capabilities", Map.of(
            "tools", Map.of()
        ));
        result.put("serverInfo", Map.of(
            "name", "xugu-mcp",
            "version", "1.0"
        ));
        return McpResponse.success(result, request.getId());
    }

    private McpResponse handleListTools(McpRequest request) {
        // 工具名称使用简单名称，Claude Code 会自动添加 mcp__xugu__ 前缀
        List<McpTool> tools = Arrays.asList(
            new McpTool("execute_query", "执行 SELECT 查询语句，返回结果集", List.of(
                new McpTool.McpToolInput("sql", "string", "SELECT 查询语句", true)
            )),
            new McpTool("execute_update", "执行 INSERT/UPDATE/DELETE 语句", List.of(
                new McpTool.McpToolInput("sql", "string", "INSERT/UPDATE/DELETE 语句", true)
            )),
            new McpTool("create_table", "创建新表 (DDL)", List.of(
                new McpTool.McpToolInput("sql", "string", "CREATE TABLE 语句", true)
            )),
            new McpTool("alter_table", "修改表结构 (DDL)", List.of(
                new McpTool.McpToolInput("sql", "string", "ALTER TABLE 语句", true)
            )),
            new McpTool("drop_table", "删除表 (DDL)", List.of(
                new McpTool.McpToolInput("table_name", "string", "要删除的表名", true)
            )),
            new McpTool("get_table_schema", "获取表结构信息", List.of(
                new McpTool.McpToolInput("table_name", "string", "表名", true)
            )),
            new McpTool("list_tables", "列出所有表", Collections.emptyList())
        );

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("tools", tools);
        return McpResponse.success(result, request.getId());
    }

    private McpResponse handleCallTool(McpRequest request) {
        JSONObject params = (JSONObject) request.getParams();
        String toolName = params.getString("name");
        JSONObject args = params.getJSONObject("arguments");

        logger.info("Calling tool: {}, args: {}", toolName, args);

        // 去掉工具名称的前缀（如果有）
        String actualToolName = toolName;
        if (toolName.startsWith("mcp__" + SERVER_NAME + "__")) {
            actualToolName = toolName.substring(("mcp__" + SERVER_NAME + "__").length());
        }

        Object result;
        switch (actualToolName) {
            case "execute_query" -> {
                String sql = args.getString("sql");
                result = databaseService.executeQuery(sql);
            }
            case "execute_update" -> {
                String sql = args.getString("sql");
                int affected = databaseService.executeUpdate(sql);
                result = Map.of("affected_rows", affected);
            }
            case "create_table" -> {
                String sql = args.getString("sql");
                databaseService.createTable(sql);
                result = Map.of("success", true);
            }
            case "alter_table" -> {
                String sql = args.getString("sql");
                databaseService.alterTable(sql);
                result = Map.of("success", true);
            }
            case "drop_table" -> {
                String tableName = args.getString("table_name");
                databaseService.dropTable(tableName);
                result = Map.of("success", true);
            }
            case "get_table_schema" -> {
                String tableName = args.getString("table_name");
                result = databaseService.getTableSchema(tableName);
            }
            case "list_tables" -> {
                result = databaseService.listTables();
            }
            default -> {
                return McpResponse.error(-32602, "Unknown tool: " + toolName, request.getId());
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", List.of(Map.of(
            "type", "text",
            "text", JSON.toJSONString(result)
        )));
        return McpResponse.success(response, request.getId());
    }
}
