package com.apurva.chat.dm;

import com.apurva.chat.kafka.ChatEventProducer;
import com.apurva.chat.kafka.ChatMessageEvent;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/**
 * Receives a 1:1 message over WebSocket and PUBLISHES it as a Kafka event.
 *
 * Note what changed for event-driven architecture: this controller no longer
 * saves to the DB or delivers the message itself. It just fires an event and
 * returns immediately. The ChatEventConsumer handles persistence + delivery.
 */
@Controller
public class DmController {

    private final ChatEventProducer producer;

    public DmController(ChatEventProducer producer) {
        this.producer = producer;
    }

    @MessageMapping("/dm.send")
    public void send(DmPayload payload, Principal principal) {
        if (principal == null || payload.recipient() == null) {
            return;
        }
        boolean hasContent = payload.content() != null && !payload.content().isBlank();
        boolean hasAttachment = payload.attachmentUrl() != null && !payload.attachmentUrl().isBlank();
        if (!hasContent && !hasAttachment) {
            return;
        }

        producer.publish(new ChatMessageEvent(
                principal.getName(),
                payload.recipient(),
                payload.content() == null ? "" : payload.content(),
                payload.attachmentUrl(),
                payload.attachmentType(),
                payload.attachmentName(),
                System.currentTimeMillis()));
    }
}
