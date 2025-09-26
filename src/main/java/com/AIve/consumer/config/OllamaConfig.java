package com.AIve.consumer.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OllamaConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder) {
        return chatClientBuilder
//                .defaultSystem("You are an AI Assistant that evaluates stock portfolios. "
//                        + "Given a portfolio, check risk exposure and stock balance, "
//                        + "and return a list of tips to manage the portfolio.")
                .build();
    }
}
