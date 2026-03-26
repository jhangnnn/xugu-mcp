package com.hivesoft.dms.xugumcp.domain;

/**
 * MCP 请求对象
 */
public class McpRequest {
    private String jsonrpc = "2.0";
    private String method;
    private Object params;
    private String id;

    public String getJsonrpc() { return jsonrpc; }
    public void setJsonrpc(String jsonrpc) { this.jsonrpc = jsonrpc; }
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
    public Object getParams() { return params; }
    public void setParams(Object params) { this.params = params; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
