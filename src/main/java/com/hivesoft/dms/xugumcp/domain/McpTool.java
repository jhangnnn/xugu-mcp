package com.hivesoft.dms.xugumcp.domain;

import java.util.List;

/**
 * MCP 工具定义
 */
public class McpTool {
    private String name;
    private String description;
    private List<McpToolInput> inputSchema;

    public McpTool() {}

    public McpTool(String name, String description, List<McpToolInput> inputSchema) {
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<McpToolInput> getInputSchema() { return inputSchema; }
    public void setInputSchema(List<McpToolInput> inputSchema) { this.inputSchema = inputSchema; }

    /**
     * 工具输入参数定义
     */
    public static class McpToolInput {
        private String name;
        private String type;
        private String description;
        private boolean required;

        public McpToolInput() {}

        public McpToolInput(String name, String type, String description, boolean required) {
            this.name = name;
            this.type = type;
            this.description = description;
            this.required = required;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
    }
}
