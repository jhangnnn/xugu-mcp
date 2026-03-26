package com.hivesoft.dms.xugumcp.domain;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.ArrayList;

/**
 * MCP 工具定义
 * 符合 JSON Schema 格式
 */
public class McpTool {
    private String name;
    private String description;
    private Object inputSchema;  // JSON Schema 格式的对象

    public McpTool() {}

    public McpTool(String name, String description, List<McpToolInput> inputs) {
        this.name = name;
        this.description = description;
        this.inputSchema = buildJsonSchema(inputs);
    }

    private Map<String, Object> buildJsonSchema(List<McpToolInput> inputs) {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", new LinkedHashMap<String, Object>());
        List<String> required = new ArrayList<>();

        if (inputs != null) {
            for (McpToolInput input : inputs) {
                Map<String, Object> prop = new LinkedHashMap<>();
                prop.put("type", input.getType());
                prop.put("description", input.getDescription());
                ((Map<String, Object>) schema.get("properties")).put(input.getName(), prop);
                if (input.isRequired()) {
                    required.add(input.getName());
                }
            }
        }

        if (!required.isEmpty()) {
            schema.put("required", required);
        }

        return schema;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Object getInputSchema() { return inputSchema; }
    public void setInputSchema(Object inputSchema) { this.inputSchema = inputSchema; }

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
