package com.AIve.consumer.consumer;

import com.AIve.consumer.dto.Marketaux.BatchOfNews;
import com.AIve.consumer.dto.ResumedNews;
import com.AIve.consumer.dto.StockMapping;
import com.AIve.consumer.service.OllamaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class StockConsumer {

    @Autowired
    OllamaService ollamaService;

    @KafkaListener(topics = "portfolio-evaluations",containerFactory = "portfolioKafkaListenerContainerFactory", groupId = "portfolio-group")
    public void listenStockMapping(StockMapping stockMapping) {
        log.info("Received StockMapping: {}", stockMapping);
        String prompt = "Given the following stock portfolio, check risk exposure and stock balance, and return a list of tips to manage the portfolio: " + stockMapping.toString();
        String response = ollamaService.ask(prompt,Map.of());
        log.info("Ollama response: {}", response);
    }

    @KafkaListener(topics = "news-summaries",containerFactory = "newsKafkaListenerContainerFactory", groupId = "portfolio-group")
    public void listenResumedNews(BatchOfNews news) {
        Map<String,Object> options = Map.of("format","json","json_schema","{title:string,summary:string,stockName:string}");
        String prompt = "Summarize the following news article with he following structure {title:_, summary:_, stockName:_}: ";
        ResumedNews resumedNews = ollamaService.askWithSchema(prompt + news, ResumedNews.class);
        log.info("Ollama response: {}", resumedNews.summary());
    }
}