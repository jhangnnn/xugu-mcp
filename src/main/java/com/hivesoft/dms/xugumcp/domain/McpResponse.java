package com.hivesoft.dms.xugumcp.domain;

/**
 * MCP 响应对象
 */
public class McpResponse {
    private String jsonrpc = "2.0";
    private Object result;
    private McpError error;
    private String id;

    public McpResponse() {}

    public McpResponse(String jsonrpc, Object result, McpError error, String id) {
        this.jsonrpc = jsonrpc;
        this.result = result;
        this.error = error;
        this.id = id;
    }

    public static McpResponse success(Object result, String id) {
        return new McpResponse("2.0", result, null, id);
    }

    public static McpResponse error(int code, String message, String id) {
        return new McpResponse("2.0", null, new McpError(code, message), id);
    }

    public String getJsonrpc() { return jsonrpc; }
    public void setJsonrpc(String jsonrpc) { this.jsonrpc = jsonrpc; }
    public Object getResult() { return result; }
    public void setResult(Object result) { this.result = result; }
    public McpError getError() { return error; }
    public void setError(McpError error) { this.error = error; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    /**
     * MCP 错误对象
     */
    public static class McpError {
        private int code;
        private String message;

        public McpError() {}

        public McpError(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
