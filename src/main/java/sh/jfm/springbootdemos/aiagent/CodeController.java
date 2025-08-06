package sh.jfm.springbootdemos.aiagent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CodeController {

    private final ChatClient chatClient;

    public CodeController(
            ChatModel chatModel,
            @Value("${git.basepath}") String gitBasePath
    ) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultTools(new GitTools(gitBasePath))
                .build();
    }

    @PostMapping("code")
    public String promptCode(@RequestBody String prompt) {
        return chatClient.prompt(prompt).call().content();
    }
}
