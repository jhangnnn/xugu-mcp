package com.hivesoft.dms.xugumcp;

import com.hivesoft.dms.xugumcp.server.McpStdioServer;

/**
 * 虚谷数据库 MCP 服务器启动类
 */
public class XuGuMcpApplication {

    public static void main(String[] args) {
        System.out.println("Starting XuGu MCP Server...");
        McpStdioServer server = new McpStdioServer();
        server.start();
    }
}
