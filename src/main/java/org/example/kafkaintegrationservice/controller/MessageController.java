package org.example.kafkaintegrationservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.kafkaintegrationservice.event.OrderCreatedEvent;
import org.example.kafkaintegrationservice.producer.MessageProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageProducer messageProducer;

    @PostMapping
    public ResponseEntity<String> sendMessage(
            @RequestParam String key,
            @RequestBody OrderCreatedEvent message
    ) {
        messageProducer.sendMessage(key, message);
        return ResponseEntity.ok("Message sent to Kafka topic");
    }
}
