package com.hivesoft.dms.xugumcp.server;

import com.alibaba.fastjson2.JSON;
import com.hivesoft.dms.xugumcp.config.XuGuConfig;
import com.hivesoft.dms.xugumcp.domain.McpRequest;
import com.hivesoft.dms.xugumcp.domain.McpResponse;
import com.hivesoft.dms.xugumcp.handler.McpRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * MCP STDIO 服务器
 * 通过标准输入输出与 MCP 客户端通信
 */
public class McpStdioServer {

    private static final Logger logger = LoggerFactory.getLogger(McpStdioServer.class);

    private final McpRequestHandler handler;

    public McpStdioServer() {
        XuGuConfig config = XuGuConfig.load();
        this.handler = new McpRequestHandler(config);
        logger.info("MCP Server initialized");
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
