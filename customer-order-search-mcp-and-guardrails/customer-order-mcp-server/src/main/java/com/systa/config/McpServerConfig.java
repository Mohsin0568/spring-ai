package com.systa.config;

import com.systa.tools.CustomerOrderTool;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpServerConfig {

    @Bean
    List<ToolCallback> toolCallbackList(final CustomerOrderTool customerOrderTool){
        return List.of(ToolCallbacks.from(customerOrderTool));
    }
}
