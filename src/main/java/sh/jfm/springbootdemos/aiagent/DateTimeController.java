package sh.jfm.springbootdemos.aiagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DateTimeController {
    private static final String DEFAULT_PROMPT = "What day is tomorrow?";

    private final ChatClient chatClient;

    public DateTimeController(ChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
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
    @PostMapping("datetime")
    public String datetime(@RequestBody(required = false) String prompt) {
        return chatClient
                .prompt(useDefaultForNullOrEmpty(prompt))
                .tools(new DateTimeTools())
                .call()
                .content();
    }

    /// Same as /datetime endpoint, but returns a ChatResponse object instead of a String.
    @PostMapping("datetime-full")
    public ChatResponse datetimeFull(@RequestBody(required = false) String prompt) {
        return chatClient
                .prompt(useDefaultForNullOrEmpty(prompt))
                .tools(new DateTimeTools())
                .call()
                .chatResponse();
    }
}
