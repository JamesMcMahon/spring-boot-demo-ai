package sh.jfm.springbootdemos.aiagent.adoption;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/// Simple example of using Spring AI for cat adoption agency
///
/// Adapted from https://spring.io/blog/2025/05/20/your-first-spring-ai-1
@Controller
@ResponseBody
public class AdoptionsController {
    private final ChatClient chatClient;

    AdoptionsController(
            ChatClient.Builder builder,
            CatAdoptionScheduler catAdoptionScheduler,
            PromptChatMemoryAdvisor promptChatMemoryAdvisor,
            VectorStore vectorStore
    ) {
        // Configure the mission statement for the assistant
        // This will prevent the assistant from being used as a general chatbot
        var systemPrompt = """
                You are an AI powered assistant to help people adopt a cat from the adoption agency named Tabby Road with locations in Rio de Janeiro, Mexico City, Seoul, Tokyo, Singapore, New York City, Amsterdam, Paris, Mumbai, New Delhi, Barcelona, London, and San Francisco.
                Information about the cats available will be presented below. If there is no information, then return a polite response suggesting we don't have any cats available.
                """;
        this.chatClient = builder
                .defaultTools(catAdoptionScheduler)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        promptChatMemoryAdvisor,
                        new QuestionAnswerAdvisor(vectorStore)
                )
                .build();
    }

    /// Endpoint to process adoption related queries using AI chat model.
    ///
    /// Example curl command:
    /// ```
    /// curl 'http://localhost:8080/james/assistant?question=do+you+have+any+gray+cats%3F'
    ///```
    ///
    /// Example xh command:
    /// ```
    /// xh :8080/james/assistant question=="do you have any gray cats?"
    ///```
    @GetMapping("/{user}/assistant")
    String inquire(@PathVariable String user, @RequestParam String question) {
        return chatClient
                .prompt()
                .user(question)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, user))
                .call()
                .content();
    }
}
