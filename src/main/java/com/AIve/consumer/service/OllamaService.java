package com.AIve.consumer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class OllamaService {

    private final ChatClient chatClient;

    public OllamaService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public <T> T askWithSchema(String prompt, Class<T> responseClass) {
        BeanOutputConverter<T> converter = new BeanOutputConverter<>(responseClass);
        log.info("Using converter with format: {}", converter.getFormat());
        log.info("Using converter with format: {}", converter.getFormat());
        log.info("Using converter with format: {}", converter.getFormat());
        String fullPrompt = prompt + "\n" + converter.getFormat();

        String rawResponse = chatClient.prompt()
                .user(fullPrompt)
                .call()
                .content();

        String cleanedResponse = rawResponse.replaceAll("(?s)<think>.*?</think>", "").trim();

        return converter.convert(cleanedResponse);
    }

    public String ask(String prompt, Map<String,Object> options) {
        return chatClient.prompt(prompt)
                .call()
                .content(); // get the response text
    }
}
