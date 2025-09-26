package com.AIve.consumer.config;

import com.AIve.consumer.dto.Stock;
import com.AIve.consumer.dto.StockMapping;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.RecoverableDataAccessException;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaConfig {

    @Autowired
    KafkaTemplate<String, StockMapping> kafkaTemplate;

    DeadLetterPublishingRecoverer publishingRecoverer() {
        return new DeadLetterPublishingRecoverer(kafkaTemplate, (r, e) -> {
            Throwable cause = e.getCause(); // unwrap
            if (e instanceof RecoverableDataAccessException) {
                log.info("Inside the IllegalArgumentException logic, sending to DLT");
                return new org.apache.kafka.common.TopicPartition("retry", r.partition());
            } else if (cause instanceof IllegalArgumentException) {
                log.info("Inside the IllegalArgumentException logic, sending to DLT");
                return new org.apache.kafka.common.TopicPartition("dlt-topic", r.partition());
            } else {
                log.info("Inside the non-recoverable logic, sending to DLT");
                return new org.apache.kafka.common.TopicPartition("dlt-topic", r.partition());

            }
        });
    }

    private DefaultErrorHandler errorHandler() {

        log.error("Configuring the Default Error Handler");
        var fixedBackOff = new FixedBackOff(1000L, 2); // wait for 1 second before retrying and retry 2 times
        var exceptionsToIgnore = List.of(IllegalArgumentException.class);//This data Skip and don't even retry
        DefaultErrorHandler defaultErrorHandler = new DefaultErrorHandler(publishingRecoverer(), fixedBackOff);
        exceptionsToIgnore.forEach(defaultErrorHandler::addNotRetryableExceptions);//Add this to the exception to ignore when handling errors
        return defaultErrorHandler;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StockMapping> kafkaListenerContainerFactory(
            ConsumerFactory<String, StockMapping> consumerFactory) {

        // Firstly configure the consumer properties with KafkaConsumerFactory (KCF)
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.87:9092,192.168.1.171:9092");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "postgres-consumer-group");

// Tell the deserializer which classes are trusted
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.AIve.consumer.dto");

// Correct format for type mappings
        props.put(JsonDeserializer.TYPE_MAPPINGS, "com.AIve.producer.dto.StockMapping:com.AIve.consumer.dto.StockMapping");

        DefaultKafkaConsumerFactory<String, StockMapping> customFactory = new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JsonDeserializer<>(StockMapping.class, false)
        );

        log.info("Custom KCF created");

        ConcurrentKafkaListenerContainerFactory<String, StockMapping> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(customFactory);
        factory.setCommonErrorHandler(errorHandler());

        return factory;
    }


}

