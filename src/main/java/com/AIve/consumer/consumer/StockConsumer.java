package com.AIve.consumer.consumer;

import com.AIve.consumer.dto.StockMapping;
import com.AIve.consumer.service.OllamaService;
import com.AIve.consumer.service.PostgresService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(topics = "portfolio-evaluations", groupId = "postgres-consumer-group")
public class StockConsumer {

    @Autowired
    OllamaService ollamaService;

    @KafkaHandler
    public void listenStockMapping(StockMapping stockMapping) {
        log.info("Received StockMapping: {}", stockMapping);
        String prompt = "Given the following stock portfolio, check risk exposure and stock balance, and return a list of tips to manage the portfolio: " + stockMapping.toString();
        String response = ollamaService.ask(prompt);
        log.info("Ollama response: {}", response);
    }
}
