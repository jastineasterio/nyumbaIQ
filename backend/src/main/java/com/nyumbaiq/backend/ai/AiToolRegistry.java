package com.nyumbaiq.backend.ai;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AiToolRegistry {
    private final Map<String, AiTool> tools = new LinkedHashMap<>();

    public AiToolRegistry(List<AiTool> toolBeans) {
        for (AiTool tool : toolBeans) {
            tools.put(tool.name(), tool);
        }
    }

    public AiTool get(String name) {
        AiTool tool = tools.get(name);
        if (tool == null) {
            throw new IllegalArgumentException("Unknown AI tool: " + name);
        }
        return tool;
    }

    public List<AiTool> getAll() {
        return new ArrayList<>(tools.values());
    }

    public List<Map<String, Object>> getToolDefinitions() {
        List<Map<String, Object>> definitions = new ArrayList<>();
        for (AiTool tool : tools.values()) {
            Map<String, Object> definition = new LinkedHashMap<>();
            definition.put("name", tool.name());
            definition.put("description", tool.description());
            definition.put("parameters", Map.of(
                    "type", "object",
                    "properties", Map.of(),
                    "required", tool.requiredParameters()
            ));
            definitions.add(definition);
        }
        return definitions;
    }
}
