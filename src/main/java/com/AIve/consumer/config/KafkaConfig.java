package com.AIve.consumer.config;

import com.AIve.consumer.dto.Marketaux.BatchOfNews;
import com.AIve.consumer.dto.StockMapping;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    // Base consumer config
    private Map<String, Object> baseConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.AIve.consumer.dto.*");
        return props;
    }

    @Bean
    public ConsumerFactory<String, StockMapping> portfolioConsumerFactory() {
        Map<String, Object> props = baseConsumerConfigs();
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, StockMapping.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 10);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StockMapping> portfolioKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, StockMapping> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(portfolioConsumerFactory());
        factory.setConcurrency(3); // Different concurrency
        return factory;
    }

    @Bean
    public ConsumerFactory<String, BatchOfNews> newsConsumerFactory() {
        Map<String, Object> props = baseConsumerConfigs();
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, BatchOfNews.class);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 50);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, BatchOfNews> newsKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, BatchOfNews> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(newsConsumerFactory());
        factory.setConcurrency(5); // Different concurrency
        factory.setBatchListener(true); // Enable batch processing if needed
        return factory;
    }
}