package sh.jfm.springbootdemos.aiagent.datetime;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpConfig {

    @Bean
    MethodToolCallbackProvider methodToolCallbackProvider() {
        return MethodToolCallbackProvider
                .builder()
                .toolObjects(new DateTimeTools())
                .build();
    }

    @Bean
    McpSyncClient mcpSyncClient(@Value("${mcp.client.uri}") String uri) {
        return McpClient
                .sync(HttpClientSseClientTransport.builder(uri).build())
                .build();
    }
}
