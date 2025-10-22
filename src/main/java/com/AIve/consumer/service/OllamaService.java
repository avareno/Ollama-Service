package com.AIve.consumer.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class OllamaService {

    private final ChatClient chatClient;

    public OllamaService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public <T> T askWithSchema(String prompt, Class<T> responseClass) {
        BeanOutputConverter<T> converter = new BeanOutputConverter<>(responseClass);

        String fullPrompt = prompt + "\n\n" + converter.getFormat();

        return chatClient.prompt()
                .user(fullPrompt)
                .call()
                .entity(responseClass);
    }

    public String ask(String prompt, Map<String,Object> options) {
        return chatClient.prompt(prompt)
                .call()
                .content(); // get the response text
    }
}
