package org.example.kafkaintegrationservice.producer;

import lombok.RequiredArgsConstructor;
import org.example.kafkaintegrationservice.event.OrderCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageProducer {
    private static final String TOPIC = "kafka-topic-1";

    private final KafkaTemplate<String, OrderCreatedEvent> kafkaTemplate;

    public void sendMessage(String key, OrderCreatedEvent message) {
        kafkaTemplate.send(TOPIC, key, message);
    }
}
