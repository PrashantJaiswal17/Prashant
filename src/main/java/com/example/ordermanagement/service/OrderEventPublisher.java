package com.example.ordermanagement.service;

import com.example.ordermanagement.events.OrderCreatedEvent;
import com.example.ordermanagement.events.OrderProcessedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String createdTopic;
    private final String processedTopic;

    public OrderEventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topics.order-created}") String createdTopic,
            @Value("${app.kafka.topics.order-processed}") String processedTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.createdTopic = createdTopic;
        this.processedTopic = processedTopic;
    }

    public void publishCreated(OrderCreatedEvent event) {
        kafkaTemplate.send(createdTopic, event.orderId().toString(), event);
    }

    public void publishProcessed(OrderProcessedEvent event) {
        kafkaTemplate.send(processedTopic, event.orderId().toString(), event);
    }
}
