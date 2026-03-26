package com.hivesoft.dms.xugumcp.server;

import com.alibaba.fastjson2.JSON;
import com.hivesoft.dms.xugumcp.config.XuGuConfig;
import com.hivesoft.dms.xugumcp.domain.McpRequest;
import com.hivesoft.dms.xugumcp.domain.McpResponse;
import com.hivesoft.dms.xugumcp.handler.McpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

/**
 * MCP STDIO 服务器
 * 通过标准输入输出与 MCP 客户端通信
 */
public class McpStdioServer {

    private static final Logger logger = LoggerFactory.getLogger(McpStdioServer.class);

    private McpRequestHandler handler;
    private boolean configLoaded = false;

    public McpStdioServer() {
        try {
            XuGuConfig config = XuGuConfig.load();
            this.handler = new McpRequestHandler(config);
            this.configLoaded = true;
            logger.info("MCP Server initialized with database connection");
        } catch (Exception e) {
            logger.warn("Failed to initialize database connection: {}. Server will start but database operations will fail.", e.getMessage());
            this.handler = new McpRequestHandler(null);
            this.configLoaded = false;
        }
    }

    public void start() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, java.nio.charset.StandardCharsets.UTF_8));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out, java.nio.charset.StandardCharsets.UTF_8));

        logger.info("MCP Server started, listening on stdin/stdout");

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                logger.debug("Received: {}", line);

                try {
                    McpRequest request = JSON.parseObject(line, McpRequest.class);

                    // 处理 initialize 请求 - 此时可能还没有数据库配置
                    if (request.getMethod().equals("initialize")) {
                        Map<String, Object> result = new java.util.LinkedHashMap<>();
                        result.put("protocolVersion", "2024-11-05");
                        result.put("capabilities", Map.of("tools", Map.of()));
                        result.put("serverInfo", Map.of("name", "xugu-mcp", "version", "1.0"));

                        McpResponse response = McpResponse.success(result, request.getId());
                        String responseJson = JSON.toJSONString(response);
                        writer.write(responseJson);
                        writer.newLine();
                        writer.flush();
                        logger.debug("Sent: {}", responseJson);
                        continue;
                    }

                    // 处理 tools/list 请求
                    if (request.getMethod().equals("tools/list")) {
                        McpResponse response = handler.handle(request);
                        String responseJson = JSON.toJSONString(response);
                        writer.write(responseJson);
                        writer.newLine();
                        writer.flush();
                        logger.debug("Sent: {}", responseJson);
                        continue;
                    }

                    // 其他请求需要数据库连接
                    if (!configLoaded) {
                        McpResponse errorResponse = McpResponse.error(-32603, "Database not configured. Please set environment variables or config file.", request.getId());
                        writer.write(JSON.toJSONString(errorResponse));
                        writer.newLine();
                        writer.flush();
                        continue;
                    }

                    McpResponse response = handler.handle(request);

                    String responseJson = JSON.toJSONString(response);
                    writer.write(responseJson);
                    writer.newLine();
                    writer.flush();

                    logger.debug("Sent: {}", responseJson);
                } catch (Exception e) {
                    logger.error("Failed to process request", e);
                    McpResponse errorResponse = McpResponse.error(-32700, "Parse error: " + e.getMessage(), null);
                    writer.write(JSON.toJSONString(errorResponse));
                    writer.newLine();
                    writer.flush();
                }
            }
        } catch (IOException e) {
            logger.error("IO error", e);
        }
    }
}
