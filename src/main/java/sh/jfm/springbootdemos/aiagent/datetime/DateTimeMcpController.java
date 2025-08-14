package sh.jfm.springbootdemos.aiagent.datetime;

import io.modelcontextprotocol.client.McpSyncClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DateTimeMcpController {
    private static final String DEFAULT_PROMPT = "What day is tomorrow?";

    private final Logger log = LoggerFactory.getLogger(DateTimeMcpController.class);
    private final ChatClient chatClient;
    private final McpSyncClient mcpClient;

    public DateTimeMcpController(
            ChatModel chatModel,
            McpSyncClient mcpClient
    ) {
        this.chatClient = ChatClient.create(chatModel);
        this.mcpClient = mcpClient;
    }

    private static String useDefaultForNullOrEmpty(String prompt) {
        return (prompt == null || prompt.trim().isEmpty()) ? DEFAULT_PROMPT : prompt;
    }

    /// Endpoint to process date/time related queries using AI chat model.
    ///
    /// Example curl command:
    /// ```
    /// curl -X POST http://localhost:8080/datetime \
    /// -H "Content-Type: text/plain" \
    /// -d "What is the current time in UTC?"
    ///```
    ///
    /// @param prompt The text prompt to process. If not provided, uses default prompt "What day is tomorrow?"
    /// @return The AI-generated response addressing the date/time query
    @PostMapping("datetime-mcp")
    public String datetime(@RequestBody(required = false) String prompt) {
        if (!mcpClient.isInitialized()) {
            log.info("Initializing MCP client...");
            mcpClient.initialize();
        }

        return chatClient
                .prompt(useDefaultForNullOrEmpty(prompt))
                .tools(new SyncMcpToolCallbackProvider(mcpClient))
                .call().content();
    }

}