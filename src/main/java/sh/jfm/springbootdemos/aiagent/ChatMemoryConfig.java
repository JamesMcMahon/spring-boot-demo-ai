package sh.jfm.springbootdemos.aiagent;

import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class ChatMemoryConfig {
    private static MessageWindowChatMemory buildMessageWindowChatMemory(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory
                .builder()
                .chatMemoryRepository(chatMemoryRepository)
                .build();
    }

    private static JdbcChatMemoryRepository buildChatMemoryRepository(DataSource dataSource) {
        return JdbcChatMemoryRepository
                .builder()
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public PromptChatMemoryAdvisor promptChatMemoryAdvisor(DataSource dataSource) {
        return PromptChatMemoryAdvisor
                .builder(buildMessageWindowChatMemory(buildChatMemoryRepository(dataSource)))
                .build();
    }
}
