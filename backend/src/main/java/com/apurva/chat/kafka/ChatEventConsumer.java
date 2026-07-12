package com.apurva.chat.kafka;

import com.apurva.chat.dm.DirectMessage;
import com.apurva.chat.dm.DirectMessageDto;
import com.apurva.chat.dm.DirectMessageRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * CONSUMER side of event-driven architecture: listens to the Kafka topic and,
 * for each message event, (1) saves it to PostgreSQL and (2) delivers it over
 * WebSocket to the recipient and the sender.
 *
 * This is decoupled from the sender — we could add more consumers later (e.g. a
 * push-notification service, analytics) without touching the send path.
 */
@Service
public class ChatEventConsumer {

    private final DirectMessageRepository repo;
    private final SimpMessagingTemplate messaging;

    public ChatEventConsumer(DirectMessageRepository repo, SimpMessagingTemplate messaging) {
        this.repo = repo;
        this.messaging = messaging;
    }

    @KafkaListener(topics = KafkaTopics.CHAT_MESSAGES, groupId = "chat-service")
    public void onChatMessage(ChatMessageEvent event) {
        Instant timestamp = Instant.ofEpochMilli(event.timestamp());
        String content = event.content() == null ? "" : event.content();

        DirectMessage entity = new DirectMessage(event.sender(), event.recipient(), content, timestamp);
        entity.setAttachmentUrl(event.attachmentUrl());
        entity.setAttachmentType(event.attachmentType());
        entity.setAttachmentName(event.attachmentName());
        repo.save(entity);

        DirectMessageDto dto = new DirectMessageDto(event.sender(), event.recipient(), content, timestamp,
                event.attachmentUrl(), event.attachmentType(), event.attachmentName());
        messaging.convertAndSendToUser(event.recipient(), "/queue/messages", dto);
        messaging.convertAndSendToUser(event.sender(), "/queue/messages", dto);
    }
}
