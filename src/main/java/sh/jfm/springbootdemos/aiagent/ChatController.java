package sh.jfm.springbootdemos.aiagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
    private static final String DEFAULT_PROMPT = "What day is tomorrow?";

    private final ChatModel chatModel;

    public ChatController(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    private static String useDefaultForNullOrEmpty(String prompt) {
        return (prompt == null || prompt.trim().isEmpty()) ? DEFAULT_PROMPT : prompt;
    }

    /// Endpoint to process date/time related queries using AI chat model.
    ///
    /// Example curl command:
    /// ```
    /// curl -X POST http://localhost:8080/agents/datetime \
    /// -H "Content-Type: text/plain" \
    /// -d "What is the current time in UTC?"
    ///```
    ///
    /// @param prompt The text prompt to process. If not provided, uses default prompt "What day is tomorrow?"
    /// @return The AI-generated response addressing the date/time query
    @PostMapping("datetime")
    public String datetime(@RequestBody(required = false) String prompt) {
        return ChatClient.create(chatModel)
                .prompt(useDefaultForNullOrEmpty(prompt))
                .tools(new DateTimeTools())
                .call()
                .content();
    }
}
