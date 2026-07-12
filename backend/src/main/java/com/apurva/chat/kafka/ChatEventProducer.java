package com.apurva.chat.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * PRODUCER side of event-driven architecture: publishes a message event to Kafka.
 * The sender doesn't wait for the message to be saved/delivered — it just fires
 * the event and moves on (decoupling).
 */
@Service
public class ChatEventProducer {

    private final KafkaTemplate<String, ChatMessageEvent> kafkaTemplate;

    public ChatEventProducer(KafkaTemplate<String, ChatMessageEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ChatMessageEvent event) {
        // Key by recipient so all messages TO a person land on the same partition
        // (preserving their order).
        kafkaTemplate.send(KafkaTopics.CHAT_MESSAGES, event.recipient(), event);
    }
}
