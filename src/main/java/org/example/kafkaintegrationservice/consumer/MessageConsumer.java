package org.example.kafkaintegrationservice.consumer;

import org.example.kafkaintegrationservice.event.OrderCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {
    @KafkaListener(
        topics = "kafka-topic-1",
        groupId = "message-processing-group"
    )
    public void consume(OrderCreatedEvent event) {
        System.out.println("Consumed message: " + event);
    }
}
